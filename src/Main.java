import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import domain.Instance;
import domain.Solution;
import util.AlgoUtil;
import util.FileUtil;

public class Main {
	
	public static String instanceDirectory = null;
	
	// main instance
	public static void main(String[] args) {
		AlgoUtil.seed = new Random().nextLong();
		AlgoUtil.random = new Random(AlgoUtil.seed);
		
		AlgoUtil.instance = new Instance(args[0]);

//		// Debug-----------------------
//		Solution sol = Solver.greedysearch();
//		AlgoUtil.checkItIsSolution(sol, AlgoUtil.instance);
//		AlgoUtil.displaySolution(sol);
//		// Debug-----------------------
		
		
		AlgoUtil.mode = 0;
		long startTime, endTime;
		Solution solution, initialSolution, initialSolutionBackup;
		initialSolutionBackup =  Solver.randomSolver();
		int numberOfIterations = 10;
		
		
//		//# Greedy ----------------------------------------------------------
//		startTime = System.currentTimeMillis();
//		solution = Solver.greedysearch();
//		endTime = System.currentTimeMillis();
//		
//		solution.elapsedTime = endTime - startTime;
//		guardarFichero(AlgoUtil.instance, solution);
//		
//		long greedyTime = solution.elapsedTime; // Save for hybrid
//		Solution greedySolution = solution.clone();
//		
//		//# HC ----------------------------------------------------------
//		AlgoUtil.mode++;
//		initialSolution = initialSolutionBackup.clone();
//		
//		startTime = System.currentTimeMillis();
//		solution = Solver.localsearch(initialSolution, numberOfIterations, AlgoUtil.mode);
//		endTime = System.currentTimeMillis();
//		
//		solution.elapsedTime = endTime - startTime;
//		guardarFichero(AlgoUtil.instance, solution);
//		
//		//# GD ----------------------------------------------------------
//		AlgoUtil.mode++;
//		initialSolution = initialSolutionBackup.clone();
//		
//		startTime = System.currentTimeMillis();
//		solution = Solver.localsearch(initialSolution, numberOfIterations, AlgoUtil.mode);
//		endTime = System.currentTimeMillis();
//		
//		solution.elapsedTime = endTime - startTime;
//		guardarFichero(AlgoUtil.instance, solution);
//
//		//# Hybrid 1 -> Puedo reutilizar la solución que saqué del greedy o recalcularla (ojo, si reutilizo súmale el tiempo que tardó el greedy)
//		AlgoUtil.mode++;
//		initialSolution = greedySolution.clone();
//		
//		startTime = System.currentTimeMillis();
//		solution = Solver.localsearch(initialSolution, numberOfIterations, AlgoUtil.mode);
//		endTime = System.currentTimeMillis();
//		
//		solution.elapsedTime = (endTime - startTime) + greedyTime;
//		guardarFichero(AlgoUtil.instance, solution);
//		
//		//# Hybrid 2
//		AlgoUtil.mode++;
//		initialSolution = greedySolution.clone();
//		
//		startTime = System.currentTimeMillis();
//		solution = Solver.localsearch(initialSolution, numberOfIterations, AlgoUtil.mode);
//		endTime = System.currentTimeMillis();
//		
//		solution.elapsedTime = (endTime - startTime) + greedyTime;
//		guardarFichero(AlgoUtil.instance, solution);
		
		//# Multistart
		AlgoUtil.mode = 5;
		
		startTime = System.currentTimeMillis();
		solution = Solver.multistartSolver(60);
		endTime = System.currentTimeMillis();
		
		solution.elapsedTime = endTime - startTime;
		guardarFichero(AlgoUtil.instance, solution);
	}

	private static void guardarFichero(Instance instance, Solution solution) {
		/*
			filename:	instance_mode_fecha_hora_semilla.csv
	
			content:
				Que la última linea sea:
					solucion (bins); tiempo; núm vecinos generados; núm vecinos evaluados (no en greedy); iteraciones; LB; Ceil LB
		*/
		
		double LB = instance.demands.stream().reduce( (a,b) -> a+b ).get()/(double) instance.binSize;
		String filename = removeExtension(instance.name) + "_" + AlgoUtil.getModeName() + "_" + getDate() + "_" + AlgoUtil.seed + ".csv";
		StringBuilder content = new StringBuilder();
		content.append("bins;tiempo(ms);núm vecinos generados;núm vecinos evaluados;iteraciones;Theoretical LB;Ceil LB\n");
		content.append(solution.bins.size() + ";" + solution.elapsedTime + ";" + solution.numVecinosGenerados + ";" + solution.numEvaluaciones + ";" + solution.numIteraciones + ";" + LB + ";" + Math.ceil(LB));
		
		FileUtil.saveToFile(filename, content.toString());
	}
	
	private static String getDate() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH.mm.ss_dd.MM.yyyy");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	private static String removeExtension(String str) {
		return str.split("[.]")[0];
	}
}
