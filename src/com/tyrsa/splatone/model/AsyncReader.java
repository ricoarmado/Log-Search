package com.tyrsa.splatone.model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AsyncReader {

	public static boolean read(String path, String lex, Tree node) throws IOException, InterruptedException {
		ExecutorService pool = new ScheduledThreadPoolExecutor(3);
	    AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
	    Paths.get(path), EnumSet.of(StandardOpenOption.READ),
	        pool);
	    CompletionHandler<Integer, ByteBuffer> handler = new CompletionHandler<Integer, ByteBuffer>() {
	      @Override
	      public synchronized void completed(Integer result, ByteBuffer attachment) {
	        for (int i = 0; i < attachment.limit(); i++) {
	          System.out.println((char) attachment.get(i));
	        }
	      }
	      @Override
	      public void failed(Throwable e, ByteBuffer attachment) {
	      }
	    };
	    final int bufferCount = 5;
	    ByteBuffer buffers[] = new ByteBuffer[bufferCount];
	    for (int i = 0; i < bufferCount; i++) {
	      buffers[i] = ByteBuffer.allocate(10);
	      fileChannel.read(buffers[i], i * 10, buffers[i], handler);
	    }
	    pool.awaitTermination(1, TimeUnit.SECONDS);
	    String result = "";
	    for (ByteBuffer byteBuffer : buffers) {
	      for (int i = 0; i < byteBuffer.limit(); i++) {
	    	  //System.out.print((char) byteBuffer.get(i));
	    	  result += (char)byteBuffer.get(i);
	      }
	    }
	    result.trim();
	    if(result.contains(lex)) {
	    	
	    	
	    	Tree chain = new Tree(null);
	    	chain.setText(result);
	    	chain.setNode(new File(path));
	    	node.addNode(chain);
	    	return true;
	    }
	    return false;
	}
	
	public static void run(String path, String lex, String type, WriteToUIInterface out) throws IOException, InterruptedException {
		File currentDir = new File(path);
		Tree root = null;
		if(currentDir == null && !currentDir.isDirectory()) {
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
		Tree chain = new Tree(null);
		boolean found = false;
		for(File current : dirFiles) {
			if(current.isDirectory()) {			
				chain.setParent(node);
				chain.setNode(current);
				node.addNode(chain);
				traversal(current,lex, type, chain);
			}
			else if(getFileExtension(current).equals(type)){
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
