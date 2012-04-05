package com.bertvanbrakel.test.finder;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Objects;

public class ClassPathResource  {

	private final ClassPathRoot classPathRoot;
	private final File file;
	private final String relPath;
	private final int depth;
	private final boolean fromArchive;
	
	public ClassPathResource(ClassPathRoot classPathRoot, File file, String relPath, boolean fromArchive) {
		this.classPathRoot = classPathRoot;
		this.file = file;
		this.relPath = relPath;
		this.depth = countForwardSlashes(relPath);
		this.fromArchive = fromArchive;
	}
	
	private static int countForwardSlashes(String s){
		int count = 0;
		for( int i = 0; i < s.length(); i++){
			if( s.charAt(i) == '/'){
				count++;
			}
		}
		return count;
	}

	public File getFile() {
		return file;
	}
	
	public ClassPathRoot getClassPathRoot() {
		return classPathRoot;
	}

	public int getDepth() {
    	return depth;
    }
	
	public boolean isDir(){
		return file != null && file.isDirectory();
	}
	
	public boolean isArchiveEntry(){
		return fromArchive;
	}
	
	public String getRelPath() {
		return relPath;
	}

	public String getPackagePart(){
		int slash = relPath.lastIndexOf('/');
		if( slash != -1){
			String dottified = relPath.substring(0, slash).replace('/', '.');	
			if (dottified.charAt(0) == '.') {
				dottified = dottified.substring(1);
			}
			return dottified;
		}
		return null;
	}
	
	public String getBaseFileNamePart(){
		return FilenameUtils.getBaseName(relPath);
	}
	
	public String getPathWithoutExtension(){
		String ext = getExtension();
		if( ext != null ){
			return relPath.substring(0,relPath.length() - ext.length() - 1);
		}
		return relPath;
	}
	
	public boolean hasExtension(String extension){
		return extension.equals(getExtension());
	}

	public boolean hasExtensionIgnoreCase(String extension){
		return extension.toLowerCase().equals(getExtension());
	}
	
	public String getExtension(){
		return isDir()?null:FilenameUtils.getExtension(getRelPath());
	}
	
	@Override
	public String toString(){
		return Objects
			.toStringHelper(this)
			.add("classPathRoot", classPathRoot)
			.add("relPath", relPath)
			.add("depth",depth)
			.add("extension",getExtension())
			.add("fromArchive", isArchiveEntry())
			.toString();	
	}
}