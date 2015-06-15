#!/usr/bin/env python

import sys
import datetime

macDict = {}

for match in sys.stdin:
  match = match.rsplit('\n') # remove trailing newlines
  fields = match[0].split("\t")
  try:
    macAddr = fields[0]
    timestamp = float(fields[1])
    videoTitle = fields[2]
    startIndex = int(fields[3])
    print "Received a match: " + match[0]
  except ValueError, e:
    continue

  if macAddr not in macDict:
    macDict[macAddr] = []

  matchList = macDict[macAddr]

  matchList.append((timestamp, videoTitle, startIndex))

  if (len(matchList) > 1):
    for a in range(0, len(matchList) - 1):
      for b in range(a+1, len(matchList)):
        matchA = matchList[a]
        matchB = matchList[b]

        if (matchA[1] != matchB[1]):
          continue

        timeDiff = matchB[0] - matchA[0]
        numSegments = int(round(timeDiff / 4.0))

        if (numSegments < 15):
          continue

        if (numSegments == (matchB[2] - matchA[2])):
          print "Watching: " + macAddr + " " + matchA[1]
          print datetime.datetime.now()
          sys.exit() ## for testing purposes... you get one shot to ID the movie
          del matchList[:]
          break
