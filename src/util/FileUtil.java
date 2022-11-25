package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
*/

public class FileUtil {

	public static List<List<String>> readFiles(List<String> paths) throws IOException{
		List<List<String>> result = new ArrayList<>();
		
		for (String path : paths) {
			result.add(readFile(path));
		}
		
		return result;
	}
	
	public static List<String> readFile(String path) throws IOException{
		List<String> result = new ArrayList<>();
		
		BufferedReader input = new BufferedReader( new java.io.FileReader( path ) );
		
		try {
			
			String line;
			while( ( line = input.readLine() ) != null ){
				result.add(line);
			}
			
		} finally { input.close(); }
		
		return result;
	}
	
	public static void saveToFile(String filename, List<String> lines){
		BufferedWriter output = null;
		try {
			try {
				output = new BufferedWriter( new FileWriter( filename ) );
				for (String line : lines) {
					output.write(line + "\n");
				}
			} finally {
				if (output != null) output.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static void saveToFile(String filename, String line){
		BufferedWriter output = null;
		try {
			try {
				output = new BufferedWriter( new FileWriter( filename ) );
				output.write(line);
			} finally {
				if (output != null) output.close();
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	// ---------------------------------------------------------
	
	public static Integer loadedInstance_binSize = null;
	public static Integer loadedInstance_numberOfOrders = null;
	
	public static List<Integer> parseInstance(List<String> lines) {
		loadedInstance_binSize = null;
		loadedInstance_numberOfOrders = null;
		
		return loadBPPInstance(lines);
	}
	
	private static List<Integer> loadBPPInstance(List<String> lines) {
		int index = 2; // Skip first two lines
		
		loadedInstance_numberOfOrders = Integer.parseInt(lines.get(0)); // number of weights
		loadedInstance_binSize = Integer.parseInt(lines.get(1)); // width of bin
		
		// Parse orders
		List<Integer> orders = new ArrayList<>();
		for (int i = index; i < lines.size(); i++) {
			int width = Integer.parseInt(lines.get(i));
			orders.add(width);
		}
		
		return orders;
	}

	public static String fileName(String fullName) {
		String[] split = fullName.split("/");
		return split[split.length-1];
	}
}
