package org.linuxlsx.java.programming.concurrency.on.jvm.chapter5;

import java.util.concurrent.*;

/**
 * 1. 通过静态工厂将线程的初始化移出构造函数
 * 2. 对线程资源进行管理
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class EnergySourceWithSourceManage {

    private final long MAX_VALUE = 100;

    /**
     * 线程不安全的共享变量。其他线程可能无法及时看到 level 值的变化
     */
    private long level = MAX_VALUE;

    /**
     * 有了 ScheduledExecutorService 以后这个变量不需要了
     */
    @Deprecated
    private boolean keepRunning = true;

    /**
     * 使用 ScheduledExecutorService 来完成定时的任务处理
     * 另外指定一个线程创建工厂，将创建出来的线程设置为守护线程，这样在进程退出的时候，这些线程也会退出
     * 否则会因为默认创建的线程不是守护线程而导致线程无法关闭
     */
    private static final ScheduledExecutorService replenishTimer = Executors.newScheduledThreadPool(10, r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    });
    private ScheduledFuture<?> replenishTask;

    private EnergySourceWithSourceManage() {

    }

    /**
     * 通过静态工厂解决类的不变式问题
     *
     * @return
     */
    public static EnergySourceWithSourceManage create() {
        final EnergySourceWithSourceManage source = new EnergySourceWithSourceManage();
        source.init();
        return source;
    }

    private void init() {
        //注册一个定时执行的任务
        replenishTask = replenishTimer.scheduleAtFixedRate(() -> replenish(), 0, 1, TimeUnit.SECONDS);
    }

    public long getUnitsAvailable() {
        return level;
    }

    public boolean useEnergy(final long units) {
        if (units > 0 && level > units) {
            level -= units;
            return true;
        }

        return false;
    }

    public void stopEnergySource() {
        replenishTask.cancel(false);
    }

    private void replenish() {

        //直接执行，不需要判断和睡眠
        if (level < MAX_VALUE) {
            level++;
        }


    }
}
