
package racecondition_haiau_bui;

import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import static racecondition_haiau_bui.ThreadColor.ANSI_CYAN;
import static racecondition_haiau_bui.ThreadColor.ANSI_GREEN;
import static racecondition_haiau_bui.ThreadColor.ANSI_PURPLE;
import static racecondition_haiau_bui.ThreadColor.ANSI_RED;

/**
 *
 * @author HaiAu Bui,
 * CSD 415, Professor Abbott
 * There will be 4 different class with its own main() method
 * This is the third implementation, use ATOMICINTEGERARRY data type
 */
public class Third_Implementation {
    
    
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(ANSI_GREEN + "============================================");
        System.out.println(ANSI_GREEN + "======= ATOMIC DATA - IMPLEMENTATION =======");
        System.out.println(ANSI_GREEN + "============================================");
        
        //instanciate the circular buffers with size of 5
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
        private AtomicIntegerArray buffers;
        private int nextIn = 0, nextOut = 0;
       
        
        
        /**Constructor will pass the size of the circular buffer
         * @param size: integer size of the circular buffers
        */
        public CircularBuffer( int size ){
            if( size <= 0 ){
                throw new IllegalArgumentException("number of slots can not be less than 0!");
            }
            //pass in the buffers size and initalize the array with values of 0's
            this.size = size;
            buffers = new AtomicIntegerArray(size) ;
        }

        
        //get() method return the value of atomic integer at given index
        public int get( int index ){
            return buffers.get(index%size);
        }
        
        
        //add method will add 1 to the element at the given index 
        //and return the updated value of the Atomic integer
        public int add( int index ){
            return buffers.incrementAndGet(index%size);
        }
        
        //reset method will reset to 0 to the element at the given index
        //and return the previous value of the Atomic integer
        public int reset( int index ) throws InterruptedException{
            if( buffers.get(index%size) <= 1)
                return buffers.getAndSet(index%size,0);
            else{
                System.out.println(ANSI_PURPLE + "Con: " + index + ", value: "+ buffers.get(index));
                throw new InterruptedException();
            }
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
            cpuBurstDuration = rand.nextInt( 20 ) + 5;
            sleepDuration = rand.nextInt(1000) + 1500;
        }

        @Override
        public void run() {
            System.out.println("Producer cpuBurstDuration === " + cpuBurstDuration);
            while(true){
                //calling add() method to add 1 to elements from index 0-range(k1)
                for( int i=0; i<cpuBurstDuration; i++ ){
                    System.out.println(ANSI_CYAN + "Pro: " + i + ", value: " + buffers.get(i) + 
                           ", add = "+ buffers.add(i) );
                
                    try{
                        //calling add() method to add 1 of slot 0-range(k1)
                        Thread.sleep(sleepDuration);

                    }catch(InterruptedException e){}
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
            sleepDuration = rand.nextInt(1000) + 500;
        }
        
       
        @Override
        public void run() {
            System.out.println("Consumer cpuBurstDuration === " + cpuBurstDuration);
            while( true ){
                //calling reset() method to reset elements to 0 from index 0-range(k2)
                for( int i=0; i<cpuBurstDuration; i++ ){

                    try{
                        System.out.println(ANSI_PURPLE + "Con: " + i + ", value: "+ buffers.reset(i)
                            +   ", reset = " + buffers.get(i));
                        Thread.sleep(sleepDuration);

                    }catch( InterruptedException e ){
                        System.out.println(ANSI_RED + "Uh oh! Race condition is happening in consumer at index = " + i);
                        System.exit(0);
                    }

                }
            }
        }
    }
}
