from Point import *
from Edge import *

class FileWriter(object):
	def __init__(self):
		pass
	def writeTxt(self,path,edges):
		with open(str(path)+"res.txt","w") as f:
			res=[]
			for edge in edges:
				start=edge.start.toArray()
				end=edge.end.toArray()
				res.append(start)
				res.append(end)
		f.writelines(res)

