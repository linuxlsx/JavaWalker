package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4;

import java.io.File;

/**
 * 初始的顺序版本
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class TotalFileSizeSequential {

    private long getTotalSizeOfFilesInDir(final File file){
        if(file.isFile()){
            return file.length();
        }

        final File[] children = file.listFiles();
        long total = 0L;

        if (children != null) {
            for (File child : children) {
                total += getTotalSizeOfFilesInDir(child);
            }
        }

        return total;
    }

    public static void main(String[] args) {

        String filePath = "/Users/linuxlsx/logs";

        //Total Size: 48841844
        //Time taken: 0.018896883

        String filePath2 = "/Users/linuxlsx/workspace/jdk/jdk12";

        //Total Size: 628661900
        //Time taken: 7.188894109

        final long start = System.nanoTime();
        final long total = new TotalFileSizeSequential().getTotalSizeOfFilesInDir(new File(filePath2));
        final long end = System.nanoTime();

        System.out.println("Total Size: " + total);
        System.out.println("Time taken: " + (end - start) / 1.0e9);
    }
}
