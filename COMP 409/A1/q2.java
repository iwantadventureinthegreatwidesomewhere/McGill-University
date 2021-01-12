import java.util.*;
import java.math.*;

//MARCO GUIDA - 260803123

public class q2{
    //parameters
    public static volatile TreeNode root;
    public static volatile LeafThreadNode head;
    public static volatile LeafThreadNode tail;

    //list of strings of ids used by tree nodes
    public static volatile List<String> usedIDs = new ArrayList<String>();

    //boolean flag to signal to threads to stop looping
    public static volatile boolean terminationFlag;

    public static void main(String[] args){
        try{
            //initializes root node and its two child nodes
            root = new TreeNode(null, null, null, null);
            root.createChildren(createNodeID());

            //initializes the leaf-threading
            head = new LeafThreadNode(root.leftChild, null);
            tail = new LeafThreadNode(root.rightChild, null);
            head.next = tail;

            //initializes the termination flag to false
            terminationFlag = false;

            //initializes thread 0
            Thread thread0 = new Thread(new Runnable(){
                public void run(){
                    while(!terminationFlag){
                        /**
                         * following block of code iterates the leaf-threading
                         * starting from the head node and prints the name of each 
                         * node encountered
                         */
                        LeafThreadNode presentNode = head;
                        
                        while(presentNode != null){
                            if(presentNode == head){
                                System.out.print("*");
                            }

                            System.out.print(" " + presentNode.node.nodeID);
                            presentNode = presentNode.next;

                            //thread sleeps for 50ms between outputting each individual node-name
                            try{
                                Thread.sleep(50);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        System.out.println();
                        
                        //thread sleeps for 200ms between iterations of the leaf-threading
                        try{
                            Thread.sleep(200);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            //initializes thread 1
            Thread thread1 = new Thread(new Runnable(){
                public void run(){
                    while(!terminationFlag){
                        /**
                         * following block of code iterates the leaf-threading
                         * starting from the head node
                         */
                        LeafThreadNode presentNode = head;
                        
                        while(presentNode != null){
                            /**
                             * expandNode computes whether the tree is to be expanded at 
                             * this node (1/10 chance) by randomly selecting an integer in 
                             * the range [0,9). If the selected integer is 0, then the tree is expanded
                             * by calling the createChildren method. 
                             */
                            boolean expandNode = new Random().nextInt(10)==0;

                            if(expandNode){
                                presentNode.node.createChildren(createNodeID());
                            }

                            presentNode = presentNode.next;

                            //thread sleeps for 20ms before moving to the next node
                            try{
                                Thread.sleep(20);
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        
                        //thread sleeps for 200ms between iterations of the leaf-threading
                        try{
                            Thread.sleep(200);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            //initializes thread 2
            Thread thread2 = new Thread(new Runnable(){
                public void run(){
                    while(!terminationFlag){
                        //calls the DFS method to compute the total number of nodes in the tree and then prints the result
                        int count = DFS(root);
                        System.out.println(count);

                        //thread sleeps for 200ms before beginning a new count
                        try{
                            Thread.sleep(200);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            //program starts all of the threads
            thread0.start();
            thread1.start();
            thread2.start();

            //program waits for 5s
            try{
                Thread.sleep(5000);
            } catch(Exception e){
                e.printStackTrace();
            }

            //termination flag is set to true to signal to threads to stop looping
            terminationFlag = true;
            
            //joins all of threads
            thread0.join();
            thread1.join();
            thread2.join();

            System.out.println();

            //calls the DFS method to compute the final count from thread 2
            int count = DFS(root);
            System.out.print(count);
            
            System.out.println();

            /**
             * following block of code iterates the leaf-threading
             * starting from the head node to find the final contents 
             * of the leaf threading
             */
            LeafThreadNode presentNode = head;
                        
            while(presentNode != null){
                System.out.print(presentNode.node.nodeID + " ");
                presentNode = presentNode.next;
            }

            System.out.println();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * performs depth-first search through the tree from the root to count 
     * the number of nodes in the tree by recursively calling the DFS method
     */
    public static int DFS(TreeNode node){
        //if the node is a leaf node, returns 1
        if(node.leftChild == null && node.rightChild == null){
            //thread sleeps for 10ms before returning the count
            try{
                Thread.sleep(10);
            } catch(Exception e){
                e.printStackTrace();
            }

            return 1;
        }

        /**
         * if the node is not a leaf node, calls the DFS method on the node's 
         * children to count the total number of nodes branching from this node
         */
        int leftCount = DFS(node.leftChild);
        int rightCount = DFS(node.rightChild);

        //thread sleeps for 10ms before returning the count
        try{
            Thread.sleep(10);
        } catch(Exception e){
            e.printStackTrace();
        }

        //returns the total number of nodes branching from this node + 1 (for this node)
        return leftCount+rightCount+1;
    }

    /**
     * generates a unique random string made up of 10 alphanumeric characters
     */
    public static String createNodeID(){
        //iterates until a unique random string is produced
        while(true){
            String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

            StringBuilder sb = new StringBuilder(10); 
    
            for (int i = 0; i < 10; i++){
                sb.append(chars.charAt((int)(chars.length()*Math.random()))); 
            }

            //if the random string is unique, then adds the string to usedIDs and returns the string
            if(!usedIDs.contains(sb.toString())){
                usedIDs.add(sb.toString());
                return sb.toString();
            }
        }
    }

    //data structure to represent a node in the tree
    public static class TreeNode{
        volatile String nodeID;
        volatile TreeNode parent;
        volatile TreeNode leftChild;
        volatile TreeNode rightChild;
    
        public TreeNode(String nodeID, TreeNode parent, TreeNode leftChild, TreeNode rightChild){
            this.nodeID = nodeID;
            this.parent = parent;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }
        
        //createChildren expands the tree from this node using a unique random string and fixes the leaf-threading
        public boolean createChildren(String nodeID){
            if(leftChild == null && rightChild == null){
                /**
                 * initializes the left and right child tree node and assigns the left node
                 * the lower case version of the unique random string and the right node the
                 * the upper case version
                 */
                //
                this.leftChild = new TreeNode(nodeID.toLowerCase(), this, null, null);
                this.rightChild = new TreeNode(nodeID.toUpperCase(), this, null, null);

                /**
                 * following block of code iterates the leaf-threading
                 * starting from the head node to fix the leaf-threading by
                 * replacing this node in the threading with the two child nodes
                 */
                LeafThreadNode presentNode = head;
                LeafThreadNode previousNode = null;
                        
                while(presentNode != null){
                    if(presentNode.node.nodeID == this.nodeID){
                        /**
                         * if this node is found in the leaf-threading, then initializes the left and right
                         * leaf-threading nodes for child tree nodes and links the left leaf-threading node
                         * to the right leaf-threading nodes
                         */
                        LeafThreadNode leftThreadChild = new LeafThreadNode(this.leftChild, null);
                        LeafThreadNode rightThreadChild = new LeafThreadNode(this.rightChild, null);
                        leftThreadChild.next = rightThreadChild;

                        /**
                         * if this node is the head of the leaf-threading, then the left leaf-threading node 
                         * is made the new head, otherwise links the previous leaf-threading node to the
                         * left leaf-threading node
                         */
                        //
                        if(presentNode == head){
                            head = leftThreadChild;
                        }else{
                            previousNode.next = leftThreadChild;
                        }
                        
                        rightThreadChild.next = presentNode.next;

                        /**
                         * if this node is the tail of the leaf-threading, then the right leaf-threading node 
                         * is made the new tail
                         */
                        if(presentNode == tail){
                            tail = rightThreadChild;
                        }
                    }

                    previousNode = presentNode;
                    presentNode = presentNode.next;
                }
                
                return true;
            }

            return false;
        }
    }

    //data structure to represent a node in the leaf-threading
    public static class LeafThreadNode{
        volatile TreeNode node;
        volatile LeafThreadNode next;

        LeafThreadNode(TreeNode node, LeafThreadNode next){
            this.node = node;
            this.next = next;
        }
    }
}
