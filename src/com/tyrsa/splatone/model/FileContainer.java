package com.tyrsa.splatone.model;

import java.io.File;

public class FileContainer {
	
	private File file;
	
	private String content;
	
	public FileContainer(File file, String content) {
		super();
		this.file = file;
		this.content = content;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getContent() {
		return content;
	}
}
