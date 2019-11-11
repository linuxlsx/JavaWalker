package org.linuxlsx.java.programming.concurrency.on.jvm.chapter4.account;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 为什么Account 要实现 {@link Comparable}。假设有两个线程同时在相同两个账户之间转账并且转账属性相反，类似这样： <br />
 * thread1: transfer money from account1 to account2 <br />
 * thread2: transfer money from account2 to account1 <br />
 *
 * 在这种情况下，thread1 可能会锁定 account1, thread2 会锁定 account2, 于是两个线程就形成了死锁。为了解决这个问题， <br />
 * 可以令两个线程按照相同的顺序加锁。这样一个线程可以获得两把锁，另一个线程则需要等待。实现 {@link Comparable} 可以 <br />
 * 利用账户之间的自然顺序作为加锁的顺序。实际实现的时候可以使用账户ID等更稳定的方式来进行排序。 <br />
 *
 * @author linuxlsx
 * @date 2019/11/11
 */
public class Account implements Comparable<Account>{

    private int balance;

    public final Lock monitor = new ReentrantLock();

    @Override
    public int compareTo(Account o) {
        return new Integer(hashCode()).compareTo(o.hashCode());
    }

    public void deposit(final int amount){

        monitor.lock();
        try{
            if(amount > 0){
                balance += amount;
            }
        }finally {
            monitor.unlock();
        }
    }

    public boolean withdraw(final int amount){
        monitor.lock();
        try {

            if(amount > 0 && balance >= amount){
                balance -= amount;
                return true;
            }

            return false;
        }finally {
            monitor.unlock();
        }
    }
}
