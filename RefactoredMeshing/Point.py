from Edge import *
import operator
import gc

class Point(object):
	def __init__(self,x,y,z,nnlist):
		self.x=float(x)
		self.y=float(y)
		self.z=float(z)
		self.nnlist=nnlist
	def __init__(self,x,y,z,nnlist=[]):
		self.x=float(x)
		self.y=float(y)
		self.z=float(z)
		self.nnlist=nnlist
	def __eq__(self,other):
		if isinstance(other,self.__class__):
			return self.__dict__ == other.__dict__
		else:
			return False
	def __ne__(self,other):
		return not self.__eq__(other)

	def __str__(self):
		return "( "+str(self.x)+","+str(self.y)+","+str(self.z)+" )"
	__repr__ = __str__

	def update(self):
		pass
	def showNN(self):
		print(repr(self.nnlist))

	def connectNeighbors(self):
		edges=[]
		for nn in self.nnlist:
			edges.append(Edge(self,nn))
		return edges
	def plus(self,other):
		return Point(self.x-other.x,self.y-other.y,self.z-other.z)
	def minus(self,other):
		return Point(self.x+other.x,self.y+other.y,self.z+other.z)
	def dot(self,other):
		return self.x*other.x+self.y*other.y+self.z*other.z
	def times(self,number):
		return Point(number*self.x,number*self.y,number*self.z)
	def cross(self,other):
		##u_cross_v = [uy*vz-uz*vy, uz*vx-ux*vz, ux*vy-uy*vx]
		ux=self.x
		uy=self.y
		uz=self.z
		vx=other.x
		vy=other.y
		vz=other.z
		return Point(uy*vz-uz*vy,uz*vx-ux*vz,ux*vy-uy*vx)

	def norm(self):
		return (self.x)**2+(self.y)**2+(self.z)**2
	def dist(self,p):
		return (self.x-p.x)**2+(self.y-p.y)**2+(self.z-p.z)**2
	# def addNN(self,nnlist):
	# 	for nn in nnlist:

	def EuclideanSort(self):
		distArr=[]
		for nn in self.nnlist:
			distArr.append(self.dist(nn))
		# zip the neighbors and the distance into a dict
		distDict=dict(zip(self.nnlist,distArr))
		# print(type(distDict))
		# sort the dict based on distArr value from low to high
		# NN is the key, distDict is the value
		# sortedNN=[v for v in sorted(distDict.values())]
		sortedNN=sorted(distDict.items(),key=operator.itemgetter(1))
		res=[]
		for stuff in sortedNN:
			res.append(stuff[0])
		self.nnlist=res
		del res
		gc.collect()
		# self.nnlist=sortedDistDict.keys()


	# returns the projected Point on specified plane
	def project2Plane(self,plane):
		return self.minus( (plane.getNormalizedDir()).times( self.dot(plane.getNormalizedDir()) ) )

	# convert Point to normal list array
	def toArray(self):
		return [self.x,self.y,self.z]