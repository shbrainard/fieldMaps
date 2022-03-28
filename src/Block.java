import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Block {
	private final List<Sample> samples = new ArrayList<>();
	private final int size;
	private double currentRemainder = 0; 
	private final List<String> rowPlantIds = new ArrayList<>();
	private final String desc;
	
	public Block(int size, String desc) {
		this.size = size;
		this.desc = desc;
	}
	
	public void addSamples(List<Sample> samples, double newRemainder) {
		this.samples.addAll(samples);
		currentRemainder = newRemainder;
	}
	
	public void addRowPlantIds(List<String> rowPlantIds) {
		this.rowPlantIds.addAll(rowPlantIds);
	}

	public String getDesc() {
		return desc;
	}

	public int getSize() {
		return size;
	}
	
	public double getCurrentRemainder() {
		return currentRemainder;
	}

	public List<Sample> getSamples() {
		return samples;
	}
	
	public List<String> getRandomizedPlantLocs() {
		Collections.shuffle(rowPlantIds);
		return rowPlantIds;
	}

	@Override
	public String toString() {
		return "Block [samples=" + samples + ", size=" + size + ", currentRemainder=" + currentRemainder + "]";
	}
}

