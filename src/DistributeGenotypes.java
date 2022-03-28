import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DistributeGenotypes {

	
	public static void main(String[] args) {
		File configFile = new File(args[0]);
		Config config = Config.parse(configFile);
		List<Sample> samples = PlantParser.loadSamples(config.getInputFile());
		List<Block> blocks = config.getBlocks();
		distributeToBlocks(samples, blocks);
		PlantParser.printBlocks(blocks, config.getOutputFile());
		
		printBlockDescription(config, blocks);
	}

	private static void printBlockDescription(Config config, List<Block> blocks) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(config.getBlockDescFile()))) {
			for (Block block : blocks) {
				out.write(block.getDesc());
				out.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void distributeToBlocks(List<Sample> samples, List<Block> blocks) {
		if (samples.isEmpty()) {
			return;
		}
		int totalSize = blocks.stream().mapToInt(block -> block.getSize()).sum();
		
		// heuristic: consider smaller genotypes first to improve the odds that we can rebalance
		// them to avoid 0-blocks (that's easier to do when the blocks haven't filled up already)
		List<List<Sample>> orderedGenotypes = reorderSamples(samples);
		
		for (List<Sample> oneGenotype : orderedGenotypes) {
			handleGenotype(oneGenotype, blocks, totalSize);
		}
	}
	
	private static List<List<Sample>> reorderSamples(List<Sample> samples) {
		List<List<Sample>> orderedGenotypes = new ArrayList<>();

		String currGenome = samples.get(0).getGenotype();
		List<Sample> oneGenotype = new ArrayList<>();

		for (Sample sample : samples) {
			if (!sample.getGenotype().equals(currGenome)) {
				orderedGenotypes.add(oneGenotype);
				oneGenotype = new ArrayList<>();
				currGenome = sample.getGenotype();
			}
			oneGenotype.add(sample);
		}
		orderedGenotypes.add(oneGenotype);
		
		Collections.sort(orderedGenotypes, (lista, listb) -> new Integer(lista.size()).compareTo(listb.size()));
		
		return orderedGenotypes;
	}

	private static void handleGenotype(List<Sample> oneGenotype, List<Block> blocks, int totalSize) {
		
		
		// we do a first-pass simple calculation of how many samples to put in each block proportionately
		List<Integer> samplesPerBucket = initialSampleDistribution(oneGenotype, blocks, totalSize);
		
		// Next, we're going to adjust those to try to prevent 0-size blocks. Since that adjustment requires a global view
		// (adding a sample to one block means taking it from another), we handle it in a second pass, and then store the actual
		// distribution of samples.
		for (int i = 0; i < samplesPerBucket.size(); i++) {
			if (samplesPerBucket.get(i) < 1) {
				// in degenerate cases we may not be able to load balance (fewer samples than blocks, more genotypes than blocks, or
				// just a large number of small genotypes in general). Use degree of block filledness as a proxy for the last two.
				if (oneGenotype.size() > blocks.size() && blocks.get(i).getSamples().size() < blocks.get(i).getSize() / 2) {
					// select another bucket that has at least 2 to steal from (we know such a bucket must exist b/c Pigeon Hole Principle)
					for (int j = 0; j < samplesPerBucket.size(); j++) {
						if (samplesPerBucket.get(j) > 1) {
							samplesPerBucket.set(j, samplesPerBucket.get(j) - 1);
							samplesPerBucket.set(i, 1);
							break;
						}
					}
				}
			}
		}
		
		
		// make sure we're assigning all and only all the samples; for example, 5 samples across 2 equal size buckets
		// needs to assign 5 total samples, not 6 (what you would get if you naively rounded 2.5 twice).
		// this is also used across genotypes to make sure we're not always shortchanging the same block
		double currentRemainder = 0; 
		int currSampleIndex = 0;
		
		for (int i = 0; i < blocks.size(); i++) {
			Block block = blocks.get(i);
            // include the previous genotype's error in the first calculation, so that it's not double-counted in this genotype's error tracking
			double numSamplesToBlockPrecise = (1.0 * block.getSize() * oneGenotype.size()) / totalSize + block.getCurrentRemainder();
			int numSamplesToBlockInt = samplesPerBucket.get(i);
			
			currentRemainder = numSamplesToBlockPrecise - numSamplesToBlockInt;
			
			block.addSamples(oneGenotype.subList(currSampleIndex, currSampleIndex + numSamplesToBlockInt), currentRemainder);
			currSampleIndex += numSamplesToBlockInt;
		}
	}

	private static List<Integer> initialSampleDistribution(List<Sample> oneGenotype, List<Block> blocks, int totalSize) {
		// make sure we're assigning all and only all the samples; for example, 5 samples across 2 equal size buckets
		// needs to assign 5 total samples, not 6 (what you would get if you naively rounded 2.5 twice).
		// this is also used across genotypes to make sure we're not always shortchanging the same block
		double currentRemainder = 0; 
		int currSampleIndex = 0;
		List<Integer> samplesPerBucket = new ArrayList<>();
		for (Block block : blocks) {
            // include the previous genotype's error in the first calculation, so that it's not double-counted in this genotype's error tracking
			double numSamplesToBlockPrecise = (1.0 * block.getSize() * oneGenotype.size()) / totalSize + block.getCurrentRemainder();
			
			// for very small genotype sizes relative to the number of buckets, this can go negative. Don't do that, just carry the rounding error
			// over until we find a larger genotype
			int numSamplesToBlockInt = Math.max(0, (int) (Math.round(numSamplesToBlockPrecise + currentRemainder)));

			// there are occasional edge cases where the error over or unders by 1 on the last block.
			// force it to always account for the last sample
			if (block == blocks.get(blocks.size() - 1)) {
				numSamplesToBlockInt = oneGenotype.size() - currSampleIndex;
			}
			
			samplesPerBucket.add(numSamplesToBlockInt);
			
			currentRemainder = numSamplesToBlockPrecise - numSamplesToBlockInt;
			
			currSampleIndex += numSamplesToBlockInt;
		}
		return samplesPerBucket;
	}
}
