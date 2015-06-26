#!/usr/bin/env python

import sys
import datetime
"""
TODO:

Save stdin stream

"""
strongGuess="None"
weakGuess="None"

movieDict ={
                 '01'  : 'noah',
                 '02' :  'twilight',
                 '03' :  'peabody_and_sherman',
                 '04' :  'neverbeast',
                 '05' :  'into_darkness',
                 '06' :  'national_treasure',
                 '07' :  'i_frankenstein',
                 '99' :  'silence_of_the_lambs',
                 '08' :  'odd_thomas',
                 '09' :  'defiance',
                 '10' :  'hot_fuzz',
                 '11' :  'footloose',
                 '12' :  'icetastrophe',
                 '13' :  'boo_fest',
                 '14' :  'automata',
                 '15' :  'room_on_the_broom',
                 '16' :  'chitty_chitty_bang_bang',
                 '17' :  'chicken_run',
                 '18' :  'tad_the_lost_explorer',
                 '19' :  'the_wedding_pact',
                 '20' :  'halo_forward_unto_dawn',
                 '21' :  'killing_season',
                 '22' :  'the_gruffalo',
                 '23' :  'rent',
                 '24' :  'dragons_2',
                 '25' :  'good_will_hunting',
                 '27' :  'the_croods',
                 '28' :  'mulan',
                 '29' :  'jack_ryan',
                 '30' :  'hoodwinked',
                 '31' :  'the_switch',
                 '32' :  'brick_mansions',
                 '33' :  'road_to_el_dorado',
                 '34' :  'payback',
                 '35' :  'november_man',
                 '36' :  'october_baby',
                 '37' :  'leapfrog_phonics_farm',
                 '38' :  'legally_blonde',
                 '39' :  'mud',
                 '40' :  'robocop',
                 '41' :  'rescuers_down_under',
                 '42' :  'remember_me',
                 '43' :  'europa_report',
                 '44' :  'blackfish',
                 '45' :  'fantasia',
                 '46' :  'stuck_in_love',
                 '47' :  'homesman',
                 '48' :  'catching_fire',
                 '49' :  'road_rally',
                 '50' :  'crank'

            }





macDict = {}
#test = open("test.txt","wt")
#print  >> test, sys.stdin
#print  >> test, sys.argv
#print "========"

def correlationCheck(vidList):
    for i in range(0, len(vidList) - 1):
      for j in range(i+1, len(vidList)):
        timeDiff = vidList[j][0] - vidList[i][0]
        numSegments = int(round(timeDiff / 4.0))
        if(numSegments < 5): 
            continue
        seqDif = vidList[j][1] - vidList[i][1]
        if (seqDif == numSegments): 
            return True
    return False

def getSegmentCount(vidList):
    return len({ ele for ele in [ind for ts, ind in vidList]})


def getGuess(matchDict):
    winningTitle=""
    winningCount = -1
    for title, vidList in matchDict.iteritems():
        if(getSegmentCount(vidList) > winningCount):
            winningCount = getSegmentCount(vidList)
            winningTitle = title
    return winningTitle

apMovie = movieDict[sys.argv[1][-2:]]
for match in sys.stdin:
  match = match.rsplit('\n') # remove trailing newlines
  fields = match[0].split("\t")
  #print  >> test, fields
  #print match
  try:
    macAddr = fields[0]
    timestamp = float(fields[1])
    videoTitle = fields[2]
    startIndex = int(fields[3])
    #print "Received a match: " + match[0]
  except ValueError, e:
    continue

  if macAddr not in macDict:
    #macDict[macAddr] = []
    macDict[macAddr] = {}
    
  #matchList = macDict[macAddr]
  matchDict = macDict[macAddr]
  if videoTitle in matchDict:
      #Get list, append results
      matchList = matchDict[videoTitle]
      matchList.append((timestamp, startIndex))
      if(correlationCheck(matchList) and strongGuess == "None"):
          #print "Watching: " + macAddr + " " + videoTitle
          #print datetime.datetime.now()
          strongGuess = videoTitle
          #sys.stdout.flush();sys.stderr.flush();sys.stdin.flush()
          #sys.exit()
  else:
      #Add new list
      matchDict[videoTitle] = [(timestamp, startIndex)]
  #matchList.append((timestamp, videoTitle, startIndex))

  #macDict[macAddr] = matchList 	
  #print matchList
  #print "====================="
#print "No strongly correlated results"
weakGuess = getGuess(matchDict)
#print "Best guess is "+weakGuess
if(strongGuess == 'netflix/'+apMovie):
    strongGuessFlag="1"
else:
    strongGuessFlag="0"
if(weakGuess == 'netflix/'+apMovie):
    weakGuessFlag="1"
else:
    weakGuessFlag="0"

#A prior movie, strong guess, weak guess, strong guess true/false, weak guess true false 
print apMovie+","+strongGuess+","+weakGuess+","+strongGuessFlag+","+weakGuessFlag
