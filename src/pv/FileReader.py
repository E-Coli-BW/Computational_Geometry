class FileReader(object):
	def __init__(self):
		pass
	def readTxt(self,path):
		with open(path) as f:
			f.readlines()
			