class Utils(object):
	'''
	Utility class containing methods for finding/determine common geometrical
	relationships between Points, Edges and Planes
	'''
	def __init__(self,string="Utils"):
		self.string=string
	
	#find Edge a's common nn (i.e: intersection of set a.start.nnlist and set a.end.nnlist)
	def commonNeighbors(self,start,end):
		'''
		args: start Point and end Point of an Edge
		return: list of common Nearest neighbors of this Edge
		'''
		res=[]
		for i in range(len(start.nnlist)):
			for j in range(len(end.nnlist)):
				if(start.nnlist[i]==end.nnlist[j]):
					res.append(start.nnlist[i])
					continue
		return res

	def AreaABCSign(self,a,b,c):
		'''
		determine the orientation of the triangles,
		This is useful when we need to determine intersections

		args: 3 Points
		return: 1 if the triangle is formed clockwise, -1 if counter-clockwise
		'''
		ax=a.x
		ay=a.y
		az=a.z

		bx=b.x
		by=b.y
		bz=b.z

		cx=c.x
		cy=c.y
		cz=c.z

		sign=ax*(by*cz-bz*cy)+ay*(bz*cx-bx*cz)+az*(bx*cy-by*cx)
		if sign>0:
			return 1
		else:
			return -1

