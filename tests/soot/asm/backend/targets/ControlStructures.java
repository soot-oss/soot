package soot.asm.backend.targets;

import java.util.ArrayList;
import java.util.List;

public class ControlStructures {
	
	List<Integer> result;

	protected List<Integer> get(int i){
		result = new ArrayList<Integer>();
		
		switch(i){
		case 1:
			result.add(1);
			break;
		case 2:
			result.add(2);
		case 3:
			result.add(3);
			break;
		default:
			result.add(null);
		}
		
		switch(i){
		case 1:
			result.add(1);
		case 10:
			result.add(10);
		case 100:
			result.add(100);
		case 1000:
			result.add(1000);
		default:
			result.add(null);
		}
		
		return result;
	}
	
}
