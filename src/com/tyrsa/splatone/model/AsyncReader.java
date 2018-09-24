package com.tyrsa.splatone.model;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AsyncReader {
	
	public static String read(String path, String lex, Tree node) throws IOException, InterruptedException {
		String result = "";
		Stream<String> lines = Files.lines(Paths.get(path), Charset.forName("windows-1251"));
		List<String> allLines = lines.collect(Collectors.toList());
		lines.close();
		result = String.join("\n", allLines);
		result.trim();
	    if(result.contains(lex)) {
	    	
	    	
	    	Tree chain = new Tree(null);
	    	chain.setText(result);
	    	chain.setNode(new File(path));
	    	node.addNode(chain);
	    	return result;
	    }
		return null;
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
				String read = read(current.getAbsolutePath(), lex, chain);
				if(read != null) {					
					chain.setParent(node);
					chain.setNode(current);
					chain.setText(read);
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
