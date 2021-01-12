import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class q2{
    public static void main(String[] args){
        try{
            //parses program arguments
            int p = Integer.parseInt(args[0]);
            int d = Integer.parseInt(args[1]);
            int n = Integer.parseInt(args[2]);
            int t = Integer.parseInt(args[3]);
            int e = Integer.parseInt(args[4]);

            //initializes elimination stack with the timeout duration and the size of the elimination array
            EliminationStack eliminationStack = new EliminationStack(t, e);

            //creates p threads
            Thread[] threads = new Thread[p];

            long startTime = System.currentTimeMillis();

            //creates lists for the pushes and pops performed by individual threads
            List<Integer> totalPushes = new ArrayList<Integer>();
            for(int a = 0; a < threads.length; a++){
                totalPushes.add(0);
            }

            List<Integer> totalPops = new ArrayList<Integer>();
            for(int a = 0; a < threads.length; a++){
                totalPops.add(0);
            }

            //launches p threads and declares what each thread is to do
            for(int i = 0; i < threads.length; i++){
                final int index = i;

                threads[i] = new Thread(new Runnable(){
                    public void run(){
                        //history list of previously popped nodes to be reused

                        /**
                         * I use AtomicStampedReferences in this program to stamp reuse threads
                         * in order to solve the ABA problem should a node with the same reference be
                         * re-pushed
                         */
                        List<AtomicStampedReference<Node>> history = new ArrayList<AtomicStampedReference<Node>>();
                        int threadPushes = 0;
                        int threadPops = 0;

                        //n pushes/pops attempted
                        for(int j = 0; j < n; j++){
                            //updates the stamp of the history in case other threads re-push similar nodes
                            for(int k = 0; k < history.size(); k++){
                                AtomicStampedReference<Node> node = history.get(k);
                                node.set(node.getReference(), node.getReference().next.size()-1);
                            }

                            //randomly (uniformly distributed) selects whether this iteration will perform a push or a pop
                            Random random = new Random();
                            boolean pushPopChoice = random.nextBoolean();

                            //true is for push, false is for pop
                            if(pushPopChoice){
                                //push
                                AtomicStampedReference<Node> node = null;

                                //if this thread has previously popped nodes, it may select one
                                if(history.size() > 0){
                                    //randomly (uniformly distributed) selects whether this iteration will push an old or new node
                                    boolean oldNewChoice = random.nextBoolean();

                                    //true is for old, false is for new
                                    if(oldNewChoice){
                                        //randomly selects an old node from the thread's history to re-push
                                        node = history.get(random.nextInt(history.size()));
                                    }else{
                                        //creates a new node with a random Integer value to push
                                        node = new AtomicStampedReference<Node>(new Node(random.nextInt()), 0);
                                    }
                                }else{
                                    //creates a new node with a random Integer value to push
                                    node = new AtomicStampedReference<Node>(new Node(random.nextInt()), 0);
                                }

                                //pushes the node onto the elimination stack
                                boolean isSuccessful = eliminationStack.push(node);

                                //if the stack successfully pushed the node, increment the number of pushes
                                if(isSuccessful){
                                    threadPushes++;
                                }

                                //thread then sleeps for a random amount of time, up to d ms
                                try{
                                    Thread.sleep(random.nextInt(d));
                                }catch(InterruptedException e){
            
                                }
                            }else{
                                //pop
                                try{
                                    //thread attempts to pop a node from the elimination stack
                                    AtomicStampedReference<Node> node = eliminationStack.pop();
                                    
                                    //if successful, add to history and increment the number of pops
                                    if(node != null){

                                        //if history contains more than 20 nodes, remove the topmost (oldest)
                                        if(history.size() >= 20){
                                            history.remove(0);
                                        }

                                        history.add(node);
                                        threadPops++;
                                    }
                                }catch(EmptyStackException e){

                                }

                                //thread then sleeps for a random amount of time, up to d ms
                                try{
                                    Thread.sleep(random.nextInt(d));
                                }catch(InterruptedException e){
            
                                }
                            }

                            
                        }

                        //after n iterations, thread updates the totalPushes and totalPops lists with its individual results
                        synchronized(this){
                            totalPushes.set(index, totalPushes.get(index) + threadPushes);
                            totalPops.set(index, totalPops.get(index) + threadPops);
                        }
                    }
                });
                
                threads[i].start();
            }

            //joins all the threads
            for(Thread thread : threads){
                thread.join();
            }

            long endTime = System.currentTimeMillis();
            
            //computes the total sum of pushes and pops performed by the program
            int finalPushes = totalPushes.stream().mapToInt(Integer::intValue).sum();
            int finalPops = totalPops.stream().mapToInt(Integer::intValue).sum();

            System.out.println(endTime - startTime);
            System.out.println(finalPushes + " " + finalPops + " " + eliminationStack.getNumberOfNodes());
        }catch(InterruptedException e){

        }
    }

    public static class LockFreeStack{
        //top-most node in the linked-list of nodes on the stack
        AtomicStampedReference<Node> top = new AtomicStampedReference<Node>(null, -1);

        //tryPush attempts to add a new node to the top; we solve the ABA problem by considering the stamp of the node as well through its AtomicStampedReference
        protected boolean tryPush(AtomicStampedReference<Node> node){
            Node expectedReference = top.getReference();
            int expectedStamp = top.getStamp();
            node.getReference().next.set(node.getStamp(), new AtomicStampedReference<Node>(expectedReference, expectedStamp));
            //if the current reference or stamp for the top-most node does not match the stored ones, then the push is not performed
            return(top.compareAndSet(expectedReference, node.getReference(), expectedStamp, node.getStamp()));
        }

        //pushes a new node using tryPush and returns a boolean result marking whether the push is successful or not
        public boolean push(AtomicStampedReference<Node> node){
            //stamp is incremented as it is going to be used again, and its list of next nodes is expanded to mark the update (i.e. next = null)
            synchronized(this){
                node.getReference().next.add(new AtomicStampedReference<Node>(null, -1));
                node.set(node.getReference(), node.getStamp()+1);
            }

            if(tryPush(node)){
                return true;
            }

            return false;
        }
        
        //tryPop attempts to remove the top-most node and return it to the pop method
        protected AtomicStampedReference<Node> tryPop() throws EmptyStackException{
            //stores the node reference and stamp of the old top
            Node expectedReference = top.getReference();
            int expectedStamp = top.getStamp();

            if(expectedReference == null){
                throw new EmptyStackException();
            }

            AtomicStampedReference<Node> newTop = expectedReference.next.get(Math.max(Math.min(expectedStamp, expectedReference.next.size()-1), 0));

            //if stored reference and stamp for the old top does not match the current top, then the new node newTop is not set as top
            if(top.compareAndSet(expectedReference, newTop.getReference(), expectedStamp, newTop.getStamp())){
                //returns the old top if the top was not changed
                return new AtomicStampedReference<Node>(expectedReference, expectedStamp);
            }else{
                //otherwise returns null
                return null;
            }
        }

        //pop calls tryPop to attempt a pop of the top-most node
        public AtomicStampedReference<Node> pop() throws EmptyStackException{
            AtomicStampedReference<Node> node = tryPop();

            if(node != null){
                return node;
            }else{
                return null;
            }
        }

        //getNumberOfNodes counts and returns the number of items in the stack, starting from top
        public int getNumberOfNodes(){
            AtomicStampedReference<Node> node = top;
            int count = 0;

            while(node.getReference() != null && count < 1000){
                count++;
                node = node.getReference().next.get(node.getStamp());
            }

            return count;
        }
    }

    public static class EliminationStack extends LockFreeStack{
        long timeout;
        List<Exchanger<AtomicStampedReference<Node>>> eliminationArray;

        public EliminationStack(long timeout, int eliminationArraySize){
            this.timeout = timeout;

            //initializes list of eliminationArraySize exchangers for threads to use in case the LockFreeStack operation fails
            eliminationArray = new ArrayList<Exchanger<AtomicStampedReference<Node>>>();

            for(int i = 0; i < eliminationArraySize; i++){
                eliminationArray.add(new Exchanger<AtomicStampedReference<Node>>());
            }
        }

        @Override
        public boolean push(AtomicStampedReference<Node> node){
            synchronized(this){
                node.getReference().next.add(new AtomicStampedReference<Node>(null, -1));
                node.set(node.getReference(), node.getStamp()+1);
            }

            //the new push method still attempts to push onto the LockFreeStack via tryPush
            if(tryPush(node)){
                return true;
            }else{
                //in case it fails, it attempts to exchange values with a popping thread via a random exchanger
                Random random = new Random();
                Exchanger<AtomicStampedReference<Node>> exchanger = eliminationArray.get(random.nextInt(eliminationArray.size()));
                
                try{
                    AtomicStampedReference<Node> result = exchanger.exchange(node, timeout, TimeUnit.MILLISECONDS);

                    //if the exchange yields a null value, the the exchange is successful as we can confirm that the sender is a popping thread, otherwise the operation fails
                    if(result == null){
                        return true;
                    }else{
                        node.set(node.getReference(), node.getStamp() - 1);
                        return false;
                    }
                }catch(InterruptedException | TimeoutException e){
                    node.set(node.getReference(), node.getStamp() - 1);
                    return false;
                }
            }
        }

        @Override
        public AtomicStampedReference<Node> pop() throws EmptyStackException{
            AtomicStampedReference<Node> node = tryPop();

            //the new pop method still attempts to pop from the LockFreeStack via tryPop
            if(node != null){
                return node;
            }else{
                //in case it fails, it attempts to exchange values with a pushing thread via a random exchanger
                Random random = new Random();
                Exchanger<AtomicStampedReference<Node>> exchanger = eliminationArray.get(random.nextInt(eliminationArray.size()));

                try{
                    AtomicStampedReference<Node> result = exchanger.exchange(null, timeout, TimeUnit.MILLISECONDS);

                    //if the exchange yields a non-null value, the the exchange is successful as we can confirm that the sender is a pushing thread with data, otherwise the operation fails
                    if(result != null){
                        return result;
                    }else{
                        return null;
                    }
                }catch(InterruptedException | TimeoutException e){
                    return null;
                }
            }
        }
    }
    
    public static class Node{
        public int value;
        public List<AtomicStampedReference<Node>> next;

        public Node(int value){
            this.value = value;

            next = new ArrayList<AtomicStampedReference<Node>>();
            next.add(new AtomicStampedReference<Node>(null, -1));
        }
    }
}
