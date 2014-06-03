package de.so.ma.validation.matching;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Log;

import de.so.ma.data.DummyProjectionItem;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.NoIdAssignmentException;
import de.so.ma.exceptions.NonPreferencesMappingException;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.util.MapPrinter;
import de.so.ma.util.StateHelper;
import de.so.ma.util.TablePrinter;
import de.so.ma.validation.matching.flatobject.DummyAttribute;
import de.so.ma.validation.matching.flatobject.FlatObject;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;
import de.so.ma.validation.matching.voters.StrictVoter;
import de.so.ma.validation.matching.voters.Voter;

public class ProjectionMatcher {
	public BiMap<ProjectionItem, FlatObjectAttribute> getMatching(
			Type expectedType, 
			List<ProjectionItem> projections,
			JCSqlQuery sqlQuery,
			Log log) {
		FlatObject flatObject = flattenObject(expectedType);
		sqlQuery.setFlatObject(flatObject);
		
		int numberOfObjectParts = flatObject.getAttributeCardinality();
		int numberOfProjections = projections.size();

		projections.addAll(addDummyProjections(numberOfObjectParts - numberOfProjections));
		flatObject.getAttributes().addAll(addDummyAttributes(numberOfProjections - numberOfObjectParts));
		
		assert flatObject.getAttributeCardinality() == projections.size();
		
		Table<ProjectionItem, FlatObjectAttribute, Integer> votings =
				calculateVotings(projections, flatObject);
		
		BiMap<ProjectionItem, FlatObjectAttribute> mapping = null;
		
		try {
			mapping = match(votings);
			if (!StateHelper.usingJtreg()) {
//				printVotings(votings);
				printMapping(mapping, votings);
			}
			
			validateNoNonPreferences(votings, mapping);
			
			/* 
			 * id assignment is disable as an explicit identity is not necessarily required
			 * (though recommended) 
			 */
//			validateIdAssignment(votings, mapping);
		} catch (NonPreferencesMappingException e) {
			log.error("sql.mapping.non.validation");
			mapping = HashBiMap.create();
		} catch (NoIdAssignmentException e) {
			String joinedObjects = Joiner.on(", ").join(e.getSubObjects());
			log.error("sql.mapping.no.id.assignment", joinedObjects);
			mapping = HashBiMap.create();
		}
		
		return mapping;
	}

	/**
	 * Gale-Shapley Algorithm for the stable marriage problem 
	 * @param votings
	 * @return
	 * @throws NonPreferencesMappingException 
	 * @throws NoIdAssignmentException 
	 */
	private BiMap<ProjectionItem, FlatObjectAttribute> match(
			final Table<ProjectionItem, FlatObjectAttribute, Integer> votings) throws NonPreferencesMappingException, NoIdAssignmentException {
		
		BiMap<ProjectionItem, FlatObjectAttribute> mapping = HashBiMap.create();
		
		// algorithm, see: http://en.wikipedia.org/wiki/Stable_marriage_problem#Algorithm
		// Initialize all m in M and w in W to free
		Queue<ProjectionItem> freeProjections = Lists.newLinkedList(votings.rowKeySet());
		
		// iterate over projections queue
		while (!freeProjections.isEmpty()) {
			// get first free projection
			ProjectionItem thisProjection = freeProjections.poll();
			
			// get projection's sorted preferences
			Map<FlatObjectAttribute, Integer> projectionRow = votings.row(thisProjection);
			List<FlatObjectAttribute> thisProjectionPrefers = 
					Ordering
						.natural()
						.reverse()
						.onResultOf(Functions.forMap(projectionRow))
						.sortedCopy(projectionRow.keySet());
			
			for (FlatObjectAttribute attribute : thisProjectionPrefers) {
				if (!mapping.containsValue(attribute)) {
					// attribute is free
					mapping.put(thisProjection, attribute);
					break;
				} else {
					// attribute isn't free anymore
					ProjectionItem otherProjection = mapping.inverse().get(attribute);
					
					int thisPreference = votings.get(thisProjection, attribute);
					int otherPreference = votings.get(otherProjection, attribute);
					
					if (thisPreference > otherPreference) {
						mapping.remove(otherProjection);
						freeProjections.add(otherProjection);
						
						mapping.put(thisProjection, attribute);
						break;
					}
					
					// else: otherProjection and attribute remain mapped, next attribute for thisProjection is evaluated
				}
			}
		}
		
		filterDummyMappings(votings, mapping);
		
		return mapping;
	}

	/**
	 * Validate that: each sub-object gets assigned an id projection item, 
	 * so that we have an identity for each object
	 * @param votings
	 * @param mapping
	 * @throws NoIdAssignmentException 
	 */
	private void validateIdAssignment(Table<ProjectionItem, FlatObjectAttribute, Integer> votings,
			BiMap<ProjectionItem, FlatObjectAttribute> mapping) throws NoIdAssignmentException {
		
		if (anyProjectionWithTable(votings)) {
			Set<String> attributePaths = Sets.newHashSet("root");
			
			for (FlatObjectAttribute att : mapping.values()) {
				if (att.getParentObject() != null) {
					attributePaths.add(att.getParentObject().getFullPath());
				}
			}
			
			for (Entry<ProjectionItem, FlatObjectAttribute> entry : mapping.entrySet()) {
				ProjectionItem pi = entry.getKey();
				FlatObjectAttribute att = entry.getValue();
				
				if (pi.isId()) {
					attributePaths.remove(att.getParentObject().getFullPath());
				}
			}
			
			if (!attributePaths.isEmpty()) {
				throw new NoIdAssignmentException(attributePaths);
			}
			
			System.out.println();
		}
	}

	private boolean anyProjectionWithTable(Table<ProjectionItem, FlatObjectAttribute, Integer> votings) {
		for (ProjectionItem pi : votings.rowKeySet()) {
			if (pi.getBaseTable() != null) {
				return true;
			}
		}
		
		return false;
	}

	private void filterDummyMappings(final Table<ProjectionItem, FlatObjectAttribute, Integer> votings,
			BiMap<ProjectionItem, FlatObjectAttribute> mapping) {
		// filter out dummy items, these will not be assigned
		Maps.filterKeys(mapping, Predicates.instanceOf(DummyProjectionItem.class)).clear();
		Maps.filterValues(mapping, Predicates.instanceOf(DummyAttribute.class)).clear();
	}

	private void validateNoNonPreferences(
			final Table<ProjectionItem, FlatObjectAttribute, Integer> votings,
			BiMap<ProjectionItem, FlatObjectAttribute> mapping) throws NonPreferencesMappingException {
		Predicate<Entry<ProjectionItem, FlatObjectAttribute>> nonPreferencePredicate = 
				new Predicate<Map.Entry<ProjectionItem,FlatObjectAttribute>>() {
					@Override		
					public boolean apply(Entry<ProjectionItem, FlatObjectAttribute> input) {
						return votings.get(input.getKey(), input.getValue()) == 0;
						
					}
				};
		Map<ProjectionItem, FlatObjectAttribute> nonPreferences = 
				Maps.filterEntries(mapping, nonPreferencePredicate);
		
		if (!nonPreferences.isEmpty()) {
			throw new NonPreferencesMappingException();
		}
	}

	private Table<ProjectionItem, FlatObjectAttribute, Integer> calculateVotings(
			List<ProjectionItem> projections,
			FlatObject flattenedObject) {
		Table<ProjectionItem, FlatObjectAttribute, Integer> votings = HashBasedTable.create();
		
		Voter voter = new StrictVoter();
		
		// calculate the ratings for each combination of object part and projection
		for (FlatObjectAttribute attribute : flattenedObject.getFlatAttributes()) {
			for (ProjectionItem projection : projections) {
				int voting = voter.calculateVoting(projection, attribute);
				votings.put(projection, attribute, voting);
			}
		}
		
		return votings;
	}

	private List<ProjectionItem> addDummyProjections(int numberRequired) {
		List<ProjectionItem> dummies = Lists.newArrayList();
		
		for (int i = 0; i < numberRequired; i++) {
			dummies.add(new DummyProjectionItem());
		}
		
		return dummies;
	}

	private Collection<? extends FlatObjectAttribute> addDummyAttributes(int numberRequired) {
		List<DummyAttribute> dummies = Lists.newArrayList();
		
		for (int i = 0; i < numberRequired; i++) {
			dummies.add(new DummyAttribute());
		}
		
		return dummies;
	}

	/**
	 * Create list of object parts, based on the setters of an object
	 * @return
	 */
	private FlatObject flattenObject(Type expectedType) {
		return new FlatObject(expectedType);
	}

	private void printMapping(BiMap<ProjectionItem, FlatObjectAttribute> mapping, 
			Table<ProjectionItem, FlatObjectAttribute, Integer> votings) {
		System.out.println("Based on the GS-Algorithm, got the following filtered mapping:");
		MapPrinter<ProjectionItem, FlatObjectAttribute> mapPrinter = 
				new MapPrinter<ProjectionItem, FlatObjectAttribute>();
		mapPrinter.printMap(mapping, votings);
		System.out.println();
	}

	private void printVotings(Table<ProjectionItem, FlatObjectAttribute, Integer> votings) {
		System.out.println("Calculated the following matching preferences:");
		TablePrinter<ProjectionItem, FlatObjectAttribute, Integer> tablePrinter =
				new TablePrinter<ProjectionItem, FlatObjectAttribute, Integer>();
		tablePrinter.printTable(votings);
		System.out.println();
	}
}
