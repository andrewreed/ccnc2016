
import scala.io.Source

def parser(fileName: String = "bigcd sr	Test.txt"): Unit = {
	val lines = io.Source.fromFile(fileName).getLines
	var maxSize = 0.0	
	var listResults = List.empty[List[(Int,Double)]]
	var headerList = List.empty[(Double,Double)]
	println("Evaluate each line one by one")	
	var count = 1		
	for(lineString <- lines){
		println("Evaluating Line "+count.toString)
		count+=1
		var line = lineString.split(",").toList
		val oneD = line.head.toDouble
		val twoD = line.tail.head.toDouble
		headerList+:=(oneD,twoD)
		line = line.tail.tail
		val lineInt = line.map(x=>x.toInt).sorted //List of Int sorted
		val lineD = lineInt.distinct	
		val lineSize = lineInt.size.toDouble		
		maxSize = if((lineD.size+1) > maxSize) lineD.size+1 else maxSize //Update max size (we add one to the list below)
		val listing = lineD.map( x=>(x,lineInt.count(_==x)/lineSize)).scanLeft((0,0.0))((x,y)=>(y._1,x._2+y._2))    
		listResults+:=listing
	}
	
	listResults=listResults.reverse
	headerList=headerList.reverse
	headerList+:=(-1.0,-1.0)
	println("Initialize result array")
	println("The number of rows is "+maxSize.toInt.toString)//TODO 
	val arrayResults = Array.fill(count-1)(Array.fill(maxSize.toInt)(1.0))
	count = 0	
	for(line <- listResults){
		val subArray = arrayResults.apply(count)
		var index = 0
		var (valueNum, valuePer) = (0,0.0)		
		for((num,per)<-line){
			if(num == index) {
				valueNum = num
				valuePer = per			
			}
			subArray(index) = valuePer	
			index+=1
		}
		count+=1	
	}
	println(headerList.map(x=>x._1).mkString(","))
	println(headerList.map(x=>x._2).mkString(","))
	var rowIndex = 0
	while(rowIndex < maxSize){
		var colIndex = 0
		print(rowIndex)
		print(",")
		while(colIndex < count){
			print(arrayResults.apply(colIndex).apply(rowIndex))
			colIndex+=1
			if(colIndex != count) print(",")
		}
		print("\n")		
		rowIndex+=1
	}
}

parser()
