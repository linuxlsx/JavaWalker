package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用 {@link CountDownLatch} 来简化并发控制。并且使用{@link AtomicLong}来直接更新目录的大小。
 * 因为引入了更多的并发控制，所以在性能上稍有降低
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class ConcurrentTotalFileSizeWithLatch {

    private ExecutorService service;
    private AtomicLong totalSize = new AtomicLong();
    private AtomicLong pendingFileVisits = new AtomicLong();
    private CountDownLatch latch = new CountDownLatch(1);

    private void updateTotalSizeOfFilesInDir(final File file){
        long fileSize = 0L;
        if(file.isFile()){
            fileSize = file.length();
        }else {
            File[] children = file.listFiles();
            for (File child : children) {
                if(child.isFile()){
                    fileSize += child.length();
                }else {
                    pendingFileVisits.incrementAndGet();
                    service.execute(() -> updateTotalSizeOfFilesInDir(child));
                }
            }
        }

        totalSize.addAndGet(fileSize);
        if(pendingFileVisits.decrementAndGet() == 0){
            latch.countDown();
        }
    }

    private long getTotalSizeOfFile(File file) throws InterruptedException {
        service = Executors.newFixedThreadPool(100);
        pendingFileVisits.incrementAndGet();

        try{
            updateTotalSizeOfFilesInDir(file);
            latch.await(100, TimeUnit.SECONDS);
            return totalSize.longValue();
        }finally {
            service.shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        String filePath = "/Users/linuxlsx/logs";
        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        //Total Size: 628661900
        //Time taken: 2.17109267

        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFileSizeWithLatch().getTotalSizeOfFile(new File(filePath2));
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }
}
