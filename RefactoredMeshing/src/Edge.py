# Need to finish leastSquaredPlane and intersects
from Utils import *
class Edge(object):
	def __init__(self,start,end):
		self.start=start
		self.end=end
	def length(self):
		return self.start.dist(self.end)

	# To project to plane
	# First get the normal vector of the plane
	# NORMALIZE IT
	# projected Edge(vector) would be:
	# target-(target.dot(normalVec))*normalVec
	# self.vectorize()-self.vectorize().dot(plane.direction)*(plane.direction)
	# project2Plane returns a point vector that represents the vectorized Edge
	# has some problems....in the process of vectorize, I lost the info on start 
	# and end point of the edge....
	# so it should NOT return point vector, but a normal edge!
	# it is convinient if we have project2Plane in Point class
	def project2Plane(self,plane):
		# ugly,unreadable
		# (self.end.minus(self.start)).minus((self.end.minus(self.start).dot(plane.direction)*(plane.direction)))
		projected_start=self.start.project2Plane(plane)
		projected_end=self.end.project2Plane(plane)
		return Edge(projected_start,projected_end)

	# cross product with another line
	# resulting in a normal vector of a plane
	# Note that Point can also be viewed as a vector
	# So we just use Point to denote the vectors we get
	# determined by self and other
	# The norm of this return value(a normal vector)
	# is simply the area of the Parallelogram,
	# norm can be obtained by (a.cross(b)).norm()

	# vectorize the edge:
	def vectorize(self):
		# a Point minus a Point is a Point it is also a 
		# vectorized edge!
		return self.end.minus(self.start)

	def cross(self,other):
		# u cross v
		#u
		X=self.end.x-self.start.x
		Y=self.end.y-self.start.y
		Z=self.end.z-self.start.z

		thisEdge=Point(X,Y,Z)

		#v
		otherX=other.end.x-other.start.x
		otherY=other.end.y-other.start.y
		otherZ=other.end.z-other.start.z

		thatEdge=Point(otherX,otherY,otherZ)

		#u_cross_v = [uy*vz-uz*vy, uz*vx-ux*vz, ux*vy-uy*vx]
		resX=Y*otherZ-Z*otherY
		resY=Z*otherX-X*otherZ
		resZ=X*otherY-Y*otherX
		return Point(resX,resY,resZ)

	# FIRST NEED AN AreaABCSign(A,B,C)
	# if AreaABCSign(a,b,c)*AreaABCSign(a,b,d)<0 => intersects!
	# returns boolean value if intersects => return True else return False
	def intersects(self,other):
		a=self.start
		b=self.end
		c=other.start
		d=other.end
		return Utils().AreaABCSign(a,b,c)*Utils().AreaABCSign(a,b,d)>0


	def __str__(self):
		return "["+self.start.__str__()+","+self.end.__str__()+"]"