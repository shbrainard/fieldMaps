import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DistributeGenotypes {

	
	public static void main(String[] args) {
		File configFile = new File(args[0]);
		Config config = Config.parse(configFile);
		List<Sample> samples = PlantParser.loadSamples(config.getInputFile());
		List<Block> blocks = config.getBlocks();
		distributeToBlocks(samples, blocks);
		PlantParser.printBlocks(blocks, config.getOutputFile());
	}
	
	public static void distributeToBlocks(List<Sample> samples, List<Block> blocks) {
		if (samples.isEmpty()) {
			return;
		}
		int totalSize = blocks.stream().mapToInt(block -> block.getSize()).sum();
		
		String currGenome = samples.get(0).getGenotype();
		List<Sample> oneGenotype = new ArrayList<>();
		
		for (Sample sample : samples) {
			if (!sample.getGenotype().equals(currGenome)) {
				handleGenotype(oneGenotype, blocks, totalSize);
				oneGenotype.clear();
				currGenome = sample.getGenotype();
			}
			oneGenotype.add(sample);
		}
		
		handleGenotype(oneGenotype, blocks, totalSize);
	}
	
	private static void handleGenotype(List<Sample> oneGenotype, List<Block> blocks, int totalSize) {
		int currSampleIndex = 0;
		
		// make sure we're assigning all and only all the samples; for example, 5 samples across 2 equal size buckets
		// needs to assign 5 total samples, not 6 (what you would get if you naively rounded 2.5 twice).
		// this is also used across genotypes to make sure we're not always shortchanging the same block
		double currentRemainder = 0; 
		
		for (Block block : blocks) {
            // include the previous genotype's error in the first calculation, so that it's not double-counted in this genotype's error tracking
			double numSamplesToBlockPrecise = (1.0 * block.getSize() * oneGenotype.size()) / totalSize + block.getCurrentRemainder();
			int numSamplesToBlockInt = (int) (Math.round(numSamplesToBlockPrecise + currentRemainder));

			// there are occasional edge cases where the error over or unders by 1 on the last block.
			// force it to always account for the last sample
			if (block == blocks.get(blocks.size() - 1)) {
				numSamplesToBlockInt = oneGenotype.size() - currSampleIndex;
			}
			
			currentRemainder = numSamplesToBlockPrecise - numSamplesToBlockInt;
			assert Math.abs(currentRemainder) < 1;
			
			block.addSamples(oneGenotype.subList(currSampleIndex, currSampleIndex + numSamplesToBlockInt), currentRemainder);
			currSampleIndex += numSamplesToBlockInt;
		}
		
		
	}
}
