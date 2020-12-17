
package racecondition_haiau_bui;

import java.util.Random;
import java.util.concurrent.Semaphore;
import static racecondition_haiau_bui.ThreadColor.ANSI_CYAN;
import static racecondition_haiau_bui.ThreadColor.ANSI_GREEN;
import static racecondition_haiau_bui.ThreadColor.ANSI_PURPLE;
import static racecondition_haiau_bui.ThreadColor.ANSI_RED;

/**
 *
 * @author HaiAu Bui,
 * CSD 415, Professor Abbott
 * There will be 4 different class with its own main() method
 * This is the second implementation, use SEMAPHORE to SYNCHRONIZE the threads and AVOID the race condition
 */
public class Second_Implementation {
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(ANSI_GREEN + "==========================================");
        System.out.println(ANSI_GREEN + "======= SEMAPHORE - IMPLEMENTATION =======");
        System.out.println(ANSI_GREEN + "==========================================");
        CircularBuffer test = new CircularBuffer(5);
        
        Producer producer = new Producer(test);
        Consumer consumer = new Consumer(test);
        producer.start();
        consumer.start();

    }
    
    
    /**
     * Class Circular buffer will instantiate the shared buffers 
     * for Producer to write int 1 on empty slot 
     * and for Consumer to reset to 0 on a non-empty slot       
     */
    private static class CircularBuffer{
        private int size;
        private int[] buffers;
        private int nextIn = 0, nextOut = 0;
        Semaphore mutex = new Semaphore(1);
        Semaphore full = new Semaphore(0);
        Semaphore empty;
        
        
        /**Constructor will pass the size of the circular buffer
         * @param size: integer size of the circular buffers
        */
        public CircularBuffer( int size ){
            if( size <= 0 ){
                throw new IllegalArgumentException("number of slots can not be less than 0!");
            }
            //pass in the buffers size and initalize the array with values of 0's
            this.size = size;
            buffers = new int[size];    
            empty = new Semaphore( size );
            
        }

        //add method will synchronized adding 1 to the slot from index 0 - range(k1) 
        //of the shared buffers using Semaphore mechanisms
        public void add( int range ) {
            try {
                //while( nextIn < range ){
                while(true){
                    for( int i = 0; i<range; i++){
                        empty.acquire();
                        mutex.acquire();
                        int value = buffers[nextIn%size];

                        //add 1 to the slot
                        buffers[nextIn%size] += 1;                    
                        System.out.println(ANSI_CYAN + "Pro: " + i + ", value: " + value + ", add = "+ buffers[nextIn%size]);                    
                        nextIn = (nextIn+1);               

                        mutex.release();
                        full.release();
                    }
                }
                
            } catch (InterruptedException ex) {
                //System.out.println("I am done!!!");
            }
        }
        
             
        //reset method will synchronized reset slot = 0 from index 0 - range(k2) 
        //of the shared buffers using Semaphore mechanisms
        public void reset( int range ) {
            try {                
                while(true){
                    for( int i = 0; i<range; i++){
                        full.acquire();
                        mutex.acquire();
                        int value = buffers[nextOut%size];

                        //if race condition will ever happend (it should not since we use semaphore synchronized)
                        //will display a red color error message
                        if( value > 1 ){
                            System.out.println(ANSI_RED + "Uh Oh!!! The race condition is happening at index: ");
                            System.exit(0);
                        } 
                        
                        buffers[nextOut%size] = 0;
                        System.out.println(ANSI_PURPLE + "Con: " + i + ", value: " + value + ", set = " + buffers[nextOut%size]);
                        nextOut = (nextOut+1);

                        mutex.release();
                        empty.release();
                    }
                }
                
            } catch (InterruptedException ex) { }
        }
    }
    

    //Producer class 
    private static class Producer extends Thread {
        private CircularBuffer buffers;
        private int cpuBurstDuration;
        private int sleepDuration;    
        Random rand = new Random();
        
        public Producer ( CircularBuffer boundedBuffer ){
            this.buffers = boundedBuffer;
            cpuBurstDuration = rand.nextInt(20) + 5;
            sleepDuration = rand.nextInt(1000) + 500;
        }

        @Override
        public void run() {
            System.out.println("Producer cpuBurstDuration === " + cpuBurstDuration);
            while(true){
                try{
                //calling add() method to add 1 of slot 0-range(k1)
                buffers.add(cpuBurstDuration);

                Thread.sleep(sleepDuration);

                }catch(InterruptedException e){}
            }
        }        
    }


    //Consumer class 
    private static class Consumer extends Thread {    
        private CircularBuffer buffers;
        private int cpuBurstDuration;
        private int sleepDuration;
        Random rand = new Random();

        public Consumer ( CircularBuffer boundedBuffer ){
            this.buffers = boundedBuffer;
            cpuBurstDuration = rand.nextInt(20) + 5;
            sleepDuration = rand.nextInt(1000) + 500;
        }

        
       
        @Override
        public void run() {
            System.out.println("Consumer cpuBurstDuration === " + cpuBurstDuration);
            while( true ){
                //calling reset() method to reset slots to 0 from index 0-range(k2)
                try{
                        
                    Thread.sleep(sleepDuration);
                    buffers.reset(cpuBurstDuration);

                }catch( InterruptedException e ){}
            }
        }
    }

}
    

