package com.bertvanbrakel.test.finder;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bertvanbrakel.test.finder.Root.TYPE;
import com.bertvanbrakel.test.util.ProjectFinder;
import com.bertvanbrakel.test.util.ProjectResolver;

public class ClassPathBuilder {
	
	private final Map<String,Root> classPathsRoots = newLinkedHashMap();
	
	private ProjectResolver projectResolver;

	private boolean includeClassesDir = true;
	private boolean includeTestDir = false;
	private boolean includeClasspath = false;
	private boolean includeGeneratedDir = false;
	

	public static ClassPathBuilder newBuilder(){
		return new ClassPathBuilder();
	}
	
	private ClassPathBuilder(){
		//prevent instantiation outside of builder method
	}
	
	public List<Root> build(){
		ProjectResolver resolver = toResolver();
		
		ClassPathBuilder copy = new ClassPathBuilder();
		copy.classPathsRoots.putAll(classPathsRoots);
		if (includeClassesDir) {
			copy.addClassPaths(resolver.getMainCompileTargetDirs(),TYPE.MAIN_COMPILE);
		}
		if (includeTestDir) {
			copy.addClassPaths(resolver.getTestCompileTargetDirs(),TYPE.TEST_COMPILE);
		}
		if (includeGeneratedDir) {
			copy.addClassPaths(resolver.getGeneratedCompileTargetDirs(),TYPE.GENERATED_COMPILE);
		}
		if (includeClasspath) {
			copy.addClassPaths(findClassPathDirs());
		}
		return newArrayList(copy.classPathsRoots.values());
	}
	
	private ProjectResolver toResolver(){
		return projectResolver!=null?projectResolver:ProjectFinder.getDefaultResolver();
	}
	
	public ClassPathBuilder copyOf(){
		ClassPathBuilder copy = new ClassPathBuilder();
		copy.projectResolver = projectResolver;
		copy.includeClassesDir = includeClassesDir;
		copy.includeClasspath = includeClasspath;
		copy.includeGeneratedDir = includeGeneratedDir;
		copy.includeTestDir = includeTestDir;
		copy.classPathsRoots.putAll(classPathsRoots);
		return copy;
	}
	
	private Set<File> findClassPathDirs() {
		Set<File> files = newLinkedHashSet();

		String classpath = System.getProperty("java.class.path");
		String sep = System.getProperty("path.separator");
		String[] paths = classpath.split(sep);

		Collection<String> fullPathNames = newArrayList();
		for (String path : paths) {
			try {
				File f = new File(path);
				if (f.exists() & f.canRead()) {
					String fullPath = f.getCanonicalPath();
					if (!fullPathNames.contains(fullPath)) {
						files.add(f);
						fullPathNames.add(fullPath);
					}
				}
			} catch (IOException e) {
				throw new ClassFinderException("Error trying to resolve pathname " + path);
			}
		}
		return files;
	}	
	
	public ClassPathBuilder setProjectResolver(ProjectResolver resolver){
		this.projectResolver = resolver;
		return this;
	}
	
	public ClassPathBuilder addClassPathDir(String path) {
    	addClassPathDir(new File(path));
    	return this;
    }

	public ClassPathBuilder addClassPaths(Collection<File> paths) {
		for( File path:paths){
			addClassPathDir(path);
		}
    	return this;
    }
	
	public ClassPathBuilder addClassPaths(Collection<File> paths, TYPE type) {
		for( File path:paths){
			addClassPath(new ClassPathRoot(path,type));
		}
    	return this;
    }
	
	public ClassPathBuilder addClassPathDir(File path) {
		addClassPath(new ClassPathRoot(path,TYPE.UNKNOWN));
    	return this;
    }

	public ClassPathBuilder addClassPaths(Iterable<Root> roots) {
		for(Root root:roots){
			addClassPath(root);
		}
		return this;
	}
	
	public ClassPathBuilder addClassPath(Root root) {
		String key = root.getPathName();
		if (root.isTypeKnown() || !classPathsRoots.containsKey(key)) {
			classPathsRoots.put(key, root);
		}
		return this;
	}
	
	public ClassPathBuilder setIncludeClassesDir(boolean b) {
		this.includeClassesDir = b;
		return this;
	}

	public ClassPathBuilder setIncludeTestDir(boolean b) {
		this.includeTestDir = b;
		return this;
	}

	public ClassPathBuilder setIncludeClasspath(boolean b) {
    	this.includeClasspath = b;
    	return this;
    }
}