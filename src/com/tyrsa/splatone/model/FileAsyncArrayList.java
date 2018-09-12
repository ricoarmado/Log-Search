package com.tyrsa.splatone.model;

import java.util.concurrent.CopyOnWriteArrayList;

public class FileAsyncArrayList  {
	
	private static FileAsyncArrayList instance;
	
	private CopyOnWriteArrayList<FileContainer> files;
		
	private FileAsyncArrayList() {
		super();
		files = new CopyOnWriteArrayList<>();
	}

	public static FileAsyncArrayList getInstance() {
		if(instance == null) {
			instance = new FileAsyncArrayList();
		}
		return instance;
	}
	
	public void add(FileContainer wrapper) {
		files.addIfAbsent(wrapper);
	}

}
