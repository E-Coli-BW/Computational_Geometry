from Point import *

class Plane(object):
	'''
	 A plane is simply a normal vector(direction) and a point that goes through
	treat direction vector as a Point Vector
	'''
	def __init__(self,direction,p):
		# direction is a Point(vector)
		self.direction=direction
		# point is a Point
		self.p=p
	def getNormalizedDir(self):
		'''
		args: None
		return: normal vector represented as Point
		'''
		return Point(self.direction.x/self.direction.norm(),self.direction.y/self.direction.norm(),self.direction.z/self.direction.norm())
