from FileReader import *
path=r"C:\\Spring 2019 UM Class\\CSC 647 Computational Geometry\\FinalProject\\RefactoredMeshing\\cluster0.txt"
reader=FileReader()
data=reader.readTxt(path)
points=reader.constructPoints(data)
# print(points)
print(type(points[0]))
