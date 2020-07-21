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
'''
tests functions using some small artificial data
'''
if(len(sys.argv)!=3):
	print("Usage: functionalTest.py <path of input data> <path for saving result>")
	sys.exit(-1)

nnlist0=[Point(1,1,1),Point(0,1,10),Point(4,5,6),Point(1,2,3)]
nnlist1=[Point(0,0,0),Point(0,1,2),Point(1,2,3),Point(4,5,6)]
nnlist2=[Point(0,0,0),Point(1,1,1)]
# point0=Point(0,0,0,nnlist0)
# point1=Point(0,0,0,nnlist1)
point0=Point(0,0,0,nnlist0)
point1=Point(1,1,1,nnlist1)
point2=Point(0,1,2,nnlist2)

print(point0)
# print(point1)
# print(point2)
point0.showNN()
edges=point0.connectNeighbors()
print(edges)
for edge in edges:
	print(edge)
	print(edge.length())
point0.showNN()
point0.EuclideanSort()
point0.showNN()
#take first 2 edges
to_be_added=point0.nnlist[:2]
make_edges=[Edge(point0,p) for p in to_be_added]
print(to_be_added)
print(make_edges)
allEdges=[]
allEdges.extend(make_edges)
#heap elements could be tuples in which the first element is the priority and defines the sort order
#sort the Edges
allEdges.sort(key=lambda edge:edge.length())
print(allEdges)


point1.EuclideanSort()
point1.showNN()
#take first 2 edges
to_be_added=point1.nnlist[:2]
make_edges=[Edge(point0,p) for p in to_be_added]
print(to_be_added)
print(make_edges)
allEdges.extend(make_edges)
#heap elements could be tuples in which the first element is the priority and defines the sort order
#--------------------------------------SORT EDGES----------------------------------------------------
#sort the Edges
allEdges.sort(key=lambda edge:edge.length())
print(allEdges)

commonNeighbors=Utils().commonNeighbors(point0,point1)
print(commonNeighbors)

plane=Plane(Point(1,1,1),Point(0,0,0))
projectionTest=allEdges[0].project2Plane(plane)

projectedEdge1=allEdges[1].project2Plane(plane)
projectedEdge2=allEdges[2].project2Plane(plane)
print(allEdges[1])
print(allEdges[2])
print("projected 1:")
print(projectedEdge1)
print("projected 2:")
print(projectedEdge2)
testIntersects=projectedEdge1.intersects(projectedEdge2)
print("intersects?:",testIntersects)

# Test PlaneFitting
pointArr=[Point(1,2,3),Point(0,2,4),Point(0,1,5),Point(0,1.5,5),Point(0.1,1,5),Point(-0.1,1,5),Point(0,1,4.8),Point(0,1,5.2),Point(1.3,0,5),Point(-0.8,1.7,5),Point(0.5,1.24,4.5),Point(-0.8,1,5.7)]
fitter=LeastSquareFit()
# should return a list of length 5 [data,X,Y,Z,C]
dataBlob=fitter.fitPlane(pointArr)
assert len(dataBlob)==5
# visualize the plane, should pop up a graph
# fitter.visulizePlane(dataBlob)
# Pointerize dataBlob (turn np array back to Points)
Points=fitter.Pointerize(dataBlob)
print(list(Points))
# get the plane we need
targetPlane=fitter.getPlane(dataBlob)
# test Projection using targetPlane
newProjectedEdge1=allEdges[3].project2Plane(targetPlane)
newProjectedEdge2=allEdges[1].project2Plane(targetPlane)
print("new projected 1:")
print(newProjectedEdge1)
print("new projected 2:")
print(newProjectedEdge2)
# test intersects using our new edges
testIntersects1=newProjectedEdge1.intersects(newProjectedEdge2)
print("Intersects?:",testIntersects1)
testIntersects2=newProjectedEdge2.intersects(newProjectedEdge1)
print("Switch Order...")
print("Intersects?:",testIntersects2)
#should have the same result!
assert testIntersects1==testIntersects2
print("------------------------------------------nn test starts----------------------")
# Read file and convert to Points
if(len(sys.argv)!=3):
	print("Usage: functionalTest.py <path of input data> <path for saving result>")
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




