package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 使用 ForkJoinPool 来解决该问题。这是目前最简洁，性能最高的解决方案。
 * 得益于 ForkJoinPool 的 work-stealing 机制，最大化的利用了线程的计算能力。
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class ForkJoinFileSize {

    private final static ForkJoinPool forkJoinPool = new ForkJoinPool();

    private static class FileSizeFinder extends RecursiveTask<Long> {

        final File file;

        public FileSizeFinder(File file) {
            this.file = file;
        }

        @Override
        protected Long compute() {

            long size = 0L;

            if (file.isFile()) {
                size = file.length();
            }else {

                List<ForkJoinTask<Long>> tasks = new ArrayList<>();

                File[] children = file.listFiles();
                for (File child : children) {
                    if(child.isFile()){
                        size += child.length();
                    }else {
                        tasks.add(new FileSizeFinder(child));
                    }
                }

                for (ForkJoinTask<Long> task : invokeAll(tasks)) {
                    size += task.join();
                }
            }

            return size;
        }

    }

    public static void main(String[] args) {
        String filePath = "/Users/linuxlsx/logs";
        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        //Total Size: 628661900
        //Time taken: 1.777697445

        final long start = System.nanoTime();
        final long total = forkJoinPool.invoke(new FileSizeFinder(new File(filePath2)));
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }
}
