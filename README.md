
# Instructions

To run the tool:

java blockAssign.jar \<path to config file\>

The config file is a prefs file that specifies:

plant_file=\<path to csv with specific layout around cross ids and counts\>

output_file=\<path to write output csv\>

The blocks can be specified in two ways, either as sizes:

blocks=\<comma-separated list of block sizes, ex: 243,243,270,270\>

or dynamically chosen to be as equally sized as possible from a field layout, specified as

block_cols=\<number of blocks across the field\>

block_rows=\<number of blocks down the field\>

total_rows=\<number of rows in the field\>

plants_per_row=\<number of plants in each row\>

Note that this assumes the field is a rectangle - blocks cannot be generated dynamically for non-rectangular fields (in that case, you must specify the block sizes)
