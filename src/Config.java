import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	
	private static final String INPUT_FILE = "plant_file";
	private static final String OUTPUT_FILE = "output_file";
	private static final String BLOCK_DEF = "blocks";
	private static final String BLOCK_COLS = "block_cols";
	private static final String BLOCK_ROWS = "block_rows";
	private static final String TOTAL_ROWS = "total_rows";
	private static final String PLANTS_PER_ROW = "plants_per_row";
	
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
			Map<String, String> props = new HashMap<>();
			List<String> contents = Files.readAllLines(configFile.toPath());
			for (String arg : contents) {
				String[] parsed = arg.split("=");
				props.put(parsed[0].toLowerCase(), parsed[1]);
			}	
			String inputFile = props.get(INPUT_FILE);
			String outputFile = props.get(OUTPUT_FILE);
			List<Block> blocks = new ArrayList<>();
			if (props.containsKey(BLOCK_DEF)) {
				String[] blockDefs = props.get(BLOCK_DEF).split(",", 0);
				for (String blockDef : blockDefs) {
					blocks.add(new Block(Integer.parseInt(blockDef)));
				}
			} else {
				int blockCols = Integer.parseInt(props.get(BLOCK_COLS));
				int blockRows = Integer.parseInt(props.get(BLOCK_ROWS));
				int fieldCols = Integer.parseInt(props.get(PLANTS_PER_ROW));
				int fieldRows = Integer.parseInt(props.get(TOTAL_ROWS));
				
				int minHeight = fieldCols / blockCols;
				int minWidth = fieldRows / blockRows;
				int nExtraHeight = fieldCols - minHeight * blockCols; // int math
				int nExtraWidth = fieldRows - minWidth * blockRows;
				
				// a block will occupy (roughly) i*minHeight to (i+1)*minHeight plants and j*minWidth to (j+1)*minWdith rows
				for (int i = 0; i < blockCols; i++) {
					for (int j = 0; j < blockRows; j++) {
						int startCol = Math.min(i, nExtraHeight) + i*minHeight;
						int endCol = startCol + minHeight + (i < nExtraHeight ? 1 : 0);
						int startRow = Math.min(j,  nExtraWidth) + j*minWidth;
						int endRow = startRow + minWidth + (j < nExtraWidth ? 1 : 0);
						List<String> blockLabels = createLabels(startCol, endCol, startRow, endRow);
						Block block = new Block(blockLabels.size());
						block.addRowPlantIds(blockLabels);
						blocks.add(block);
					}
				}
			}
			
			return new Config(inputFile, outputFile, blocks);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	private static List<String> createLabels(int startCol, int endCol, int startRow, int endRow) {
		List<String> labels = new ArrayList<>();
		for (int i = startCol; i < endCol; i++) {
			for (int j = startRow; j < endRow; j++) {
				labels.add((j+1) + "," + (i+1)); // rows and plants are 1-indexed
			}
		}
		return labels;
	}
}
