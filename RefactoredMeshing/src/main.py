from Point import *
from Edge import *
from Plane import *
from Utils import *
from LeastSquareFit import *
from FileReader import *
from FileWriter import *
import gc
import objgraph
import inspect
import sys


# Read file and convert to Points
if(len(sys.argv)!=4):
	print("Usage: functionalTest.py <path of input data> <path for saving result> <flag to indicate whether to visualize result: 0: diable, 1: enable>")
	sys.exit(-1)

path=sys.argv[1]# "path/to/real/data"
reader=FileReader()
data=reader.readTxt(path)
points=reader.constructPoints(data)

allEdges=[]
for i in range(len(points)):
	points[i].EuclideanSort()
	to_be_added=points[i].nnlist[:len(points[i].nnlist)]
	# print(to_be_added)
	# need to make the to_be_added ones know its neighbors
	#-----------make my neigbors know their neighbors!
	for j in range(len(to_be_added)):
		curPoint=to_be_added[j]
		# print(type(curPoint))
		# if(curPoint in points):
		# print("in it!")
		# numpify so that we can compare Points using index method
		numpifiedPoints=[point.toArray() for point in points]
		# print(numpifiedPoints)
		nn_index=numpifiedPoints.index(curPoint.toArray())
		curPoint.nnlist=points[nn_index].nnlist
		to_be_added[j]=curPoint

	make_edges=[Edge(point,p) for p in to_be_added]
	allEdges.extend(make_edges)
	del make_edges
	gc.collect()
allEdges.sort(key=lambda edge:edge.length())
print(len(allEdges)) # should be 999000


#---------------- check intersects then construct final graph----------------
# pop edges one by one from allEdges
# test intersections
FinalRes=[]
FinalRes.append(allEdges.pop(0))
fitter=LeastSquareFit()

currentEdge=allEdges.pop(0)
while(len(allEdges)>0):
	testEdge=allEdges.pop(0)
	pointArr=Utils().commonNeighbors(currentEdge.start,currentEdge.end)
	# print("start's nn:",currentEdge.start.nnlist)
	# print("end's nn:",currentEdge.end.nnlist)
	try:
		dataBlob=fitter.fitPlane(pointArr[:12])
		if(sys.argv[3]=='1'):
			fitter.visulizePlane(dataBlob)
		assert len(dataBlob)==5
		targetPlane=fitter.getPlane(dataBlob)
		newProjectedEdge1=currentEdge.project2Plane(targetPlane)
		newProjectedEdge2=testEdge.project2Plane(targetPlane)
		if(newProjectedEdge1.intersects(newProjectedEdge2)):
			FinalRes.append(testEdge)
	except Exception:
		continue

print(len(FinalRes))
writer=FileWriter()
path=sys.argv[2] #"path/to/store/your/data"
writer.writeTxt(path,FinalRes)