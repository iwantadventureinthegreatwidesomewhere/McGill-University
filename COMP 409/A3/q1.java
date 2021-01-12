import java.util.*;
import java.util.concurrent.*;

/* Template code for making a random, sometimes matching bracket sequence. */
public class q1 {
    public static int size;
    public static char[] array;

    // Static function to construct the sequence.  Randomization is based on the given seed value.
    public static char[] construct(int _size,long seed) {
        Random r = new Random(seed);
        size = _size; // record size for easier validation later
        array = new char[size];

        int b = 0; // current bracket count

        for (int i=0;i<size;) {
            int c = r.nextInt(3);

            // we choose the char randomly from (,),x, but with some constraints
            switch(c) {
            case 0:
                // don't generate an opening bracket if there's not enough chars left to close it
                if (size-i>b) {
                    array[i] = '[';
                    b++;
                    i++;
                }
                break;
            case 1:
                // dont' generate a closing bracket if there are no open ones pending
                if (b>0) {
                    array[i] = ']';
                    b--;
                    i++;
                }
                break;
            default:
                // don't generate a non-bracket if there's not enough chars left to close the brackets
                if (size-i>b) {
                    array[i] = '*';
                    i++;
                }
            }
        }
        return array;
    }

    // A static, sequential verifier for the sequence.
    public static boolean verify() {
        int b = 0;
        for (int i=0;i<size;i++) {
            if (array[i]=='[') {
                b++;
            } else if (array[i]==']') {
                b--;
                if (b<0) return false;
            }
        }
        if (b!=0)
            return false;
        return true;
    }

    // For debugging (of small arrays), show the array as a string.
    public static void print() {
        System.out.println(new String(array));
    }

    // Just a debug driver stub for testing it
    public static void main(String[] args) {
        try {
            //parses program arguments
            int n = Integer.parseInt(args[0]);
            int t = Integer.parseInt(args[1]);
            long s = (args.length>2) ? Integer.parseInt(args[2]) : System.currentTimeMillis();
            
            //constructs the string to verify
            char[] array = Bracket.construct(n, s);
            String string = String.valueOf(array);

            //creates the thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(t);

            List<Callable<Triple>> callableTasks = new ArrayList<Callable<Triple>>();

            //determines the length of the substring to distribute to each thread to do work on
            int substringLength = (int)Math.floor(string.length() / (double)t);

            for(int i = 0; i < t; i++){
                final int index = i;

                String temp = null;

                //creates the substring to give to one of the threads in the thread pool to do work on
                //last thread gets a slightly longer substring as the substringLength calculation rounds down
                if(i != t-1){
                    temp = string.substring(substringLength * i, (substringLength * (i + 1)));
                }else{
                    temp = string.substring(substringLength * i, string.length());
                }

                final String substring = temp;

                //creates the task to give to the thread pool
                Callable<Triple> callableTask = () -> {
                    List<Triple> tripleList = new ArrayList<Triple>();

                    //the thread that is assigned the task creates a Triple for each char in the substring
                    for(int j = 0; j < substring.length(); j++){
                        tripleList.add(new Triple(j, "" + substring.charAt(j)));
                    }

                    //after creating the base case Triples, the thread combines all of the Triples to get the resulting Triple "threadTriple"
                    Triple threadTriple = tripleList.get(0);
                
                    for(int j = 1; j < tripleList.size(); j++){
                        threadTriple = new Triple(threadTriple, tripleList.get(j));
                    }

                    threadTriple.index = index;
                    
                    //task returns the resulting Triple to the caller
                    return threadTriple;
                };

                callableTasks.add(callableTask);
            }

            List<Future<Triple>> futures = null;

            long startTime = System.currentTimeMillis();
            
            //the tasks are all at once provided to the thread pool to work on
            //a list of futures, whose results are to be waited for
            try{
                futures = executorService.invokeAll(callableTasks);
            }catch(InterruptedException ie){
                System.err.println("Error in executing some callable tasks");
                System.exit(0);
            }

            //the futures are waited for, and their result is added to the list of thread Triples "results"
            List<Triple> results = new ArrayList<Triple>();

            for (int i = 0; i < futures.size(); i++) {
                try {
                    results.add(futures.get(i).get());
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error in getting some task results");
                    System.exit(0);
                }
            }
    
            executorService.shutdown();

            //The list of Triples is sorted according to index so that they are not combined in the wrong order later
            results.sort((a, b) -> a.index - b.index);

            //the Triples produced by the threads are sequentially combined to produce the final Triple result
            Triple finalTriple;
            
            if(results.size() > 1){
                Triple temp = results.get(0);
                
                for(int i = 1; i < results.size(); i++){
                    temp = new Triple(temp, results.get(i));
                }

                finalTriple = temp;
            }else{
                //if only one thread is used, we don't have to combine thread Triples
                finalTriple = results.get(0);
            }

            long endTime = System.currentTimeMillis();

            System.out.println(endTime - startTime);
            System.out.println(finalTriple.ok + " " + Bracket.verify());
        } catch(NumberFormatException nfe) {
            System.err.println("Error in parsing arguments");
        }
    }

    /**
     * a class representing a Triple for some string s;
     * a Triple can be constructed either by evaulating the string represents
     * or by combining two existing Triples
     */
    public static class Triple{
        int index;
        String s;

        boolean ok;
        int f;
        int m;

        public Triple(int index, String s){
            //sequential parsing of a string, which in this program is used only for creating base case Triples
            this.index = index;
            this.s = s;

            f = 0;
            m = Integer.MAX_VALUE;

            for(int i = 0; i < s.length(); i++){
                if(s.charAt(i) == '['){
                    f++;
                }else if(s.charAt(i) == ']'){
                    f--;
                }

                m = Math.min(m, f);
            }

            ok = (f == 0);
        }

        public Triple(Triple t1, Triple t2){
            //calculation for combining two Triples
            index = Math.min(t1.index, t2.index);
            s = t1.s + t2.s;

            ok = (t1.ok && t2.ok) || ((t1.f + t2.f == 0) && (t1.m >= 0) && (t1.f + t2.m >= 0));
            f = t1.f + t2.f;
            m = Math.min(t1.m, (t1.f + t2.m));
        }
    }
}
