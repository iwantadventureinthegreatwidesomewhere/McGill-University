import java.util.*;

public class MonitorSC extends Monitor{
    public synchronized void await(Thread caller) throws InterruptedException{
        if(conditionQueue.size() > 0){
            //enqueues the calling thread to end of the condition variable wait queue and makes the calling thread wait to be signaled
            conditionQueue.add(caller);
            caller.wait();
        }
    }

    public synchronized void signal(Thread caller) throws InterruptedException{
        if(conditionQueue.size() > 0){
            //dequeues the next thread on the condition variable wait queue and enqueues it on the monitor wait queue
            Thread nextThread = conditionQueue.remove();
            monitorQueue.add(nextThread);
            nextThread.wait();
            //calling thread continues
        }
    }
}
