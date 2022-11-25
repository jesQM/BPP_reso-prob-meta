import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import domain.Bin;
import domain.Instance;
import domain.Solution;
import util.AlgoUtil;

public class Solver {

	private static int numberOfBinsToDestroy = 4;
	
	public static Solution localsearch(Solution initial, int iterations, int mode) {
		Solution best = initial;
		List<Solution> vecindario;
		int iterationCount = 0;
		
		int numEvaluaciones = 0;
		int numVecinosGenerados = 0;

		while( iterationCount < iterations ) {
			iterationCount++;
			boolean mejora = false;
			vecindario = generarVecindario(best);
			numVecinosGenerados += vecindario.size();
			
			for (Solution vecino : vecindario) {
//				AlgoUtil.displaySolution(vecino);
				numEvaluaciones++;
				if ( (fitness_falkenauer(vecino) > fitness_falkenauer(best)) ) {
					mejora = true;
					best = vecino;
					if (mode == 1) break;
				}
			}
			
			if (!mejora) break; // No improving neighbor -> local optimum
		}
		
		best.numIteraciones = iterationCount;
		best.numEvaluaciones = numEvaluaciones;
		best.numVecinosGenerados = numVecinosGenerados;

		return best;
	}

	private static List<Solution> generarVecindario(Solution best) {
		List<Solution> result = vecindario(best);
		return result;
		
		/*
		
		Alvim et al:
			each bin of the current solution is destroyed successively, and its contents are spread over the other bins
				if -> solution is feasible -> done, 1 less bin
				else -> solution non feasible ->
					pairs of bins are investigated and its items are redistributed among themselves
		
		Falkenauer’s HGGA:
			1. a number of items of the initial solution are made free (on ACO paper, the n less-filled bins are destroyed)
			2. replace up to three items in each of the existing bins of the solution by one or two of the free items
			3. After all bins have been examined, the remaining free items are added to the solution using the FFD heuristic
			
		*/
	}

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
			

			// ------------------ 2 by 2 free ------------------ 
			for (int i_freeItem1 = 0; i_freeItem1 < freeItems.size(); i_freeItem1++) {
				Integer freeItem1 = freeItems.get(i_freeItem1);
				for (int i_freeItem2 = 0; i_freeItem2 < freeItems.size(); i_freeItem2++) {
					Integer freeItem2 = freeItems.get(i_freeItem2);
					
					if (i_freeItem1 == i_freeItem2) continue; // Skip if same item
					
					for (int i_order1 = 0; i_order1 < bin.numOrders; i_order1++) {
						Integer order1 = bin.orders.get(i_order1);
						for (int i_order2 = 0; i_order2 < bin.numOrders; i_order2++) {
							Integer order2 = bin.orders.get(i_order2);
							
							if (i_order1 == i_order2) continue;
							// skip if permutation of items
							
							if (freeItem1+freeItem2 > order1+order2 && freeItem1+freeItem2 <= bin.freeSpace() + order1+order2) {
								Solution vecino = vecinoBase.clone();
									// replace item
								vecino.bins.get(i_bin).removeWidth(order1);
								vecino.bins.get(i_bin).removeWidth(order2);
								vecino.bins.get(i_bin).addWidth(freeItem1);
								vecino.bins.get(i_bin).addWidth(freeItem2);
									// update free items List
								List<Integer> freeItems_vecino = AlgoUtil.cloneIntList(freeItems);
								List<Integer> idxs = new ArrayList<>();
								idxs.add(i_freeItem1);
								idxs.add(i_freeItem2);
								Collections.sort(idxs); // Remove last one first, to not destroy the order of the list
								freeItems_vecino.remove((int) idxs.get(1));
								freeItems_vecino.remove((int) idxs.get(0));
								freeItems_vecino.add(order1);
								freeItems_vecino.add(order2);
									// FFD
								vecindarioFFDFiller(vecino, freeItems_vecino);
									// Add to list
								vecindario.add(vecino);
							}
						}
					}
				}
			}
			
			//  ------------------ 2 by 1 free ------------------ 
			for (int i_freeItem = 0; i_freeItem < freeItems.size(); i_freeItem++) {
				Integer freeItem = freeItems.get(i_freeItem);
				
				for (int i_order1 = 0; i_order1 < bin.numOrders; i_order1++) {
					Integer order1 = bin.orders.get(i_order1);
					for (int i_order2 = 0; i_order2 < bin.numOrders; i_order2++) {
						Integer order2 = bin.orders.get(i_order2);
						
						if (i_order1 == i_order2) continue;
						
						if (freeItem > order1+order2 && freeItem <= bin.freeSpace() + order1+order2) {
							Solution vecino = vecinoBase.clone();
								// replace item
							vecino.bins.get(i_bin).removeWidth(order1);
							vecino.bins.get(i_bin).removeWidth(order2);
							vecino.bins.get(i_bin).addWidth(freeItem);
								// update free items List
							List<Integer> freeItems_vecino = AlgoUtil.cloneIntList(freeItems);
							freeItems_vecino.remove(i_freeItem);
							freeItems_vecino.add(order1);
							freeItems_vecino.add(order2);
								// FFD
							vecindarioFFDFiller(vecino, freeItems_vecino);
								// Add to list
							vecindario.add(vecino);
						}
					}
				}
			}
			
			//  ------------------ 1 by 1 free ------------------ 
			for (int i_freeItem = 0; i_freeItem < freeItems.size(); i_freeItem++) {
				Integer freeItem = freeItems.get(i_freeItem);
				
				for (int i_order = 0; i_order < bin.numOrders; i_order++) {
					Integer order = bin.orders.get(i_order);
					
					if (freeItem > order && freeItem <= bin.freeSpace() + order) {
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
		Solution vecino = vecinoBase.clone();
		vecindario.add( vecindarioFFDFiller(vecino, freeItems) );
		
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
				if (bin.freeSpace() >= width) {
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
		
		AlgoUtil.isSolutionForInstance_2(vecino, AlgoUtil.instance);
		return vecino;
	}
	

	// FFD - First Fit Decreasing
	public static Solution greedysearch() {
		Solution sol = new Solution();
		Instance copy = AlgoUtil.instance.clone();
		
		Collections.sort(copy.demands, (a,b) -> b - a);
		while (copy.demands.size() > 0) {
			int idx = 0;
			Integer width = copy.demands.get(idx);
			boolean added = false;
			
			for (Bin bin : sol.bins) {
				if (bin.freeSpace() >= width) {
					bin.addWidth(width);
					added = true;
					break;
				}
			}
			
			if (!added) {
				Bin b = new Bin();
				sol.bins.add(b);
				
				b.addWidth(width);
			}
			
			copy.demands.remove((int) idx);
		}
		
		return sol;
	}
	
	public static Solution randomSolver() {
		Solution sol = new Solution();
		Instance copy = AlgoUtil.instance.clone();
		
		while (copy.demands.size() > 0) {
			int idx = AlgoUtil.random.nextInt(copy.demands.size());
			Integer width = copy.demands.get(idx);
			boolean added = false;
			
			for (Bin bin : sol.bins) {
				if (bin.freeSpace() >= width) {
					bin.addWidth(width);
					added = true;
					break;
				}
			}
			
			if (!added) {
				Bin b = new Bin();
				sol.bins.add(b);
				
				b.addWidth(width);
			}
			
			copy.demands.remove((int) idx);
		}
		
		return sol;
	}
	
	public static Solution onePerBinSolver() {
		Solution sol = new Solution();
		Instance copy = AlgoUtil.instance.clone();
		
		for (Integer width : copy.demands) {
			Bin b = new Bin();
			sol.bins.add(b);
			b.addWidth(width);
		}
		
		return sol;
	}
	
	public static Solution multistartSolver(int timeoutSeconds) {
		int timeoutsec = timeoutSeconds;
		long start = System.currentTimeMillis();
		Solution best = localsearch(randomSolver(), 10, 2);
		
		while (start + timeoutsec*1000 > System.currentTimeMillis()) {
			Solution lsa = localsearch(randomSolver(), 10, 2);
			
			if ( best.bins.size() > lsa.bins.size() ) {
				//System.out.println("New best, old: " + best.bins.size() + " - new: " + lsa.bins.size());
				best = lsa;
			}
		}
		
		return best;
	}
	
	// ------------------- FITNESS --------------------
	
	public static double fitness_bins(Solution s) {
		return 1/(double) s.bins.size();
	}
	
	// TODO; can be used?
	public static double fitness_falkenauer(Solution s) {
		double k = 2;
		double C = AlgoUtil.instance.binSize;
		double sumatorio = 0;
		
		for (Bin bin : s.bins) {
			double F = bin.width;
			sumatorio = Math.pow(F/C, k);
		}
		
		return sumatorio/(double) s.bins.size();
	}
}
