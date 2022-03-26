import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestLayout {

	@Test
	public void testSimple() throws Exception {
		List<Block> blocks = createBlocks(10, 20);
		List<Sample> samples = createSamples(4,4,4);
		
		DistributeGenotypes.distributeToBlocks(samples, blocks);
		
		assert Math.abs(blocks.get(0).getCurrentRemainder()) < 0.1;
		assert blocks.get(0).getSamples().size() == 4;
		assert Math.abs(blocks.get(1).getCurrentRemainder()) < 0.1;
		assert blocks.get(1).getSamples().size() == 8;
	}
	
	@Test
	public void testMulti() throws Exception {
		List<Block> blocks = createBlocks(10, 20, 30);
		List<Sample> samples = createSamples(13, 13, 13);
		
		DistributeGenotypes.distributeToBlocks(samples, blocks);
		
		assert Math.abs(blocks.get(0).getCurrentRemainder()) < 0.6;
		assert Math.abs(blocks.get(0).getCurrentRemainder()) > 0.4;
		assert blocks.get(0).getSamples().size() == 6;
		assert Math.abs(blocks.get(1).getCurrentRemainder()) < 0.1;
		assert blocks.get(1).getSamples().size() == 13;
		assert Math.abs(blocks.get(2).getCurrentRemainder()) < 0.6;
		assert Math.abs(blocks.get(2).getCurrentRemainder()) > 0.4;
		assert blocks.get(2).getSamples().size() == 20;
	}
	
	
	@Test
	public void testReal() throws Exception {
		List<Block> blocks = createBlocks(243, 243, 270, 270);
		List<Sample> samples = createSamples(27, 3, 30, 15, 3);
		
		DistributeGenotypes.distributeToBlocks(samples, blocks);
		
		assert blocks.get(0).getSamples().size() == 18;
		assert blocks.get(1).getSamples().size() == 19;
		assert blocks.get(2).getSamples().size() == 20;
		assert blocks.get(3).getSamples().size() == 21;
	}
	
	
	private List<Sample> createSamples(int ...genotypeSizes) {
		List<Sample> samples = new ArrayList<>();
		int currGenotype = 0;
		int sampleId = 0;
		for (int size : genotypeSizes) {
			for (int i = 0; i < size; i++) {
				samples.add(new Sample("" + sampleId++, "" + currGenotype));
			}
			currGenotype++;
		}
		return samples;
	}
	
	private List<Block> createBlocks(int ...sizes) {
		List<Block> blocks = new ArrayList<>();
		for (int size : sizes) {
			blocks.add(new Block(size));
		}
		return blocks;
	}
}
