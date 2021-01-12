import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class q2a {
    //parameters
    public static int n;
    public static int t;
    public static int k;
    public static int m;

    //initializes a list for coordinates
    public static ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();

    public static int numEdges = 0;
    public static boolean coordinateTerminationFlag = false;
    public static boolean spiderTerminationFlag = false;

    public static void main(String[] args) {
        try {
            //parsing arguments
            if (args.length>0) {
                n = Integer.parseInt(args[0]);
                t = Integer.parseInt(args[1]);
                k = Integer.parseInt(args[2]);
                m = Integer.parseInt(args[3]);
            }
            
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
                        
                        //iterates until the thread has failed to add a total of k edges or until the coordinate termination flag is set to true
                        while(numFails < k && !coordinateTerminationFlag) {
                            Coordinate c1;
                            Coordinate c2;

                            //gets two random coordinates to add an edge between
                            int index1 = ThreadLocalRandom.current().nextInt(0, coordinates.size());
                            int index2 = ThreadLocalRandom.current().nextInt(0, coordinates.size());

                            c1 = coordinates.get(index1);
                            c2 = coordinates.get(index2);

                            //ensures the coordinates are different
                            if(c1 != c2) {
                                //tries to lock the first coordinate
                                if(c1.coordinateLock.tryLock()) {
                                    //tries to lock the second coordinate
                                    if(c2.coordinateLock.tryLock()) {
                                        //adds coordinates to each other's adjacency list
                                        c1.adjacentCoordinates.add(c2);
                                        c2.adjacentCoordinates.add(c1);
                                        
                                        //increments the shared counter for the number of edges
                                        synchronized(this) {
                                            numEdges++;
                                        }
                                        
                                        //unlocks the held locks
                                        c2.coordinateLock.unlock();
                                        c1.coordinateLock.unlock();
                                    } else {
                                        //unlocks the held lock
                                        c1.coordinateLock.unlock();
                                    }
                                }
                            } else {
                                //increments the counter for the number of fails by the thread
                                numFails++;
                            }
                        }
                        
                        //the first thread to fail sets to termination flag to true to stop all other threads
                        synchronized(this) {
                            coordinateTerminationFlag = true;
                        }
                    }
                });
                
                //starts thread
                threads[i].start();
            }
            
            //joins all threads
            for(Thread thread : threads) {
                thread.join();
            }

            //initializes an array for t spider threads
            Thread[] spiders = new Thread[t];

            //instantiates t spider threads
            for(int i = 0; i < spiders.length; i++) {
                spiders[i] = new Thread(new Runnable() {
                    public void run() {
                        //initializes a spider instance for the thread
                        Spider threadSpider = new Spider();
                        //counter for the total number of jumps made by the spider
                        int hops = 0;

                        //iterates until the spider termination flag is set to true
                        while(!spiderTerminationFlag) {
                            //calls findRandomSpiderCoordinate to move the spider to a random coordinate
                            findRandomSpiderCoordinate(threadSpider);
                            hops++;

                            //thread sleeps for 50ms
                            try {
                                Thread.sleep(50);
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //thread prints the total number of jumps its spider performed after m ms
                        System.out.println(hops);
                    }
                });
                
                //start thread
                spiders[i].start();
            }

            //main thread waits m ms
            try {
                Thread.sleep(m);
            } catch(Exception e) {
                e.printStackTrace();
            }

            //termination flag is set to true to signal spider threads to stop jumping
            spiderTerminationFlag = true;

            //joins all the threads
            for(Thread spider : spiders) {
                spider.join();
            }
        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }

    //method that moves the spider to a random coordinate
    public static void findRandomSpiderCoordinate(Spider threadSpider) {
        //iterates until spider thread successfully finds a new coordinate
        do {
            //gets random coordinate
            int index = ThreadLocalRandom.current().nextInt(0, coordinates.size());
            Coordinate newCoordinate = coordinates.get(index);

            //tries to lock the current coordinates of the body and legs of the spider
            if(threadSpider.body.currentCoordinate == null || (threadSpider.body.currentCoordinate.coordinateLock.tryLock() && threadSpider.legs[0].currentCoordinate.coordinateLock.tryLock() && threadSpider.legs[1].currentCoordinate.coordinateLock.tryLock() && threadSpider.legs[2].currentCoordinate.coordinateLock.tryLock())) {
                //locks the target coordinate newCoordinate for the body
                if(newCoordinate.coordinateLock.tryLock()) {
                    if(newCoordinate.spider == null) {
                        ArrayList<Coordinate> freeAdjacentCoordinates = new ArrayList<Coordinate>();
                        
                        //finds three available coordinates (for the spider's legs) that are adjacent to the target coordinate newCoordinate
                        for(Coordinate adjacentCoordinate : newCoordinate.adjacentCoordinates) {
                            //locks the adjacent coordinate
                            if(adjacentCoordinate.coordinateLock.tryLock()) {
                                if(adjacentCoordinate.spider == null) {
                                    //adds the adjacent coordinate to the list
                                    freeAdjacentCoordinates.add(adjacentCoordinate);
                                } else {
                                    //unlocks the adjacent coordinate if not free
                                    adjacentCoordinate.coordinateLock.unlock();
                                }
                            }
                            
                            if(freeAdjacentCoordinates.size() == 3) {
                                break;
                            }
                        }
                        
                        //if three adjacent coordinates were found for the legs
                        if(freeAdjacentCoordinates.size() == 3) {
                            //free the spider's current body and leg coordinates
                            if(threadSpider.body.currentCoordinate != null) {
                                threadSpider.body.currentCoordinate.spider = null;
                                threadSpider.body.currentCoordinate.spiderPart = null;

                                for(int i = 0; i < 3; i++) {
                                    threadSpider.legs[i].currentCoordinate.spider = null;
                                    threadSpider.legs[i].currentCoordinate.spiderPart = null;
                                }
                            }

                            //move the spider's body and legs to the new coordinates
                            threadSpider.body.currentCoordinate = newCoordinate;
                            newCoordinate.spider = threadSpider;
                            newCoordinate.spiderPart = threadSpider.body;
    
                            for(int i = 0; i < 3; i++) {
                                threadSpider.legs[i].currentCoordinate = freeAdjacentCoordinates.get(i);
                                freeAdjacentCoordinates.get(i).spider = threadSpider;
                                freeAdjacentCoordinates.get(i).spiderPart = threadSpider.legs[i];
                                freeAdjacentCoordinates.get(i).coordinateLock.unlock();
                            }

                            return;
                        }
                    }
                    
                    newCoordinate.coordinateLock.unlock();
                }

                if(threadSpider.body.currentCoordinate != null) {
                    threadSpider.body.currentCoordinate.coordinateLock.unlock();

                    for(int i = 0; i < 3; i++) {
                        threadSpider.legs[i].currentCoordinate.coordinateLock.unlock();
                    }
                }
            }
            
        } while(true);
    }

    //class for a coordinate
    public static class Coordinate {
        public double x;
        public double y;
        public ArrayList<Coordinate> adjacentCoordinates = new ArrayList<Coordinate>();

        //lock for the entire coordinate
        Lock coordinateLock = new ReentrantLock();

        //coordinate's current spider and spider part
        public Spider spider = null;
        public SpiderPart spiderPart = null;

        Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    //class for a spider
    public static class Spider {
        public SpiderPart body;
        public SpiderPart[] legs;

        Spider() {
            this.body = new SpiderPart();

            this.legs = new SpiderPart[3];
            legs[0] = new SpiderPart();
            legs[1] = new SpiderPart();
            legs[2] = new SpiderPart();
        }
    }

    //class for a spider part
    public static class SpiderPart {
        public Coordinate currentCoordinate = null;
    }
}
