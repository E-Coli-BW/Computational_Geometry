class Utils(object):
	def __init__(self,string="Utils"):
		self.string=string
	
	#find edge a's common nn (i.e: intersection of set a.start.nnlist and set a.end.nnlist)
	def commonNeighbors(self,start,end):
		# print("start:",start)
		# print("end:",end)
		# print("start's nnlist:",start.nnlist)
		# print("end's nnlist:",end.nnlist)
		# print(start.nnlist[1]==end.nnlist[2])
		res=[]
		for i in range(len(start.nnlist)):
			for j in range(len(end.nnlist)):
				if(start.nnlist[i]==end.nnlist[j]):
					res.append(start.nnlist[i])
					continue
		return res
	# determine the orientation of the triangles,
	# This is useful when we need to determine intersections
	def AreaABCSign(self,a,b,c):
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

