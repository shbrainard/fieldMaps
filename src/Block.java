import java.util.ArrayList;
import java.util.List;

public class Block {
	private final List<Sample> samples = new ArrayList<>();
	private final int size;
	private double currentRemainder = 0; 
	
	public Block(int size) {
		this.size = size;
	}
	
	public void addSamples(List<Sample> samples, double newRemainder) {
		this.samples.addAll(samples);
		currentRemainder = newRemainder;
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

	@Override
	public String toString() {
		return "Block [samples=" + samples + ", size=" + size + ", currentRemainder=" + currentRemainder + "]";
	}
}

