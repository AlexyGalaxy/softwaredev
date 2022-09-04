package cop2805;

import java.util.*;

public class Week3 {

	public static <E extends Comparable<E>> E max (E [] list) {
	E maxElement = list[0];
		
		for(E loop : list) {
			if(loop.compareTo(maxElement) > 0){
				 maxElement=loop;
			}
		}
		return maxElement;
	}
	
	public static void main(String[] args) {
		
		String[] colors = {"Red","Green","Blue"};
		Integer[] numbers = {1, 2, 3};
		Double[] circleRadius = {3.0, 5.9, 2.9};
		
		System.out.println("Colors: " + max(colors));
		System.out.println("Numbers: " + max(numbers));
		System.out.println("Circle Radius: " + max(circleRadius));
	}
}