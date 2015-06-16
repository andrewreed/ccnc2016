# Overview

This is the test code and results for the Stage 1 assessment.

## Usage

To compile and run the code, use the following commands:

    # javac -cp ".:../server/lib/javaml/javaml-0.1.7.jar" kdTreeRangeEval.java
    # java -cp ".:../server/lib/javaml/javaml-0.1.7.jar" kdTreeRangeEval ../server/db/Dataset_A_21_May.txt > kdTreeRangeEvalResults.txt

## Observing the Results

Each line of output represents the number of results returned for a single search of the kd-tree. Thus, _kdTreeRangeEvalResults.txt_ has 584,776 lines since there are 584,776 windows in Dataset A.

## Time to Run

This code takes approx. 10-20 minutes to run on Dataset A (I haven't measured the time, but this is in the ballpark).
