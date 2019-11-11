package org.linuxlsx.java.programming.concurrency.on.jvm.chapter5;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * 并发不安全的初始代码。EnergySource 定义为是可以被多个线程同时访问的对象
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
@NotThreadSafe
public class EnergySource {

    private final long MAX_VALUE = 100;

    /**
     * 线程不安全的共享变量。其他线程可能无法及时看到 level 值的变化
     */
    private long level = MAX_VALUE;
    private boolean keepRunning = true;

    public EnergySource(){
        //1. 每个对象都创建一个线程，会导致线程过多
        //2. 该实现破坏了类的不变式。线程可能会在 EnergySource 尚未正确初始化就调用 replenish 方法。
        //   start 方法是 synchronized 修饰的，Thread 实例在 EnergySource 实例化完成之前就脱离控制
        new Thread(() -> replenish()).start();
    }

    public long getUnitsAvailable(){return level;}

    public boolean useEnergy(final long units){
        if(units >0 && level > units){
            level -= units;
            return true;
        }

        return false;
    }

    public void stopEnergySource(){keepRunning = false;}

    private void replenish(){
        while (keepRunning){
            if (level < MAX_VALUE){
                level++;
            }

            try{
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
