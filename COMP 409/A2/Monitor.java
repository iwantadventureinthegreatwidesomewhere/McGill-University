import java.util.*;

//class for abstract Monitor
public abstract class Monitor{
    //queues for the threads waiting on entering the monitor and waiting on the condition variable
    Deque<Thread> monitorQueue = new LinkedList<Thread>(); 
    Deque<Thread> conditionQueue = new LinkedList<Thread>(); 

    //monitor enter protocol
    public synchronized void enter(Thread caller) throws InterruptedException{
        if(monitorQueue.size() > 0){
            //enqueues the calling thread on end of the monitor wait queue and makes the calling thread wait to be signaled
            monitorQueue.add(caller);
            caller.wait();
        }
    }

     //monitor exit protocol
    public synchronized void exit(Thread caller) throws InterruptedException{
        if(monitorQueue.size() > 0){
            //dequeues the next thread on the monitor wait queue and wakes it up
            Thread nextThread = monitorQueue.remove();
            nextThread.notify();
        }
    }
    
    public abstract void await(Thread caller) throws InterruptedException;

    public abstract void signal(Thread caller) throws InterruptedException;
}
