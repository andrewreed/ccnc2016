#!/usr/bin/env python

import sys

macDict = {}

for wifiFrame in sys.stdin:
  wifiFrame = wifiFrame.rsplit('\n') # remove trailing newlines
  fields = wifiFrame[0].split("\t")
  try:
    macAddr = fields[0]
    time = float(fields[1])
    size = int(fields[2])
    seqNum = int(fields[3])
  except ValueError, e:
    continue

  if macAddr not in macDict:
    macDict[macAddr] = [-100000, -100000, -100000, 1200]

  macData = macDict[macAddr]
  # lastTime = macData[0]
  # lastSize = macData[1]
  # lastSeqNum = macData[2]
  # maxSize = macData[3]

  if (macData[0] < 0.0):
    macData[0] = time
    macData[1] = size
    macData[2] = seqNum
  if (macData[2] > seqNum + 3400):
    seqNum += 4096
    #print "rolling"
  if (macData[2] >= seqNum):
    #print "skipping"
    continue
  if (seqNum - macData[2] > 200):
    continue
  
  numMissing = seqNum - macData[2] - 1
  if (numMissing > 0):
    missingSize = (size + macData[1]) / 2
    timeJump = (time - macData[0]) / (1.0 + numMissing)
    for x in range(numMissing):
      macData[0] += timeJump
      #print str(macData[0]) + "\t" + str(missingSize) + "\t" + "missing"
      print macAddr + "\t" + str(macData[0]) + "\t" + str(macData[3]) + "\t" + "missing"
  print wifiFrame[0]
  macData[0] = float(fields[1])
  macData[1] = int(fields[2])
  macData[2] = int(fields[3])
  if (macData[1] > macData[3]):
    macData[3] = macData[1]
