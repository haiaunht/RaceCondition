
package racecondition_haiau_bui;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static racecondition_haiau_bui.ThreadColor.ANSI_CYAN;
import static racecondition_haiau_bui.ThreadColor.ANSI_GREEN;
import static racecondition_haiau_bui.ThreadColor.ANSI_PURPLE;
import static racecondition_haiau_bui.ThreadColor.ANSI_RED;

/**
 *
 * @author HaiAu Bui,
 * CSD 415, Professor Abbott
 * There will be 4 different class with its own main() method
 * This is the second implementation, use JAVA Monitors
 */
public class Fourth_Implementation {
    
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(ANSI_GREEN + "==========================================");
        System.out.println(ANSI_GREEN + "======= MONITOR - IMPLEMENTATION =======");
        System.out.println(ANSI_GREEN + "==========================================");
        
        //instanciate the circular buffers with size of 5
        CircularBuffer test = new CircularBuffer(5);
        
        Producer producer = new Producer(test);
        Consumer consumer = new Consumer(test);
        producer.start();
        consumer.start();
    }
    
    
    private static class CircularBuffer{
        private int size;
        private int[] buffers;
        private int count = 0;
        
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
        }
        
        
        /**
         * add method will add 1 to the given index, if all slots are full, producer wait() and lock itself
         * then notify() consumer to know that producer unlock when it adding 1 to index
         * @param index : integer index
         */
        public synchronized void add( int index ){
            while( count == size || buffers[index%size] == 1 ){
                try {
                    wait();
                } catch (InterruptedException ex) { }
            }
            int value = buffers[index%size];
            buffers[index%size] += 1;
            System.out.println(ANSI_CYAN + "Pro at " + index + ", value = " + value + ", add = " + buffers[index%size]);
            count++;
            notify();
        }
        
        /**
         * reset method will reset element at given index, if race condition ever happened,
         * display error message and exit
         * @param index 
         */
        public synchronized void reset( int index ){
            while( count == 0 ){
                try {
                    wait();
                } catch (InterruptedException ex) { }
            }
            
            int value = buffers[index%size];
            
            if( value > 1 ){
                System.out.println(ANSI_RED + "Uh Oh!!! The race condition is happening at index: ");
                System.exit(0);
            }
            
            buffers[index%size] = 0;
            System.out.println(ANSI_PURPLE + "Con at " + index + ", value = "+ value + ", reset = "+buffers[index%size]);
            count--;
            notify();
        }
        
    }
    
    
    private static class Producer extends Thread {
        private CircularBuffer buffers;
        private int cpuBurstDuration;
        private int sleepDuration;    
        Random rand = new Random();
        
        public Producer ( CircularBuffer boundedBuffer ){
            this.buffers = boundedBuffer;
            cpuBurstDuration = rand.nextInt(20) + 5;
            sleepDuration = rand.nextInt(2000) + 500;
        }

        @Override
        public void run() {
            System.out.println("Producer cpuBurstDuration === " + cpuBurstDuration);
            while(true){
                //calling add() method to add 1 to slots to 0 from index 0-range(k1)
                for( int i=0; i<cpuBurstDuration; i++ ){                    
                
                    try{
                        buffers.add(i);
                        Thread.sleep(sleepDuration);
                        //buffers.reset(cpuBurstDuration);
                    }catch( InterruptedException e ){}
                }   
                    
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
            cpuBurstDuration = rand.nextInt(40) + 5;
            sleepDuration = rand.nextInt(2000) + 500;
        }

        
       
        @Override
        public void run() {
            System.out.println("Consumer cpuBurstDuration === " + cpuBurstDuration);
            while( true ){
                //calling reset() method to reset slots to 0 from index 0-range(k2)
                for( int i=0; i<cpuBurstDuration; i++ ){

                try{

                    Thread.sleep(sleepDuration);
                    buffers.reset(i);
                }catch( InterruptedException e ){}

                    
                }
            }
        }
    }
    
}

