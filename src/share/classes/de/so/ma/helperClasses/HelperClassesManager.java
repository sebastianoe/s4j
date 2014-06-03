package de.so.ma.helperClasses;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import de.so.ma.helperClasses.filetemplates.FileTemplate;
import de.so.ma.helperClasses.filetemplates.SqlConnectionManagerTemplate;

public class HelperClassesManager {

	private List<JavaFileObject> fileObjects;
	private Context context;
	private boolean enabled = true;

	public HelperClassesManager(List<JavaFileObject> fileObjects,
			Context context) {
		this.fileObjects = fileObjects;
		this.context = context;
	}

	public List<JavaFileObject> withAddedHelperClasses() {
		if (!enabled) {
			return fileObjects;
		}
		
		FileTemplate fileTemplate = new SqlConnectionManagerTemplate();
		JavaFileManager jfm = context.get(JavaFileManager.class);

		try {
			if (jfm instanceof StandardJavaFileManager) {
				StandardJavaFileManager sjfm = (StandardJavaFileManager) jfm;
				File tmpFile = File.createTempFile("tmp-", ".tmp");
				File baseDir = tmpFile.getParentFile();
//				File packageFolder = new File(baseDir, "jsql/"
//						+ fileTemplate.getPackageName().replaceAll("\\.", "/"));
				File packageFolder = new File(".", fileTemplate.getPackageName().replaceAll("\\.", "/"));
				if (!packageFolder.exists()) {
					packageFolder.mkdirs();
				}
				File classFile = new File(packageFolder,
						fileTemplate.getClassName() + ".java");

				BufferedWriter out = new BufferedWriter(new FileWriter(
						classFile));
				out.write(fileTemplate.getFileContent());
				out.close();

				Iterable<? extends JavaFileObject> jfos = sjfm
						.getJavaFileObjectsFromFiles(List.<File> of(classFile));
				List<JavaFileObject> jfosList = List.<JavaFileObject> of(jfos
						.iterator().next());
				return fileObjects.prependList(jfosList);
			}
		} catch (IOException e) {
			// tbd: better exception handling
			throw new RuntimeException(e);
		}

		return fileObjects;
	}
}
