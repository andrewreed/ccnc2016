#!/usr/bin/env python

import sys

lastTime = -100000
firstFrame = 1

for wifiFrame in sys.stdin:
  wifiFrame = wifiFrame.rsplit('\n') # remove trailing newlines
  fields = wifiFrame[0].split("\t")
  try:
    timestamp = fields[0].split(":")
    hour = int(timestamp[0])
    minute = int(timestamp[1])
    seconds = float(timestamp[2])
    time = 3600*hour + 60*minute + seconds
    time = float('%.3f'%(time))
    if (lastTime < 0.0):
      lastTime = time
    if ((time - lastTime > 30) or (time < lastTime)):
      continue
    macAddr = fields[1]
    size = int(fields[2])
    seqNum = int(fields[3])
  except ValueError, e:
    continue
  if (firstFrame):
    sys.stderr.write("Start:     " + fields[0] + "\n")
    firstFrame = 0
  print macAddr + "\t" + str(time) + "\t" + str(size) + "\t" + str(seqNum)
  lastTime = time
