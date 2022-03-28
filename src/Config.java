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
	private static final String BLOCK_DESC_OUT = "block_description_output_file";
	private static final String BLOCK_DEF = "blocks";
	private static final String BLOCK_COLS = "block_cols";
	private static final String BLOCK_ROWS = "block_rows";
	private static final String ROW_RANGE = "row_range";
	private static final String PLANTS_PER_ROW_RANGE = "plants_per_row_range";
	
	private final String inputFile;
	private final List<Block> blocks;
	private final String outputFile;
	private final String blockDescFile;

	public Config(String inputFile, String outputFile, List<Block> blocks, String blockDescFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.blocks = blocks;
		this.blockDescFile = blockDescFile;
	}

	public String getBlockDescFile() {
		return blockDescFile;
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
			
			List<Block> blocks = new ArrayList<>();
			if (props.containsKey(BLOCK_DEF)) {
				String[] blockDefs = props.get(BLOCK_DEF).split(",", 0);
				for (String blockDef : blockDefs) {
					blocks.add(new Block(Integer.parseInt(blockDef), "Block " + (blocks.size() + 1) + " of size " + blockDef));
				}
			} else {
				int blockCols = Integer.parseInt(props.get(BLOCK_COLS));
				int blockRows = Integer.parseInt(props.get(BLOCK_ROWS));
				String[] rowDef = props.get(ROW_RANGE).split(",", 0);
				String[] colDef = props.get(PLANTS_PER_ROW_RANGE).split(",", 0);
				int globalStartCol = Integer.parseInt(colDef[0]);
				int globalStartRow = Integer.parseInt(rowDef[0]);
				int fieldCols = Integer.parseInt(colDef[1]) - globalStartCol + 1;
				int fieldRows = Integer.parseInt(rowDef[1]) - globalStartRow + 1;
				
				int minHeight = fieldCols / blockCols;
				int minWidth = fieldRows / blockRows;
				int nExtraHeight = fieldCols - minHeight * blockCols; // int math
				int nExtraWidth = fieldRows - minWidth * blockRows;
				
				// a block will occupy (roughly) i*minHeight to (i+1)*minHeight plants and j*minWidth to (j+1)*minWdith rows
				for (int i = 0; i < blockCols; i++) {
					for (int j = 0; j < blockRows; j++) {
						int startCol = Math.min(i, nExtraHeight) + i*minHeight + globalStartCol;
						int endCol = startCol + minHeight + (i < nExtraHeight ? 1 : 0);
						int startRow = Math.min(j,  nExtraWidth) + j*minWidth + globalStartRow;
						int endRow = startRow + minWidth + (j < nExtraWidth ? 1 : 0);
						List<String> blockLabels = createLabels(startCol, endCol, startRow, endRow);
						String blockDesc = "Block " + (blocks.size() + 1) + " rows " + startRow 
								+ " to " + (endRow - 1) + " inclusive, plants within row " + startCol + " to " + (endCol - 1) + " inclusive";
						Block block = new Block(blockLabels.size(), blockDesc);
						block.addRowPlantIds(blockLabels);
						blocks.add(block);
					}
				}
			}
			
			return new Config(props.get(INPUT_FILE), props.get(OUTPUT_FILE), blocks, props.get(BLOCK_DESC_OUT));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	private static List<String> createLabels(int startCol, int endCol, int startRow, int endRow) {
		List<String> labels = new ArrayList<>();
		for (int i = startCol; i < endCol; i++) {
			for (int j = startRow; j < endRow; j++) {
				labels.add(j + "," + i); 
			}
		}
		return labels;
	}
}
