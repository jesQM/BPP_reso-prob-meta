package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.FileUtil;

public class Instance {

	public String fullName;
	public String name;
	
	public int binSize;
	
	public List<Integer> demands = new ArrayList<>();
	
	private Instance() { }
	
	public Instance(String fullName) {
		this.fullName = fullName;
		this.name = FileUtil.fileName(fullName);
				
		try {
			demands = FileUtil.parseInstance(FileUtil.readFile(fullName));
			binSize = FileUtil.loadedInstance_binSize;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public Instance clone() {
		Instance clone = new Instance();
		
		clone.fullName = this.fullName;
		clone.name = this.name;
		clone.binSize = this.binSize;
		
		for (int i = 0; i < demands.size(); i++) {
			clone.demands.add(demands.get(i));
		}
		
		return clone;
	}
}
