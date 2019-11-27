package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 有问题的并发版本。线程池诱发死锁
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class NaivelyConcurrentTotalFileSize {

    private long getTotalSizeOfFilesInDir(final ExecutorService service, final File file) throws ExecutionException, InterruptedException, TimeoutException {

        if(file.isFile()){
            return file.length();
        }

        long total = 0L;
        final File[] children = file.listFiles();

        if (children != null) {
            final List<Future<Long>> partialToFutures = new ArrayList<>();
            for (final File child : children) {
                partialToFutures.add(service.submit(() -> getTotalSizeOfFilesInDir(service, child)));
            }

            for (Future<Long> partialToFuture : partialToFutures) {
                //如果这个地方不加超时时间，可能会一直没法终止
                total += partialToFuture.get(100, TimeUnit.SECONDS);
            }
        }

        return total;
    }

    private long getTotalSizeOfFile(final String fileName) throws ExecutionException, InterruptedException, TimeoutException {

        //这个实现有个问题，如果当目录的深度超过线程的数量，那就就会出现 "线程池诱发死锁"。
        //因为每个线程都被一个目录子任务占用，同时又需要更多的线程来执行新的子目录统计
        //所以就会出现卡死的问题
        final ExecutorService service = Executors.newFixedThreadPool(100);

        try{
            return getTotalSizeOfFilesInDir(service, new File(fileName));
        }finally {
            service.shutdown();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        String filePath = "/Users/linuxlsx/logs";
        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        final long start = System.nanoTime();
        final long total = new NaivelyConcurrentTotalFileSize().getTotalSizeOfFile(filePath2);
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }
}
