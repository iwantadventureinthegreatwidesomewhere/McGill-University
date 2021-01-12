import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class q1a {
    //parameters
    public static int n;
    public static int t;
    public static int k;

    public static int numEdges = 0;
    public static boolean terminate = false;

    public static void main(String[] args) {
        try {
            //parsing arguments
            if (args.length>0) {
                n = Integer.parseInt(args[0]);
                t = Integer.parseInt(args[1]);
                k = Integer.parseInt(args[2]);
            }
            
            //initializes a list for coordinates
            ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
            
            //creates n unique coordinates and adds them to the list
            for(int i = 0; i < n; i++) {
                boolean coordinateUsed;
    
                do {
                    coordinateUsed = false;
    
                    double x = ThreadLocalRandom.current().nextDouble(0.0, 1000.0);
                    double y = ThreadLocalRandom.current().nextDouble(0.0, 1000.0);
    
                    for(Coordinate coordinate : coordinates) {
                        if(coordinate.x == x && coordinate.y == y) {
                            coordinateUsed = true;
                            break;
                        }
                    }
    
                    if(!coordinateUsed) {
                        coordinates.add(new Coordinate(x, y));
                    }
                } while(coordinateUsed);
            }
            
            //initializes an array for t threads
            Thread[] threads = new Thread[t];
            
            //instantiates t threads
            for(int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new Runnable() {
                    public void run() {
                        //counter for the number of times the thread has failed to an edge
                        int numFails = 0;
                        
                        //iterates until the thread has failed to add a total of k edges or until the termination flag is set to true
                        while(numFails < k && !terminate){
                            Coordinate c1;
                            Coordinate c2;

                            //gets two random coordinates to add an edge between
                            int index1 = ThreadLocalRandom.current().nextInt(0, coordinates.size());
                            int index2 = ThreadLocalRandom.current().nextInt(0, coordinates.size());

                            c1 = coordinates.get(index1);
                            c2 = coordinates.get(index2);

                            //ensures the coordinates are different
                            if(c1 != c2){
                                //tries to lock the first coordinate
                                if(c1.adjacentListLock.tryLock()) {
                                    //tries to lock the second coordinate
                                    if(c2.adjacentListLock.tryLock()) {
                                        //adds coordinates to each other's adjacency list
                                        c1.adjacentCoordinates.add(c2);
                                        c2.adjacentCoordinates.add(c1);
                                        
                                        //increments the shared counter for the number of edges
                                        synchronized(this) {
                                            numEdges++;
                                        }
                                        
                                        //unlocks the held locks
                                        c2.adjacentListLock.unlock();
                                        c1.adjacentListLock.unlock();
                                    } else {
                                        //unlocks the held lock
                                        c1.adjacentListLock.unlock();
                                    }
                                }
                            } else {
                                //increments the counter for the number of fails by the thread
                                numFails++;
                            }
                        }
                        
                        //the first thread to fail sets to termination flag to true to stop all other threads
                        synchronized(this) {
                            terminate = true;
                        }
                    }
                });
                
                //starts thread
                threads[i].start();
            }
            
            //joins all threads
            for(Thread thread : threads){
                thread.join();
            }

            //prints the total number of edges added
            System.out.println("Number of edges added: " + numEdges);
        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }

    //class for a coordinate
    public static class Coordinate {
        public double x;
        public double y;
        public ArrayList<Coordinate> adjacentCoordinates = new ArrayList<Coordinate>();

        //lock for the coordinate's adjacency list
        Lock adjacentListLock = new ReentrantLock();

        Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
