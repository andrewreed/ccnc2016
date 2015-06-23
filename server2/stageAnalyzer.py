import sys
import os
import numpy as np
import matplotlib.mlab as mlab
import matplotlib.pyplot as plt

badSet={'into_darkness','rent','the_gruffalo','halo_forward_unto_dawn'}

def parseDocs(fileList):
    list = open(fileList, "rt")
    #Filters out bad window aligments
    rtnList = []
    for name in list:
        #print name[:-1]
        newName = name[:-5]+"_filtered.csv"
        child_pid = os.fork()
        if child_pid == 0:
            # child process
            os.system('cat '+name[:-1]+' | grep -v ",0,-1" > '+newName)
            sys.exit(0)
        pid, status = os.waitpid(child_pid, 0) #reap children
        rtnList.append(newName)
    return rtnList

class MovieDB:
    def __init__(self, graphName):
        self.graphName=graphName
        self.titleList = []
        self.resultsToQueryDict={} #of times(y) vs #results returned(x)
        self.correctResultsToQueryDict={}
        self.incorrectResultsToQueryDict={}
        self.correctRankDict ={}
        self.fpContDict={}
        
    def add(self,movie):
        self.titleList.append(movie.title)
        self.combineMovieTotals(movie)

    def combineMovieTotals(self,movie):
        for size, freq in movie.resultsToQueryDict.iteritems():
            if size in self.resultsToQueryDict:
                self.resultsToQueryDict[size]+=freq
            else:
                self.resultsToQueryDict[size]=freq
        for size, numQueries in movie.correctResultsToQueryDict.iteritems():
            if size in self.correctResultsToQueryDict:
                self.correctResultsToQueryDict[size]+=numQueries
            else:
                self.correctResultsToQueryDict[size]=numQueries
        for size, numQueries in movie.incorrectResultsToQueryDict.iteritems():
            if size in self.incorrectResultsToQueryDict:
                self.incorrectResultsToQueryDict[size]+=numQueries
            else:
                self.incorrectResultsToQueryDict[size]=numQueries            
        for rank, freq in movie.correctRankDict.iteritems():
            if rank in self.correctRankDict:
                self.correctRankDict[rank]+=freq
            else:
                self.correctRankDict[rank]=freq
        for fp, freq in movie.fpDict.iteritems():
            if freq in self.fpContDict:
                self.fpContDict[freq]+=1
            else:
                self.fpContDict[freq]=1
    
    def generateOveralPlot(self):
        #of times(y) vs #results returned(x)
        #n, bins, patches = plt.hist(np.array(self.resultsToQueryDict.values()), len(self.resultsToQueryDict.values()),normed=False,range=(0, 500),facecolor='green')
        plt.bar(self.resultsToQueryDict.keys(), self.resultsToQueryDict.values(),facecolor='green')
        plt.xlabel('# of Results From Filter (Size of Return Set)')
        plt.ylabel('Frequency (Number of Queries with a Specified Return Set size)')
        plt.title(self.graphName+'_Histogram of Number of Results Returned by Filter')
        #weak spacing to prevent clipping of ylabel
        #plt.subplots_adjust(left=0.15)
        #plt.show()
        plt.savefig(self.graphName+"_Overall.png")
        plt.close()
    
    def generateTruePositveRank(self):
        'TODO: Drop the negative one!'
        #print len(self.correctRankDict.keys()), self.correctRankDict.keys()
        #print len(self.correctRankDict.values()), self.correctRankDict.values()
        #fList=[]
        #for key, value in self.correctRankDict.iteritems():
        #    fList = self.freqList(key,value,fList)
        #    print

        
        #n, bins, patches = plt.hist(fList, len(self.correctRankDict.values()),normed=False,range=(0, 500),facecolor='green')
        plt.bar(self.correctRankDict.keys(), self.correctRankDict.values())
        plt.xlabel('Rank')
        plt.ylabel('Frequency (Number of TPs at that Rank)')
        plt.title(self.graphName+'_Histogram of True Positive Rank')
        #weak spacing to prevent clipping of ylabel
        #plt.subplots_adjust(left=0.15)
        #plt.show()
        plt.savefig(self.graphName+"_TP.png")
        plt.close()
        plt.bar(self.correctRankDict.keys()[:-1], self.correctRankDict.values()[:-1])
        plt.xlabel('Rank')
        plt.ylabel('Frequency (Number of TPs at that Rank)')
        plt.title(self.graphName+'_Histogram of True Positive Rank (When TP Present)')
        #weak spacing to prevent clipping of ylabel
        #plt.subplots_adjust(left=0.15)
        #plt.show()
        plt.savefig(self.graphName+"_TP_nonNegative.png")
        plt.close()
        
    def generateFP(self):
        
        #sortedValues = [self.fpContDict[key] for key in sorted(self.fpContDict.keys())] 
        #print len(self.fpContDict), sorted(self.fpContDict.keys())
        #print len(sortedValues), sortedValues
        #fList=[]
  
        #for key, value in self.fpContDict.iteritems():
        #    fList = self.freqList(key,value,fList)
        #print fList
        
        #n, bins, patches = plt.hist(np.array(self.fpContDict.values()), np.array(self.fpContDict.keys()),normed=False,facecolor='green', alpha=0.5)
        #n, bins, patches = plt.hist(fList, 
                                    #sorted(self.fpContDict.keys()),
                                    #range=(0,10),
        #                            normed=False,facecolor='green'
        #                            )
        plt.bar(np.array(self.fpContDict.keys()), np.array(self.fpContDict.values()), facecolor='green')

        #print n
        #print bins
        plt.xlabel('Number of Times a Distinct FP Ranked Higher than TP')
        plt.ylabel('Count')
        plt.title(self.graphName+'_Histogram of High Value False Positives')
        #weak spacing to prevent clipping of ylabel
        #plt.subplots_adjust(left=0.15)
        #plt.show()
        plt.savefig(self.graphName+"_FP_count.png")
        plt.close()
        
    def freqList(self,number, size, fList):
        fList.append( [number for x in range(size)])
        return fList
    
    def generatePlots(self):
       self.generateOveralPlot()
       self.generateTruePositveRank()
       self.generateFP()
    
    
class Movie:
    def __init__(self, fileName):
        self.title = fileName.split("_MOVIE_")[1][:-13]
        self.fileName = fileName
        self.resultsToQueryDict = {} #of times(y) vs #results returned(x)
        self.correctResultsToQueryDict = {}
        self.incorrectResultsToQueryDict = {}
        self.correctRankDict = {}
        self.fpDict = {}
    
    def parseMovieFile(self):
        f = open(self.fileName,"rt")
        for line in f:
            fields = line.split(",")
            if(len(fields) < 3):continue
            #1st element ([0]) is the query
            #2nd element ([1]) is the size of priority queue
            #3rd element ([2]) is the rank of the true positive
            #print fields 
            self.addTotalNumberResults(int(fields[1]))
            if self.hasResults(int(fields[2])):
                self.addToCorrectFilterCount(int(fields[1]))
                highRankFPs=fields[3:int(fields[2])+2]
            else: 
                self.addToIncorrectFilterCount(int(fields[1]))
                highRankFPs=fields[3:]
            self.trackRankOfCorrectMatch(int(fields[2]))     
            self.processFP(highRankFPs)
        
    def hasResults(self, rank):
        return rank !=-1
    
    def addTotalNumberResults(self, num):
        if num in self.resultsToQueryDict:
            self.resultsToQueryDict[num]+=1
        else:
            self.resultsToQueryDict[num]=1
    
    def addToCorrectFilterCount(self,num):
        if num in self.correctResultsToQueryDict:
            self.correctResultsToQueryDict[num]+=1
        else:
            self.correctResultsToQueryDict[num]=1
    
    def addToIncorrectFilterCount(self,num):
        if num in self.incorrectResultsToQueryDict:
            self.incorrectResultsToQueryDict[num]+=1
        else:
            self.incorrectResultsToQueryDict[num]=1
    
    def trackRankOfCorrectMatch(self, rank):
        if rank in self.correctRankDict:
            self.correctRankDict[rank]+=1
        else:
            self.correctRankDict[rank]=1
    
    def processFP(self, listFP):
        for fp in listFP:
            fp = fp.split('netflix/')[1]
            if fp in self.fpDict:
                self.fpDict[fp]+=1
            else:
                self.fpDict[fp]=1
            

def main():
    fileList = 'list.txt' #List of csv files
    #sys.argv[1]
    print fileList
    parsedFiles = parseDocs(fileList)
    print "Parsing is now done"
    db=MovieDB("Overall")
    goodDB=MovieDB("Correct")
    badDB=MovieDB("Incorrect")
    for fileName in parsedFiles:
        mov = Movie(fileName)
        mov.parseMovieFile()
        db.add(mov) 
        if mov.title in badSet:
            badDB.add(mov)
        else:
            goodDB.add(mov)
    db.generatePlots()
    print "Overall Plots Generated"
    badDB.generatePlots()
    print "Incorrect Plots Generated"
    goodDB.generatePlots()
    print "Correct Plots Generated"

if __name__ == "__main__":
    main()
