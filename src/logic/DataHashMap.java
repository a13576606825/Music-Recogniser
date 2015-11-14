package logic;

import java.util.List;
import java.util.Map;

public class DataHashMap {

	private Map<Long, List<DataPoint>> dataHashMap;
	public DataHashMap(Map<Long, List<DataPoint>> dataHashMap) {
		this.dataHashMap = dataHashMap;
	}
	public Map<Long, List<DataPoint>> getDataHashMap(){ 
		return dataHashMap;
	}
	
}
