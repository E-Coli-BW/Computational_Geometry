from Point import *
class FileReader(object):
	def __init__(self):
		pass
	def readTxt(self,path):
		with open(path) as f:
			lines=f.readlines()
		return lines
	def constructPoints(self,lines):
		# print(lines[0])
		# each line consists of a current point and its nns
		# remove \n for each line
		
		# use 10 lines as a small scale test
 		data=[line[:len(line)-1] for line in lines]
 		# print(data)
		currentPoint=[]
		nns=[]
		cleanPoints=[]
		for stuff in data:
			points=stuff.split(" ")
			# print(len(points))
			currentPoint=Point(points[0],points[1],points[2])
			i=3
			while i<len(points)-2:
				nns.append(Point(points[i],points[i+1],points[i+2]))
				# print(Point(points[i],points[i+1],points[i+2]))
				i+=3
			# print("num of nns",len(nns))
			# print(len(point))
			point=Point(points[0],points[1],points[2],nns)
			cleanPoints.append(point)
			# Don't forget to clear up nns....
			nns=[]
		return cleanPoints



