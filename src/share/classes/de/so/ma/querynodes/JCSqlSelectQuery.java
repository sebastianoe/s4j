package de.so.ma.querynodes;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.codegenerator.specifics.SpecificSelectCodeGenerator;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.ExpectedTypeHintException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.parser.QueryTypeExtractor;
import de.so.ma.parser.SelectQueryParser;
import de.so.ma.types.TypesHelper;
import de.so.ma.validation.QueryValidator;
import de.so.ma.validation.SelectQueryValidator;

public class JCSqlSelectQuery extends JCSqlQuery {
	private QueryTypeExtractor queryTypeExtractor;
	
	protected JCSqlSelectQuery(String queryString, JCExpression expectedTypeHint, Log log) {
		super(queryString, expectedTypeHint, log);
	}
	
	private void fixExpectedType(Log log, Attr attr, Env<AttrContext> env) throws ExpectedTypeHintException {
		TypesHelper typesHelper = TypesHelper.getInstance();
		
		if (typesHelper.isNoneType(expectedType)) {
			JCExpression expectedTypeHint = getExpectedTypeHint();
			if (expectedTypeHint == null) {
				log.error("sql.need.expected.type.hint");
				throw new ExpectedTypeHintException();
			} else {
				this.expectedType = attr.attribType(expectedTypeHint, env);
			}
		}
	}
	
	@Override
	protected void parse() throws NoSuchTableException, AmbiguousCoalesceException {
		SelectQueryParser parser = new SelectQueryParser(this, log);
		parser.parse();
		queryTypeExtractor = parser.getQueryTypeExtractor();
	}

	@Override
	protected void performQueryCallMethodSpecifics(Env<AttrContext> env, Attr attr) throws ExpectedTypeHintException {
		fixExpectedType(validator.getLog(), attr, env);
		
		// check that the expected type is compatible with the query's projection
		SelectQueryValidator selectQueryValidator = (SelectQueryValidator) validator;
		// tbd: defer extractor type logic up to this point
		selectQueryValidator.validateProjectionCompatibility(queryTypeExtractor.getProjections(), attr, env, this);
	}

	@Override
	protected QueryValidator getValidator() {
		return new SelectQueryValidator(log, this);
	}

	@Override
	public SpecificCodeGenerator getSpecificCodeGenerator() {
		return new SpecificSelectCodeGenerator(this);
	}


	@Override
	public Type getExpectedType() {
		return expectedType;
	}
}
