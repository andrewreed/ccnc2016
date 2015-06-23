import sys
import os

def runClientQuery(port):
    #Filters out bad window aligments
    #pearsonList = [0.01, 0.25, 0.5, 0.75, 0.8, 0.85, 0.90, 0.91,.92,.93,.94,.95,.96,.97,.98,.99]
    pearsonList = [0.75, 0.8, 0.85, 0.90, 0.91,.92,.93,.94,.95,.96,.97,.98,.99]
    print pearsonList
    stage1Paras = [",.98,1.02,.015,",",.99,1.01,.01,"]
    for pears in pearsonList:
        for st in stage1Paras:
            for num in range(1,26):
                numStr = str(num)
                if len(numStr)==1:
                    numStr="0"+numStr
                paras = str(pears)+st+numStr
                para=paras.replace(",","_")
                #print name[:-1]
                firstPart = "./readFromPcap_acks_ubuntu.bash ~/Netflix_DATA/"+numStr+".pcap 127.0.0.1 "+port+" "+paras
                #print firstPart
                secondPart =  " | tail -n 1 >> finalStage_"+para[:-2]+".csv"
                print secondPart
                child_pid = os.fork()
                if child_pid == 0:
                    # child process
                    os.system(firstPart+secondPart)
                    sys.exit(0)
                pid, status = os.waitpid(child_pid, 0) #reap children"
       

def main():
    port = "10007"#sys.argv[1]
    runClientQuery(port)
    
if __name__ == "__main__":
    main()

