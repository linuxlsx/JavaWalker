package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用队列的方式进一步简化了代码
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class ConcurrentTotalFileSizeWithQueue {

    private ExecutorService service;
    private BlockingQueue<Long> fileSizes = new ArrayBlockingQueue<>(500);
    private AtomicLong pendingFileVisits = new AtomicLong();

    private void startExploreDir(final File file) {
        pendingFileVisits.incrementAndGet();
        service.execute(() -> exploreDir(file));

    }

    private void exploreDir(final File file) {
        long fileSize = 0L;
        if (file.isFile()) {
            fileSize = file.length();
        } else {
            File[] children = file.listFiles();
            for (File child : children) {
                if(child.isFile()){
                    fileSize += child.length();
                }else {
                    startExploreDir(child);
                }
            }
        }

        try{
            fileSizes.put(fileSize);

        }catch(Exception e){
            throw new RuntimeException(e);
        }

        pendingFileVisits.decrementAndGet();
    }

    public long getTotalSizeOfFile(final File file) throws InterruptedException {

        service = Executors.newFixedThreadPool(100);

        try {
            startExploreDir(file);

            long totalSize = 0L;

            while (pendingFileVisits.get() > 0 || fileSizes.size() > 0){
                totalSize += fileSizes.poll(10, TimeUnit.SECONDS);
            }

            return totalSize;
        }finally {
            service.shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String filePath = "/Users/linuxlsx/logs";
        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        //Total Size: 628661900
        //Time taken: 2.238917393

        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFileSizeWithQueue().getTotalSizeOfFile(new File(filePath2));
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }

}
