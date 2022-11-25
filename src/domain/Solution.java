package domain;

import java.util.ArrayList;
import java.util.List;

public class Solution {
	public List<Bin> bins = new ArrayList<>();
	public long elapsedTime;
	
	public int numEvaluaciones = 0;
	public int numVecinosGenerados = 0;
	public int numIteraciones = 0;
	
	public int waste() {
		return bins
				.stream()
				.map( b -> b.freeSpace())
				.reduce( (a,b) -> a+b )
				.get();
	}
	
	public Solution clone() {
		Solution copy = new Solution();
		
		for (int i = 0; i < bins.size(); i++) {
			copy.bins.add(bins.get(i).clone());
		}
		
		copy.elapsedTime = this.elapsedTime;
		
		return copy;
	}


}
