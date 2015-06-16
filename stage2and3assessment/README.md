# Overview

This is the code used for the Stage 2 and 3 assessments.

## Usage

To run the test on Dataset A, open __dbCheck.java__ and change __NUM_MOVIES__ to 400. Then compile and run the code with the following commands:

    # javac -cp ".:../server/lib/Apache/commons-lang3-3.3.2.jar:../server/lib/Apache/commons-math3-3.3.jar" dbCheck.java
    # java -cp ".:../server/lib/Apache/commons-lang3-3.3.2.jar:../server/lib/Apache/commons-math3-3.3.jar" dbCheck ../server/db/Dataset_A_21_May.txt allPairs_Dataset_A.txt > Dataset_A_stage2and3assessment.txt

To run the test on Dataset B, open __dbCheck.java__ and change __NUM_MOVIES__ to 104. Then compile and run the code with the following commands:

    # javac -cp ".:../server/lib/Apache/commons-lang3-3.3.2.jar:../server/lib/Apache/commons-math3-3.3.jar" dbCheck.java
    # java -cp ".:../server/lib/Apache/commons-lang3-3.3.2.jar:../server/lib/Apache/commons-math3-3.3.jar" dbCheck ../server/db/Dataset_B_22_May.txt allPairs_Dataset_B.txt > Dataset_B_stage2and3assessment.txt

## Observing the Results

The results are designed to be viewed/analyzed in Excel (Data > From Text).

Suggested Sort:

1. Sort By: Column A, Smallest to Largest
2. Then By: Column I, Largest to Smallest

If Column A shows a 1, then the row shows the statistics for a single Stage 3 false positive. The columns are:

* Column B: Movie A's title
* Column C: Movie A's bitrate
* Column D: Movie B's title
* Column E: Movie B's bitrate
* Column F: The start index of the first window in Movie A
* Column G: The start index of the first window in Movie B
* Column H: The offset of the second window in each movie

If Column A shows a 2, then the row shows the statistics for all of the window vs. window comparisons between two videos. The columns are:

* Column B: Movie A's title
* Column C: Movie A's bitrate
* Column D: Movie B's title
* Column E: Movie B's bitrate
* Column F: Number of window vs. window comparisons
* Column G: Number of comparisons that are within +/- 2% of the 1st dimension
* Column H: Number of comparisons that are within +/- 2% of the 1st dimension and +/- 0.015 of dimensions 2-6
* Column I: Number of comparisons that are within +/- 2% of the 1st dimension, +/- 0.015 of dimensions 2-6, and > 0.97 correlated for the 4th derivative
* Column J: Time (in milliseconds) to conduct all window vs. window comparisons for the two movies
