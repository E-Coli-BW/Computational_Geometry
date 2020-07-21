'''
test FileReader class functionality
specify the path of your test data file
'''
import sys
from FileReader import *

if(len(sys.argv)!=1):
	print("Usage: functionalTest.py <data file>")
	sys.exit(-1)
path=argv[1] # path/to/your/data you inputed on console
reader=FileReader()
data=reader.readTxt(path)
points=reader.constructPoints(data)
# print(points)
print(type(points[0]))
