
Distribute progeny families of potentially unequal size across an arbitrary number of blocks which may have uneven dimensions.  Goal is the proportionally distribute each genotype as closely as possible the ratio of the sizes of the blocks. 

# Instructions

To run the tool:

`java -cp blockAssign.jar DistributeGenotypes \<path to config file\>`

The config file is a prefs file that specifies:

`plant_file=\<path to csv with specific layout around cross ids and counts\>`

This can have lots of columns with identifying information that will be retained, but must have a column called "Cross_ID" and a column called "Counts"

`output_file=\<path to write output csv\>`

`block_description_output_file=\<path to write block descriptions to\>`

The blocks can be specified in two ways, either as sizes:

`blocks=\<comma-separated list of block sizes, ex: 243,243,270,270\>`

or dynamically chosen to be as equally sized as possible from a field layout, specified as

`block_cols=\<number of blocks across the field rows\>`

`block_rows=\<number of blocks down the field rows\>`

`row_range=\<start row number\>,\<end row number (inclusive)\>`

`plants_per_row_range=\<start plant position in row\>,\<end plant position in row (inclusive)\>`

Note that this assumes the field is a rectangle - blocks cannot be generated dynamically for non-rectangular fields (in that case, you must specify the block sizes)
