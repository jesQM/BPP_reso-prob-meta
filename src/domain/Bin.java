package domain;

import java.util.ArrayList;
import java.util.List;

import util.AlgoUtil;

public class Bin {

	public List<Integer> orders = new ArrayList<>();
	
	public int width = 0;
	public int numOrders = 0;
	
	public void addWidth(int width) {
		orders.add(width);
		this.width += width;
		numOrders++;
	}
	
	public void removeWidth(int width) {
		
		boolean removed = false;
		for (int i = 0; i < orders.size(); i++) {
			if (orders.get(i) == width) {
				orders.remove((int) i);
				removed = true;
				break;
			}
		}
		
		if (!removed) throw new RuntimeException();
		
		this.width -= width;
		numOrders--;
	}
	
	public int freeSpace() {
		return AlgoUtil.instance.binSize - this.width;
	}
	
	//-------------------------------------------------
	public Bin clone() {
		Bin copy = new Bin();
		
		for (int i = 0; i < orders.size(); i++) {
			copy.addWidth(orders.get(i));
		}
		
		return copy;
	}
}
