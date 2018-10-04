package com.tyrsa.splatone.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AsyncReader {
	
	public static boolean read(String path, String lex, Tree node) throws IOException, InterruptedException {
		String result = "";
		boolean found = false;
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		while((result = reader.readLine())!= null) {
			if(result.contains(lex)) {
				found = true;
				break;
			}
		}
		reader.close();
	    if(found) {
	    	Tree chain = new Tree(null);
	    	chain.setNode(new File(path));
	    	node.addNode(chain);
	    	return true;
	    }
		return false;
	}

	public static void run(String path, String lex, String type, WriteToUIInterface out) throws IOException, InterruptedException {
		File currentDir = new File(path);
		Tree root = null;
		if(!currentDir.isDirectory()) {
			throw new FileNotFoundException();
		}
		else {
			Tree chain = new Tree(currentDir);
			traversal(currentDir, lex, type, chain);
			root = chain;
		}
		out.write(root);
	}
	
	public static void traversal(File currentDir, String lex, String type, Tree node) throws IOException, InterruptedException {
		File[] dirFiles = currentDir.listFiles();
		
		boolean found = false;
		for(File current : dirFiles) {
			if(current.isDirectory()) {
				Tree chain = new Tree(null);
				chain.setParent(node);
				chain.setNode(current);
				node.addNode(chain);
				traversal(current,lex, type, chain);
			}
			else if(getFileExtension(current).equals(type)){
				Tree chain = new Tree(null);
				boolean read = read(current.getAbsolutePath(), lex, chain);
				if(read) {					
					chain.setParent(node);
					chain.setNode(current);
					node.addNode(chain);
					found = true;
				}
			}
		}
		if(!found) {
			Tree parent = node.getParent();
			if(parent != null) {
				parent.remove(node);
			}
		}
	}
	private static String getFileExtension(File file) {
        String extension = "";
 
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
 
        return extension;
 
    }

}
