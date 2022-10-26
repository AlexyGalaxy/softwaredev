package cen3024c;
import java.util.Random;

public class ConcurrencyMultiThreading {
/* Make an array of 200 million random numbers between 1 and 10. Compute the sum in parallel using multiple 
 * threads. Then compute the sum with only one thread, and display the sum and times for both cases.
 */

	private static int[] countArray = new int[200000001]; //both for loops are set to i=1
	private static int countA = 0;
	private static int rand_number=0;
		
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long duration = (endTime - startTime);
	
	private static long startTime2 = System.currentTimeMillis();
	private static long endTime2 = System.currentTimeMillis();
	private static long duration2 = (endTime - startTime);
	
	public static void main(String[] args) throws InterruptedException{ 
		System.out.println("Calculating 200 million Array Elements. Please Wait...\n");
		
				
		class arrayThread implements Runnable{
		
			@Override
			public void run() {
				startTime = System.nanoTime();
				for(int i=1; i<countArray.length; i++) {
							
					Random random =new Random();
					int  rand_number = random.nextInt(11);
					
					while (rand_number %2 !=0 || rand_number == 0) {
						rand_number = random.nextInt(11);
					}
					
				countArray[i] = rand_number;
				countA += countArray[i];
				}
				
			endTime =  System.nanoTime();
			duration = (endTime - startTime);
			System.out.println("Array Thread Calculated: "+ countA +" in "+ duration +" ms.");
			}
		}
				
		class sumThread implements Runnable{
		
			@Override
			public void run() {
				startTime2 = System.nanoTime();
				for(int i=1; i<countArray.length; i++) {
					countArray[i] = rand_number;
					countA += countArray[i];
				}
				
			endTime2 =  System.nanoTime();
			duration2 = (endTime2 - startTime2);
			System.out.println("\nSum Thread Calculated:\t "+ countA +" in "+ duration2 +" ms.");
			}	
		}
		
		arrayThread runnable1 = new arrayThread();
		Thread thread1 = new Thread(runnable1);
		
		sumThread runnable2 = new sumThread();
		Thread thread2 = new Thread(runnable2);
		
		thread1.start();
		thread1.join();
		thread2.start();
	}
}