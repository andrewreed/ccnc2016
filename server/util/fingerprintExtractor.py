#!/usr/bin/env python

import sys

for line in sys.stdin:
  line = line.rsplit('\n')
  tokens = line[0].split("\t")
  title = tokens[0]
  bitrates = tokens[1].split(",")
  sizes = tokens[2].split(",")
  numBitrates = len(bitrates)
  numSegments = len(sizes)/numBitrates

  offset = 0

  for bitrate in bitrates:
    outputString = title + "\t" + bitrate + "\t" + sizes[offset]

    for i in range(1,numSegments):
      outputString += "," + sizes[i * numBitrates + offset]

    print outputString
    offset += 1
    
