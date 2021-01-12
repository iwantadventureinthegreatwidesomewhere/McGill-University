import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.concurrent.ThreadLocalRandom;

public class q1 {

    // Parameters
    public static int n = 1;
    public static int width;
    public static int height;
    public static int k;

    public static void main(String[] args) {
        try {

            // example of reading/parsing an argument
            if (args.length>0) {
                n = Integer.parseInt(args[0]);
                width = Integer.parseInt(args[1]);
                height = Integer.parseInt(args[2]);
                k = Integer.parseInt(args[3]);
            }

            // once we know what size we want we can create an empty image
            BufferedImage outputimage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);

            // ------------------------------------
            // Your code would go here
            
            // The easiest mechanisms for getting and setting pixels are the
            // BufferedImage.setRGB(x,y,value) and getRGB(x,y) functions.
            // Note that setRGB is synchronized (on the BufferedImage object).
            // Consult the javadocs for other methods.

            // The getRGB/setRGB functions return/expect the pixel value in ARGB format, one byte per channel.  For example,
            //  int p = img.getRGB(x,y);
            // With the 32-bit pixel value you can extract individual colour channels by shifting and masking:
            //  int red = ((p>>16)&0xff);
            //  int green = ((p>>8)&0xff);
            //  int blue = (p&0xff);
            // If you want the alpha channel value it's stored in the uppermost 8 bits of the 32-bit pixel value
            //  int alpha = ((p>>24)&0xff);
            // Note that an alpha of 0 is transparent, and an alpha of 0xff is fully opaque.
            
            // ------------------------------------

            //MARCO GUIDA - 260803123

            //initializes an empty array for the program's threads
            Thread[] threads = new Thread[n];

            //initializes a two-dimensional array that monitors whether a pixel is currently being modified by thread
            boolean[][] pixelsUsed = new boolean[width][height];

            //marks the start time of that part of the program to be timed
            long start = System.currentTimeMillis();

            //instantiates n threads
            for(int i = 0; i < threads.length; i++){
                threads[i] = new Thread(new Runnable(){
                    public void run(){
                        while(true){
                            //ensures that only a single thread is accessing the shared variable k
                            synchronized(this){
                                /**
                                 * checks if more rectangles are to be drawn, and, if so, the thread commits
                                 * to drawing a new rectangle by decrementing k, else breaks
                                 */
                                if(k > 0){
                                    k--;
                                }else{
                                    break;
                                }
                            }

                            //initializes integers representing the coordinates of the two points between which a new rectangle is to be drawn
                            int x1, y1, x2, y2;
                            x1 = y1 = x2 = y2 = -1;

                            while(true){
                                //randomly selects the coordinates of the two points within the boundaries of the image
                                x1 = ThreadLocalRandom.current().nextInt(0, width);
                                y1 = ThreadLocalRandom.current().nextInt(0, height);

                                x2 = ThreadLocalRandom.current().nextInt(x1, width);
                                y2 = ThreadLocalRandom.current().nextInt(y1, height);
                                
                                //ensures that only a single thread is accessing the shared variable pixelsUsed
                                synchronized(this){
                                    /**
                                     * checks if the region to draw the new rectangle has pixels currently 
                                     * used to draw other rectangles, and, if so, sets isSuitable to false, else
                                     * isSuitable is true
                                     */
                                    boolean isSuitable = true;
                                    for(int x = x1; x <= x2; x++){
                                        for(int y = y1; y <= y2; y++){
                                            if(pixelsUsed[x][y]){
                                                isSuitable = false;
                                            }
                                        }
                                    }

                                    /**
                                     * if the region to draw the new rectangle is suitable, then the pixels to be used 
                                     * are reserved in pixelsUsed and the the thread exits the loop
                                     */
                                    if(isSuitable){
                                        for(int x = x1; x <= x2; x++){
                                            for(int y = y1; y <= y2; y++){
                                                pixelsUsed[x][y] = true;
                                            }
                                        }
                                        
                                        break;
                                    }

                                    /**
                                     * if the region to draw the new rectangle is not suitable, then the
                                     * thread performs another iteration of the loop to attempt to
                                     * find a suitable region to draw the new rectangle
                                     */
                                }
                            }

                            //randomly selects the color of the new rectangle
                            int red = ThreadLocalRandom.current().nextInt(0, 256);
                            int green = ThreadLocalRandom.current().nextInt(0, 256);
                            int blue = ThreadLocalRandom.current().nextInt(0, 256);
                            
                            //iterates over the pixels of the image to be used to draw the new rectangle
                            for(int x = x1; x <= x2; x++){
                                for(int y = y1; y <= y2; y++){
                                    if(x == x1 || x == x2 || y == y1 || y == y2){
                                        //sets the border color of the new rectangle to black
                                        int black = 255 << 24;
                                        black += 0 << 16;
                                        black += 0 << 8;
                                        black += 0;
                                        outputimage.setRGB(x, y, black);
                                    }else{
                                        //sets the interior color of the new rectangle to the randomly selected color 
                                        int color = 255 << 24;
                                        color += red << 16;
                                        color += green << 8;
                                        color += blue;
                                        outputimage.setRGB(x, y, color);
                                    }
                                }
                            }

                            //ensures that only a single thread is accessing the shared variable pixelsUsed
                            synchronized(this){
                                /**
                                 * the pixels used to draw the new rectangle are unreserved after the thread
                                 * finishes drawing the rectangle
                                 */
                                for(int x = x1; x <= x2; x++){
                                    for(int y = y1; y <= y2; y++){
                                        pixelsUsed[x][y] = false;
                                    }
                                }
                            }
                        }
                    }
                });
                
                //starts the thread
                threads[i].start();
            }

            //waits for all threads to complete their work
            for(Thread thread : threads){
                thread.join();
            }

            //marks the end time of that part of the program to be timed
            long end = System.currentTimeMillis();

            //prints the time that it takes for all threads to complete their work
            System.out.println("time: " + (end-start) + "ms");
            
            // Write out the image
            File outputfile = new File("outputimage.png");
            ImageIO.write(outputimage, "png", outputfile);

        } catch (Exception e) {
            System.out.println("ERROR " +e);
            e.printStackTrace();
        }
    }
}
