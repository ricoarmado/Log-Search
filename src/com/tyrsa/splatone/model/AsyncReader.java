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
	    	chain.setParent(node);
	    	node.addNode(chain);
	    	//out.write(new FileContainer(new File(path), result));
	    	return true;
	    }
	    return false;
	}
	
	public static void run(String path, String lex, String type, WriteToUIInterface out) throws IOException, InterruptedException {
		File currentDir = new File(path);
		Tree root;
		if(currentDir == null && !currentDir.isDirectory()) {
			throw new FileNotFoundException();
		}
		else {
			root = new Tree(currentDir);
			Tree chain = new Tree(currentDir);
			traversal(currentDir, lex, type, root, root);
			
		}
	}
	
	public static void traversal(File currentDir, String lex, String type, Tree node, Tree root) throws IOException, InterruptedException {
		File[] dirFiles = currentDir.listFiles();
		for(File current : dirFiles) {
			Tree chain = new Tree(null);
			chain.setNode(current);
			node.addNode(chain);
			if(current.isDirectory()) {
				Tree tmp = new Tree(current);
				tmp.setParent(chain);
				traversal(current,lex, type, tmp, root);
				
				root.addNode(node);
			}
			else if(getFileExtension(current).equals(type)){
				read(current.getAbsolutePath(), lex, chain);
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
