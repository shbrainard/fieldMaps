import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Config {
	
	private static final String INPUT_FILE = "plant_file";
	private static final String OUTPUT_FILE = "output_file";
	private static final String BLOCK_DEF = "blocks";
	
	private final String inputFile;
	private final List<Block> blocks;
	private final String outputFile;

	public Config(String inputFile, String outputFile, List<Block> blocks) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.blocks = blocks;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public String getInputFile() {
		return inputFile;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public static Config parse(File configFile) {
		try {
			List<String> contents = Files.readAllLines(configFile.toPath());
			String inputFile = "";
			String outputFile = "";
			List<Block> blocks = new ArrayList<>();
			for (String str : contents) {
				String[] split = str.split("=", 0);
				if (split[0].equals(INPUT_FILE)) {
					inputFile = split[1];
				} else if (split[0].equals(BLOCK_DEF)) {
					String[] blockDefs = split[1].split(",", 0);
					for (String blockDef : blockDefs) {
						blocks.add(new Block(Integer.parseInt(blockDef)));
					}
				} else if (split[0].equals(OUTPUT_FILE)) {
					outputFile = split[1];
				} else {
					System.out.println("unrecognized option " + split[0]);
				}
			}
			return new Config(inputFile, outputFile, blocks);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
