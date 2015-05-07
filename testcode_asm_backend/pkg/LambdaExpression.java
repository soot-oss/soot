package pkg;

import java.util.function.BiFunction;

public class LambdaExpression {
	
	public static boolean compare(BiFunction<Integer, Integer, Boolean> greaterThan, int a, int b){
		return greaterThan.apply(a, b);
	}

	public static void main (String [] args) {
		compare((a, b)->a>b, 1, 0);;
	}
	
}
