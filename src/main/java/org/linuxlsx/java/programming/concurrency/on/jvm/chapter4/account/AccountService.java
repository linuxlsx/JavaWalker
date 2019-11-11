package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4.account;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author linuxlsx
 * @date 2019/11/11
 */
public class AccountService {

    public boolean transfer(final Account from, final Account to, final int amount) throws InterruptedException {

        //对账户进行排序，确定加锁顺序
        final Account[] accounts = {from, to};
        Arrays.sort(accounts);

        if(accounts[0].monitor.tryLock(1, TimeUnit.SECONDS)){

            try {
                if(accounts[1].monitor.tryLock(1, TimeUnit.SECONDS)){
                    try{
                        if(from.withdraw(amount)){
                            to.deposit(amount);
                            return true;
                        }else {
                            return false;
                        }
                    }finally {
                        accounts[1].monitor.unlock();
                    }
                }
            }finally {
                accounts[0].monitor.unlock();
            }
        }

        throw new RuntimeException("Unable to acquire locks on the accounts");
    }
}
