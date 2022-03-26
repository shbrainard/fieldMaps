
public class Sample {

	private final String id;
	private final String genotype;
	
	public Sample(String id, String genotype) {
		this.id = id;
		this.genotype = genotype;
	}

	public String getGenotype() {
		return genotype;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Sample [id=" + id + ", genotype=" + genotype + "]";
	}

}
