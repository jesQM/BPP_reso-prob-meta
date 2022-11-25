package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import domain.Bin;
import domain.Instance;
import domain.Solution;

public class AlgoUtil {

	public static String[] modeNames = {"Greedy", "HC", "GD", "Hybrid-HC", "Hybrid-GD", "Multistart"};
	public static int mode = 0; // 0, 1, 2, 3, 4, 5

	public static Instance instance = null;
	
	public static Random random;
	public static long seed;
	
	public static int getMode() {
		return mode;
	}
	
	public static String getModeName(){
		return modeNames[mode];
	}
	
	
	public static List<Integer> cloneIntList (List<Integer> list) {
		List<Integer> copy = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			copy.add(list.get(i));
		}
		return copy;
	}
	
	//---------------------------------------------------------------------------------
	public static void checkItIsSolution(Solution solution, Instance instance) {
		if (!AlgoUtil.isSolutionForInstance(solution, instance)) 
			throw new RuntimeException(solution + " is not solution of " + instance.name + ": " + instance);
	}

	
	private static boolean isSolutionForInstance(Solution solution, Instance instance) {
		Instance copy = instance.clone();
		for (Bin bin : solution.bins) {
			if (bin.freeSpace() < 0) return false;
			if (bin.width > instance.binSize) return false;
			if (bin.numOrders != bin.orders.size()) return false;
			
			for (Integer width : bin.orders) {
				boolean removed = false;
				for (int i = 0; i < copy.demands.size(); i++) {
					if (copy.demands.get(i) == width) {
						copy.demands.remove((int) i);
						removed = true;
						break;
					}
				}
				if (!removed) return false;
			}
		}
		
		if (copy.demands.size() != 0) return false;
		
		return true;
	}
	
	public static void isSolutionForInstance_2(Solution solution, Instance instance) {
		Instance copy = instance.clone();
		for (Bin bin : solution.bins) {
			if (bin.freeSpace() < 0) throw new RuntimeException();
			if (bin.width > instance.binSize) throw new RuntimeException();
			if (bin.numOrders != bin.orders.size()) throw new RuntimeException();
			
			for (Integer width : bin.orders) {
				boolean removed = false;
				for (int i = 0; i < copy.demands.size(); i++) {
					if (copy.demands.get(i) == width) {
						copy.demands.remove((int) i);
						removed = true;
						break;
					}
				}
				if (!removed) throw new RuntimeException();
			}
		}
		
		if (copy.demands.size() != 0) throw new RuntimeException();
	}

	//---------------------------------------------------------------------------------
	public static void displaySolution(Solution solution) {
		System.out.println("----------------------");
		for (Bin bin : solution.bins) {
			System.out.print("\t[");
			for (Integer width : bin.orders) {
				System.out.print(width + ", ");
			}
			System.out.println("] (" + bin.freeSpace() + ") (" + bin.numOrders + ")");
		}
		System.out.println("Bins: " + solution.bins.size() + " - Waste: " + solution.waste() + " - Iterations: " + solution.numIteraciones + " - Vecinos generados: " + solution.numVecinosGenerados + " - Evaluados: " + solution.numEvaluaciones);
	}
	
	public static void displaySolution_2(Solution solution) {
		System.out.println("----------------------");
		for (Bin bin : solution.bins) {
			for (Integer width : bin.orders) {
				System.out.println(width);
			}
		}
	}
}
