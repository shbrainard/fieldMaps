import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlantParser {

	/*
	 * Sample format, csv

		Cross_ID	Lois_ID	Seed_Parent	Pollen_Parent	original count	actual count	difference	Count
		MN_20_0102	1C	Arb7-1	Cuddy 2-28	60	60	0	60
	*/

	public static List<Sample> loadSamples(String inputFile) {
		List<Sample> result = new ArrayList<>();
		try {
			List<String> sampleDefs = Files.readAllLines(Paths.get(inputFile));
			for (String sampleDef : sampleDefs) {
				if (sampleDef.contains("Cross_ID")) {
					continue; // this is the header line
				}
				String[] cols = sampleDef.split(",", 0);
				String crossId = cols[0];
				String loisId = cols[1];
				String seedParent = cols[2];
				String pollenParent = cols[3];
				int actualCount = Integer.parseInt(cols[7]); 
				
				// Output will be a bunch of id info in csv and then block, just store that in the id for now
				for (int i = 0; i < actualCount; i++) {
					// not sure why the ids should start at 600, but matching the sample
					String sampleId = String.join(",", crossId + "_" + (600 + i), crossId, seedParent, pollenParent, loisId);
					result.add(new Sample(sampleId, crossId));
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	private static final String HEADER = "UID,Cross_ID,Seed_Parent,Pollen_Parent,Lois_ID,Block";

	public static void printBlocks(List<Block> blocks, String outputFile) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(outputFile))) {
			out.write(HEADER);
			out.newLine();
			
			for (int i = 0; i < blocks.size(); i++) {
				Block block = blocks.get(i);
				for (Sample sample : block.getSamples()) {
					out.write(sample.getId());
					out.write("," + (i + 1));
					out.newLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
