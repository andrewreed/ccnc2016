#!/usr/bin/env python

import sys
from collections import deque
from socket import *
import threading

macDict = {}

def readFromServer(clntsock):
  while 1:
    results = clntsock.recv(1024)
    if (results[-1:] == "\f"):
      sys.stdout.write(results[:-1])
      break
    sys.stdout.write(results)

def sendDeque(mac, binList, clntsock):
  if (len(binList) < 480):
    return

  startTime = binList[0][0]

  chunkList = []
  chunkSize = 0
  count = 0

  for currentBin in binList:
    count += 1
    chunkSize += currentBin[1]
    if (count == 16):
      chunkList.append(chunkSize)
      chunkSize = 0
      count = 0

  output = mac + "\t" + str(startTime) + "\t" + str(int(chunkList[0]) - 33750)
  for x in range(1,30):
    output += "," + str(int(chunkList[x]) - 33750)
  clntsock.send(output + "\n")

### MAIN PROGRAM ###
serverName = sys.argv[1]
serverPort = int(sys.argv[2])

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect((serverName,serverPort))

readerThread = threading.Thread(target=readFromServer, args=(clientSocket,))
readerThread.start()

# read the wifi capture via stdin and bin the data into 250ms bins
for wifiFrame in sys.stdin:
  wifiFrame = wifiFrame.rsplit('\n') # remove trailing newlines
  fields = wifiFrame[0].split("\t")
  try:
    macAddr = fields[0]
    time = float(fields[1])
    frameSize = int(fields[2])
  except ValueError, e:
    continue

  if macAddr not in macDict:
    macDict[macAddr] = [deque([],480), 0, -1.0]

  macData = macDict[macAddr]

  if (macData[2] < 0.0):
    macData[2] = time  

  # this needs to be a while loop so that every 250ms bin is appended,
  #  even if the bin had no data
  while (time - macData[2] >= 0.25):
    macData[0].append((macData[2],macData[1]))
    sendDeque(macAddr, macData[0], clientSocket)
    macData[2] += 0.25
    macData[1] = 0
  macData[1] += frameSize
# make sure you append the last bin
macData[0].append((macData[2],macData[1]))
sendDeque(macAddr, macData[0], clientSocket)
clientSocket.send("complete\n")
readerThread.join()
clientSocket.close()
