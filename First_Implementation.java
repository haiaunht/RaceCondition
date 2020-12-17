
package racecondition_haiau_bui;

import java.util.Random;
import static racecondition_haiau_bui.ThreadColor.ANSI_CYAN;
import static racecondition_haiau_bui.ThreadColor.ANSI_GREEN;
import static racecondition_haiau_bui.ThreadColor.ANSI_PURPLE;
import static racecondition_haiau_bui.ThreadColor.ANSI_RED;

/**
 *
 * @author HaiAu Bui,
 * CSD 415, Professor Abbott
 * There will be 4 different class with its own main() method
 * This is the first implementation, DO NOT include ANY SYNCHRONIZATION
 */
public class First_Implementation {
    
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(ANSI_GREEN + "========================---------===============");
        System.out.println(ANSI_GREEN + "======= NO SYNCHRONIZED - IMPLEMENTATION =======");
        System.out.println(ANSI_GREEN + "==========================---------=============");
        
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
        public int size;
        public int[] buffers;


        public CircularBuffer( int size ){
            if( size <= 0 ){
                throw new IllegalArgumentException("number of slots can not be less than 0!");
            }
            //pass in the buffers size and initalize the array with values of 0's
            this.size = size;
            buffers = new int[this.size];     
        }
        
        /**
         * get() method return the value of element at specify index 
         * @param index: integer 
         * @return the value of element at index
         */
        public int get( int index ){
            return buffers[index%size];
        }
        
        //add() will add 1 to the element at given index
        public void add( int index ){
            buffers[index%size] += 1;
        }
        
        //reset() will reset the element at given index = 0
        public void reset( int index ){
            buffers[index%size] = 0;
        }

    }
    
    
    //Producer class
    private static class Producer extends Thread {
        private CircularBuffer buffers;
        private int cpuBurstDuration;
        private int sleepDuration;    
        Random rand = new Random();

        //contructor will pass the param is the size of the circular buffer
        public Producer ( CircularBuffer boundedBuffer ){
            this.buffers = boundedBuffer;
            cpuBurstDuration = rand.nextInt(20) + 5;
            sleepDuration = rand.nextInt(10) + 5;
        }

        @Override
        public void run() {        
            try{
                System.out.println("Producer cpuBurstDuration: " + cpuBurstDuration);

                //go through a circular queue from 0->rand_k1 to increase 1
                //if the index > length-1, modulo to have the circular next index
                for( int i = 0; i<cpuBurstDuration; i++ ){
                    int value = buffers.get(i);
                    buffers.add(i);
                    System.out.println( ANSI_CYAN + "Pro at " + i  + ", value = " + value + ", add = " +  buffers.get(i));
                }

                //sleep at random sleepDuration given above
                Thread.sleep(sleepDuration);

            }catch(InterruptedException e){}

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
            sleepDuration = rand.nextInt(10) + 5;
        }

        @Override
        public void run() {
            try{
                Thread.sleep(sleepDuration);

                System.out.println("Consumer cpuBurstDuration: " + cpuBurstDuration);

                //consumer if set any 1's to 0, if value>1, race condidtion occur -> then STOP
                for( int i=0; i<cpuBurstDuration; i++ ){
                    int value = buffers.get(i);
                    
                    buffers.reset(i);
                    System.out.println( ANSI_PURPLE + "Con at " + i +", value = " + value + ", set = " + buffers.get(i));
                    
                    //if race condition happed at index, display error message at the index and exit
                    if( value > 1){
                        System.out.println(ANSI_RED + "Uh Oh!!! The race condition is happening in Consumer at index " + i );
                        System.exit(0);
                    }
                    
                }
                
            }catch(InterruptedException e){ }       
        }


    }
}
    




