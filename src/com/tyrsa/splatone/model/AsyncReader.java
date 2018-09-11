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

	public static void read(String path) throws IOException, InterruptedException {
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
	    for (ByteBuffer byteBuffer : buffers) {
	      for (int i = 0; i < byteBuffer.limit(); i++) {
	    	  System.out.print((char) byteBuffer.get(i));
	      }
	    }
	}
	
	public static void run(String path) throws IOException, InterruptedException {
		File currentDir = new File(path);
		if(currentDir == null && !currentDir.isDirectory()) {
			throw new FileNotFoundException();
		}
		else {
			traversal(currentDir);
		}
	}
	
	public static void traversal(File currentDir) throws IOException, InterruptedException {
		File[] dirFiles = currentDir.listFiles();
		for(File current : dirFiles) {
			if(current.isDirectory()) {
				traversal(current);
			}
			else {
				read(current.getAbsolutePath());
			}
		}
	}

}
