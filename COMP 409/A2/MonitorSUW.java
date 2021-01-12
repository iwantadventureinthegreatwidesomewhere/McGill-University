import java.util.*;

public class MonitorSUW extends Monitor{
    public synchronized void await(Thread caller) throws InterruptedException{
        if(conditionQueue.size() > 0){
            //enqueues the calling thread to end of the condition variable wait queue and makes the calling thread wait to be signaled
            conditionQueue.add(caller);
            caller.wait();
        }
    }

    public synchronized void signal(Thread caller) throws InterruptedException{
        if(conditionQueue.size() > 0){
            //dequeues and signals the next thread on the condition variable wait queue
            Thread nextThread = conditionQueue.remove();
            nextThread.notify();
            //enqueues the calling thread on to the FRONT of the monitor wait queue so that calling thread continues to execute immediately afterwards
            monitorQueue.offerFirst(caller);
            caller.wait();
        }
    }
}
