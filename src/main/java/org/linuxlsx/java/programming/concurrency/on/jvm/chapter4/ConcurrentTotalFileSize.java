package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 修复后的并发版本。子任务只计算目录中文件的大小以及子目录的集合。
 * 再由主线程继续分配任务处理子目录
 *
 * 虽然结果正确且速度比{@link TotalFileSizeSequential}要快，但是复杂度也是更高，而且并发处理不直观
 * 需要绕一步思考才能了解实现思路
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class ConcurrentTotalFileSize {

    private long getTotalSizeOfFilesInDir(final File file) throws InterruptedException, ExecutionException, TimeoutException {

        final ExecutorService service = Executors.newFixedThreadPool(100);
        try{
            long total = 0L;
            final List<File> directories = new ArrayList<>();
            directories.add(file);

            while (!directories.isEmpty()){
                final List<Future<SubDirectoriesAndSize>> partialResults = new ArrayList<>();

                for (File directory : directories) {
                    partialResults.add(service.submit(() -> getTotalAndSubDirs(directory)));
                }

                directories.clear();

                for (Future<SubDirectoriesAndSize> partialResult : partialResults) {
                    final SubDirectoriesAndSize subDirectoriesAndSize = partialResult.get(100, TimeUnit.SECONDS);
                    directories.addAll(subDirectoriesAndSize.subDirectories);
                    total += subDirectoriesAndSize.size;
                }
            }

            return total;
        }finally {
            service.shutdown();
        }

    }

    private SubDirectoriesAndSize getTotalAndSubDirs(final File file){
        long total = 0;
        final List<File> subDirectories = new ArrayList<>();

        if(file.isDirectory()){
            final File[] children = file.listFiles();
            for (File child : children) {
                if(child.isFile()){
                    total += child.length();
                }else {
                    subDirectories.add(child);
                }
            }
        }

        return new SubDirectoriesAndSize(total, subDirectories);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

        String filePath = "/Users/linuxlsx/logs";
        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        //Total Size: 628661900
        //Time taken: 2.10601981

        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFileSize().getTotalSizeOfFilesInDir(new File(filePath2));
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }

    class SubDirectoriesAndSize{

        final public long size;
        final public List<File> subDirectories;

        public SubDirectoriesAndSize(long size, List<File> subDirectories) {
            this.size = size;
            this.subDirectories = Collections.unmodifiableList(subDirectories);
        }
    }
}
