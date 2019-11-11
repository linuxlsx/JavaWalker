package org.linuxlsx.java.programming.concurrency.on.jvm.chapter5;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 1. 通过静态工厂将线程的初始化移出构造函数
 * 2. 对线程资源进行管理
 * 3. 通过synchronized控制变量 level的可见性
 * 4. 使用AtomicLong来增加并发度。方法都不需要用 synchronized 关键词修饰
 * 5. 用可重入读写锁来保证多个变量变化的原子性
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class EnergySourceGuaranteeAtomic {

    private final long MAX_VALUE = 100;

    /**
     * 增加可重入的读写锁来控制并发和原子性
     */
    private final ReadWriteLock monitor = new ReentrantReadWriteLock();

    /**
     * 显示的锁控制，可以不用使用AtomicLong
     */
    private long level = MAX_VALUE;

    /**
     * 新增的变量。用来保存 level 使用的次数。它需要保证和 level 一起修改
     */
    private long usage = 0L;

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

    private EnergySourceGuaranteeAtomic() {

    }

    /**
     * 通过静态工厂解决类的不变式问题
     *
     * @return
     */
    public static EnergySourceGuaranteeAtomic create() {
        final EnergySourceGuaranteeAtomic source = new EnergySourceGuaranteeAtomic();
        source.init();
        return source;
    }

    private void init() {
        //注册一个定时执行的任务
        replenishTask = replenishTimer.scheduleAtFixedRate(() -> replenish(), 0, 1, TimeUnit.SECONDS);
    }

    public long getUnitsAvailable() {
        monitor.readLock().lock();

        try{
            return level;
        }finally {
            monitor.readLock().unlock();
        }
    }

    public long getUsageCount() {
        monitor.readLock().lock();

        try{
            return usage;
        }finally {
            monitor.readLock().unlock();
        }
    }


    public boolean useEnergy(final long units) {

        monitor.writeLock().lock();

        try{
            if(units > 0 && level >= units){
                level -= units;
                usage++;
                return true;
            }else {
                return false;
            }
        }finally {
            monitor.writeLock().unlock();
        }
    }

    public synchronized void stopEnergySource() {
        replenishTask.cancel(false);
    }

    private void replenish() {

        monitor.writeLock().lock();
        try{
            //直接执行，不需要判断和睡眠
            if (level < MAX_VALUE) {
                level++;
            }
        }finally {
            monitor.writeLock().unlock();
        }



    }
}
