package logic;

public class WeightedMusic implements Comparable<WeightedMusic> {

	public int id;
	public int weight;
	
	public WeightedMusic(int id, int weight) {
		this.id = id;
		this.weight = weight;
	}
	@Override
	public int compareTo(WeightedMusic o) {
		// TODO Auto-generated method stub
		if(weight > o.weight) {
			return 1;
		} else if(weight == o.weight) {
			return 0;
		} else {
			return -1;
		}
	}
	
}
