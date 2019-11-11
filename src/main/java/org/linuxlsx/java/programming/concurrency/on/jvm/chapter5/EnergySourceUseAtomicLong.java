package org.linuxlsx.java.programming.concurrency.on.jvm.chapter5;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 1. 通过静态工厂将线程的初始化移出构造函数
 * 2. 对线程资源进行管理
 * 3. 通过synchronized控制变量 level的可见性
 * 4. 使用AtomicLong来增加并发度。方法都不需要用 synchronized 关键词修饰
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class EnergySourceUseAtomicLong {

    private final long MAX_VALUE = 100;

    /**
     * 将变量修改使用 AtomicLong 保证变量的并发安全性
     */
    private AtomicLong level = new AtomicLong(MAX_VALUE);

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

    private EnergySourceUseAtomicLong() {

    }

    /**
     * 通过静态工厂解决类的不变式问题
     *
     * @return
     */
    public static EnergySourceUseAtomicLong create() {
        final EnergySourceUseAtomicLong source = new EnergySourceUseAtomicLong();
        source.init();
        return source;
    }

    private void init() {
        //注册一个定时执行的任务
        replenishTask = replenishTimer.scheduleAtFixedRate(() -> replenish(), 0, 1, TimeUnit.SECONDS);
    }

    public long getUnitsAvailable() {
        return level.get();
    }

    public boolean useEnergy(final long units) {

        final long currentLevel = level.get();
        if (units > 0 && currentLevel > units) {
            return level.compareAndSet(currentLevel, currentLevel - units);
        }

        return false;
    }

    public void stopEnergySource() {
        replenishTask.cancel(false);
    }

    private void replenish() {

        //直接执行，不需要判断和睡眠
        if (level.get() < MAX_VALUE) {
            level.incrementAndGet();
        }


    }
}
