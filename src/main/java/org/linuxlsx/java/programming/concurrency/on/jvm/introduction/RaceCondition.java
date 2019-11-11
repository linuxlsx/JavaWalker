package org.linuxlsx.java.programming.concurrency.on.jvm.introduction;

/**
 * @author linuxlsx
 * @date 2019/11/11
 */
public class RaceCondition {

    /**
     * 存在内存可见性问题
      */
    private static boolean done;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            int i = 0;
            while (!done){ i++; };
            System.out.println("Done!");
        }).start();

        System.out.println("OS: " + System.getProperty("os.name"));
        Thread.sleep(2000L);

        //在 server 模式下，done = true 可能不会被前面创建的线程读到
        //通过Jit编译优化后，线程可能都不会感知到有done这个变量
        done = true;
        System.out.println("flag done set to true");

        //OS X 的输出
        //OS: Mac OS X
        //flag done set to true
    }


}
