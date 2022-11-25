import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import domain.Bin;
import domain.Solution;
import util.AlgoUtil;

public class Test {

	public static void main(String[] args) {
		
		Solution s = new Solution();
		Bin b = new Bin();
		// |3,3,3|5,2|4,3|5,4|
		b.addWidth(3);
		b.addWidth(3);
		b.addWidth(3);
		s.bins.add(b);
		
		b = new Bin();
		// |3,3,3|5,2|4,3|5,4|
		b.addWidth(5);
		b.addWidth(2);
		s.bins.add(b);
		
		b = new Bin();
		// |3,3,3|5,2|4,3|5,4|
		b.addWidth(4);
		b.addWidth(3);
		s.bins.add(b);
		
		b = new Bin();
		// |3,3,3|5,2|4,3|5,4|
		b.addWidth(5);
		b.addWidth(4);
		s.bins.add(b);
		
		List<Solution> v = vecindario(s);
		
		for (Solution solution : v) {
			System.out.println("Solution:");
			for (Bin bin : solution.bins) {
				System.out.print("|");
				for (int o : bin.orders) {
					System.out.print(o + " ");
				}
			}
			System.out.print("|");
			System.out.println("------------------------");
		}

	}

	private static int numberOfBinsToDestroy = 2;
	private static Solution vecinoBase;
	private static List<Integer> freeItems;
	
	private static void initVecindario(Solution best) {
		vecinoBase = best.clone();
		freeItems = new ArrayList<>();
		
		// Free the n-least filled bins (the ones with the lowest)
		Collections.sort(vecinoBase.bins, (a,b) -> a.width - b.width );
		
		for (int i = 0; i < numberOfBinsToDestroy && i < vecinoBase.bins.size(); i++) {
			Bin deleted = vecinoBase.bins.remove(0); // Free smallest one
			freeItems.addAll(deleted.orders); // Mark all elements as free
		}
	}
	
	private static List<Solution> vecindario(Solution best) {
		List<Solution> vecindario = new ArrayList<>();
		initVecindario(best); // Free the n-least filled bins
		
		/*	
			For bin in vecinoBase
				For freeItem in free items
					For order in bin
						if (freeItem > order && freeItem <= freeSpace + order)
							create neighbour
							replace item
							FFD neighbour
							add neighbour to list				
 		*/
		
		for (int i_bin = 0; i_bin < vecinoBase.bins.size(); i_bin++) {
			Bin bin = vecinoBase.bins.get(i_bin);
			
			//  ------------------ 1 by 1 free ------------------ 
			for (int i_freeItem = 0; i_freeItem < freeItems.size(); i_freeItem++) {
				Integer freeItem = freeItems.get(i_freeItem);
				
				for (int i_order = 0; i_order < bin.numOrders; i_order++) {
					Integer order = bin.orders.get(i_order);
					
					if (freeItem > order && freeItem <= 10 - bin.width + order) {
						// Clonamos todo, para no editar la base y destruir los iteradores ni futuros vecinos
						Solution vecino = vecinoBase.clone();
							// replace item
						vecino.bins.get(i_bin).removeWidth(order);
						vecino.bins.get(i_bin).addWidth(freeItem);
							// update free items List
						List<Integer> freeItems_vecino = AlgoUtil.cloneIntList(freeItems);
						freeItems_vecino.remove(i_freeItem);
						freeItems_vecino.add(order);
							// FFD
						vecindarioFFDFiller(vecino, freeItems_vecino);
							// Add to list
						vecindario.add(vecino);
					}
				}
			}
		}

		// Last neightbour, a FFD without swap
//		Solution vecino = vecinoBase.clone();
//		vecindario.add( vecindarioFFDFiller(vecino, freeItems) );
		
		return vecindario;
	}
	
	private static Solution vecindarioFFDFiller(Solution vecino, List<Integer> items) {
		// Sort decreasing
		Collections.sort(items, (a,b) -> b - a);
		
		// Iterations
		while (items.size() > 0) {
			int idx = 0;
			Integer width = items.get(idx);
			boolean added = false;
			
			for (Bin bin : vecino.bins) {
				if (10 - bin.width >= width) {
					bin.addWidth(width);
					added = true;
					break;
				}
			}
			
			if (!added) {
				Bin b = new Bin();
				vecino.bins.add(b);
				
				b.addWidth(width);
			}
			
			items.remove((int) idx);
		}
		
		return vecino;
	}
}
