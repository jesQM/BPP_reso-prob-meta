import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import domain.Instance;
import domain.Solution;
import util.AlgoUtil;
import util.FileUtil;

public class SingleExecution_Test {
	
	public static String instanceDirectory = null;
	
	// main instance
	public static void main(String[] args) {
		AlgoUtil.seed = new Random().nextLong();
		AlgoUtil.random = new Random(AlgoUtil.seed);
		
		AlgoUtil.instance = new Instance(args[0]);
			
		// Debug-----------------------
		Solution sol = Solver.multistartSolver(60);
//		Solution sol = Solver.localsearch(Solver.greedysearch(), 100, 2);
//		Solution sol = Solver.localsearch(Solver.onePerBinSolver(), 10000, 2);
//		Solution sol = Solver.localsearch(Solver.randomSolver(), 100, 2);
		AlgoUtil.displaySolution(sol);
//		AlgoUtil.displaySolution_2(sol);
		AlgoUtil.isSolutionForInstance_2(sol, AlgoUtil.instance);
		// Debug-----------------------
		
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
