package com.abbajoa.thumbnailmirror;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {

	public static void visitAllFiles(File dir, Visitor<File, IOException> visitor) throws IOException, FileNotFoundException {
		File[] list = dir.listFiles();
		if(list != null) {
			for(File sub : list) {
				if(sub.isFile())
					visitor.visit(sub);
				else
					visitAllFiles(sub, visitor);			
			}
		}
	}

	public static void visitAllSubDirectories(File dir, Visitor<File, IOException> visitor) throws IOException, FileNotFoundException {
		File[] list = dir.listFiles();
		if(list != null) {
			for(File sub : list) {
				if(sub.isDirectory()) {
					visitor.visit(sub);
					visitAllSubDirectories(sub, visitor);
				}
			}
		}
	}

	public static void ensureDirectoryExist(File dir) throws IOException {
		if(!dir.exists()) {
			boolean success = dir.mkdirs();
			if(!success)
				throw new IOException("Cannot create directory");
		} else {
			if(!dir.isDirectory())
				throw new IOException(dir.getName() + " is not a directory");
		}
	}

	public static String getRelativePath(File parent, File file) {
		return file.getAbsolutePath().substring(parent.getAbsolutePath().length()+1);
	}

}
