package org.yupeng.util;


import org.yupeng.constant.LockInfoType;
import org.yupeng.lockinfo.LockInfoHandle;
import org.yupeng.lockinfo.factory.LockInfoHandleFactory;
import org.yupeng.servicelock.LockType;
import org.yupeng.servicelock.ServiceLocker;
import org.yupeng.servicelock.factory.ServiceLockFactory;
import org.yupeng.servicelock.info.LockTimeOutStrategy;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com 
 * @description: Distributed lock utility operations
 * @author: yupeng
 **/
@AllArgsConstructor
public class ServiceLockTool {
    
    private final LockInfoHandleFactory lockInfoHandleFactory;
    
    private final ServiceLockFactory serviceLockFactory;
    
    /**
     * Locked execution without return value
     * @param taskRun The task to be executed
     * @param name The business name of the lock
     * @param keys identification of the lock
     *
     * */
    public void execute(TaskRun taskRun,String name,String [] keys) {
        execute(taskRun,name,keys,20);
    } 

    /**
     * Locked execution without return value
     * @param taskRun The task to be executed
     * @param name The business name of the lock
     * @param keys identification of the lock
     * @param waitTime waiting time
     * 
     * */
    public void execute(TaskRun taskRun,String name,String [] keys,long waitTime){
        execute(LockType.Reentrant,taskRun,name,keys,waitTime);
    }
    
    /**
     * Locked execution without return value
     * @param lockType lock type
     * @param taskRun The task to be executed
     * @param name The business name of the lock
     * @param keys identification of the lock
     *
     * */
    public void execute(LockType lockType,TaskRun taskRun,String name,String [] keys) {
        execute(lockType,taskRun,name,keys,20);
    }
    
    /**
     * Locked execution without return value
     * @param lockType lock type
     * @param taskRun The task to be executed
     * @param name The business name of the lock
     * @param keys identification of the lock
     * @param waitTime waiting time
     *
     * */
    public void execute(LockType lockType,TaskRun taskRun,String name,String [] keys,long waitTime) {
        LockInfoHandle lockInfoHandle = lockInfoHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        String lockName = lockInfoHandle.simpleGetLockName(name,keys);
        ServiceLocker lock = serviceLockFactory.getLock(lockType);
        boolean result = lock.tryLock(lockName, TimeUnit.SECONDS, waitTime);
        if (result) {
            try {
                taskRun.run();
            }finally {
                lock.unlock(lockName);
            }
        }else {
            LockTimeOutStrategy.FAIL.handler(lockName);
        }
    }

    /**
     * Locked execution with return value
     * @param taskCall The task to be executed
     * @param name The business name of the lock
     * @param keys identification of the lock
     * @return The return value of the task to be performed
     * */
    public <T> T submit(TaskCall<T> taskCall,String name,String [] keys){
        LockInfoHandle lockInfoHandle = lockInfoHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        String lockName = lockInfoHandle.simpleGetLockName(name,keys);
        ServiceLocker lock = serviceLockFactory.getLock(LockType.Reentrant);
        boolean result = lock.tryLock(lockName, TimeUnit.SECONDS, 30);
        if (result) {
            try {
                return taskCall.call();
            }finally {
                lock.unlock(lockName);
            }
        }else {
            LockTimeOutStrategy.FAIL.handler(lockName);
        }
        return null;
    }
    
    /**
     * get lock
     * @param lockType lock type
     * @param name The business name of the lock
     * @param keys identification of the lock
     *
     * */
    public RLock getLock(LockType lockType, String name, String [] keys) {
        LockInfoHandle lockInfoHandle = lockInfoHandleFactory.getLockInfoHandle(LockInfoType.SERVICE_LOCK);
        String lockName = lockInfoHandle.simpleGetLockName(name,keys);
        ServiceLocker lock = serviceLockFactory.getLock(lockType);
        return lock.getLock(lockName);
    }
    
    /**
     * get lock
     * @param lockType lock type
     * @param lockName lock name
     *
     * */
    public RLock getLock(LockType lockType, String lockName) {
        ServiceLocker lock = serviceLockFactory.getLock(lockType);
        return lock.getLock(lockName);
    }
}
