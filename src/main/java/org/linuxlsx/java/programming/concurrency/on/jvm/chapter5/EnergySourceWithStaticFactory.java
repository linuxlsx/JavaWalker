package org.linuxlsx.java.programming.concurrency.on.jvm.chapter5;

/**
 * 通过静态工厂将线程的初始化移出构造函数
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class EnergySourceWithStaticFactory {

    private final long MAX_VALUE = 100;

    /**
     * 线程不安全的共享变量。其他线程可能无法及时看到 level 值的变化
     */
    private long level = MAX_VALUE;
    private boolean keepRunning = true;

    private EnergySourceWithStaticFactory(){

    }

    /**
     * 通过静态工厂解决类的不变式问题
     * @return
     */
    public static EnergySourceWithStaticFactory create(){
        final EnergySourceWithStaticFactory source = new EnergySourceWithStaticFactory();
        source.init();
        return source;
    }

    private void init(){
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
