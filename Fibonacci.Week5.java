package cen3024c;

public class Fibonacci {
	public static int rFibFun(int count){
		
		if(count == 0) {
			return 0;
		}if(count == 1 || count == 2) {
			return 1;
		}
		return rFibFun(count-1) + rFibFun(count-2);
	}
	
	public static int dFibFun(int count) {
	    
	    int num[] = new int[count + 2];
	    num[0] = 0;
	    num[1] = 1;
	    
		    for(int i=2; i<= count;i++) {
		    	num[i] = num[i - 1] + num[i - 2];
		    }
			return num[count];
		}
		
	public static void main(String[] args) {
		long startTime;
		long endTime;
		long duration;
			
		System.out.println("\tRecursive\nTime\t\t| Input\t| Fibonacci Output");
				
		for (int input=40; input<45; input++){
			int output = rFibFun(input);
			startTime = System.nanoTime();
			rFibFun(input);
			endTime = System.nanoTime();
			duration = (endTime - startTime);
			
			System.out.printf("%sns\t| %s\t| %s\n", duration, input, output);
		}
		System.out.println("\n\tDynamic\nTime\t| Input\t| Fibonacci Output");
		
		for (int input=40; input<45; input++){
			int output = rFibFun(input);
			startTime = System.nanoTime();
			dFibFun(input);
			endTime = System.nanoTime();
			duration = (endTime - startTime);
			
			System.out.printf("%sns\t| %s\t| %s\n", duration, input, output);
		}
	}
}