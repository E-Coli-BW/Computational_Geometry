# OK, it's working! Now try to Modulize it!
import numpy as np
import scipy.linalg
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt

from Point import *
from Edge import *
from Plane import *
from Utils import *

#Plan:
#Goal to achieve:
# LeastSquareFit be an independant class but has the potential of 
# being easily integrated into Utils class
# Takes inputs of points array, return a Plane object representing the plane computed
# Additional function: seperate the visulization part into a function, user call it when vis is needed 
# e.g:
class LeastSquareFit(object):
    def __init__(self):
        pass
    # fitPlane returns the raw data(array of Points)
    # it accepts a list of Points
    def fitPlane(self,data):
        #numpify the data(type Point)
        numpifiedPoints=[]
        for point in data:              
            numpifiedPoints.append(point.toArray())
        data=np.array(numpifiedPoints)
        X,Y = np.meshgrid(np.arange(-3.0, 3.0, 0.5), np.arange(-3.0, 3.0, 0.5))
        XX = X.flatten()
        YY = Y.flatten()
        A = np.c_[data[:,0], data[:,1], np.ones(data.shape[0])]
        C,_,_,_ = scipy.linalg.lstsq(A, data[:,2])    # coefficients
        #evaluate it on grid
        Z = C[0]*X + C[1]*Y + C[2]
        return [data,X,Y,Z,C]
    # accept data returned by fitPlane, which is
    # an array consists of numpified points(data), coordinates info X,Y,Z
    def visulizePlane(self,blob):
        data=blob[0]
        # note here X,Y,Z are coordinates!
        X=blob[1]
        Y=blob[2]
        Z=blob[3]
        # plot points and fitted surface
        fig = plt.figure()
        ax = fig.gca(projection='3d')
        ax.plot_surface(X, Y, Z, rstride=1, cstride=1, alpha=0.2)
        ax.scatter(data[:,0], data[:,1], data[:,2], c='r', s=50)
        plt.xlabel('X')
        plt.ylabel('Y')
        ax.set_zlabel('Z')
        ax.axis('equal')
        ax.axis('tight')
        plt.show()
    # the data accepted by Pointerize is the return value of fitPlane
    # return the pointerized data
    def Pointerize(self,blob):
        data=blob[0]
        pointerized=[]
        for numpifiedPoint in data:
            pointerized.append(Point(numpifiedPoint[0],numpifiedPoint[1],numpifiedPoint[2]))
        return pointerized
    def getPlane(self,blob):
        C=blob[4]
        #Z = C[0]*X + C[1]*Y + C[2]
        # get a Point on surface
        p=Point(0,0,C[2])
        # get the direction of the surface
        # Note that for Ax+By+Cz=Const, normal vec is just Point(A,B,C)
        # so in our case: let Const=1
        #       C=1/C[2]
        #       B=-C[1]/C[2]
        #       A=-C[0]/C[2]
        direction=Point(-C[0]/C[2],-C[1]/C[2],1/C[2])
        return Plane(direction,p)

# # # Simulated Data and Basic Statistics
# # # some 3-dim points
# # mean = np.array([0.0,0.0,0.0])
# # cov = np.array([[1.0,-0.5,0.8], [-0.5,1.1,0.0], [0.8,0.0,1.0]])
# # data = np.random.multivariate_normal(mean, cov, 50)

# # use my own data
# data=np.array([Point(1,2,3).toArray(),Point(0,2,4).toArray(),Point(0,1,5).toArray(),Point(0,1.5,5).toArray(),Point(0.1,1,5).toArray(),Point(-0.1,1,5).toArray(),Point(0,1,4.8).toArray(),Point(0,1,5.2).toArray(),Point(1.3,0,5).toArray(),Point(-0.8,1.7,5).toArray(),Point(0.5,1.24,4.5).toArray(),Point(-0.8,1,5.7).toArray()])

# # Set up base plane for the plots
# # regular grid covering the domain of the data
# X,Y = np.meshgrid(np.arange(-3.0, 3.0, 0.5), np.arange(-3.0, 3.0, 0.5))
# XX = X.flatten()
# YY = Y.flatten()

# order = 1    # 1: linear, 2: quadratic
# if order == 1:
#     # best-fit linear plane
#     A = np.c_[data[:,0], data[:,1], np.ones(data.shape[0])]
#     C,_,_,_ = scipy.linalg.lstsq(A, data[:,2])    # coefficients
    
#     # evaluate it on grid
#     Z = C[0]*X + C[1]*Y + C[2]
    
#     # or expressed using matrix/vector product
#     #Z = np.dot(np.c_[XX, YY, np.ones(XX.shape)], C).reshape(X.shape)

# elif order == 2:
#     # best-fit quadratic curve
#     A = np.c_[np.ones(data.shape[0]), data[:,:2], np.prod(data[:,:2], axis=1), data[:,:2]**2]
#     C,_,_,_ = scipy.linalg.lstsq(A, data[:,2])
    
#     # evaluate it on a grid
#     Z = np.dot(np.c_[np.ones(XX.shape), XX, YY, XX*YY, XX**2, YY**2], C).reshape(X.shape)

# # plot points and fitted surface
# fig = plt.figure()
# ax = fig.gca(projection='3d')
# ax.plot_surface(X, Y, Z, rstride=1, cstride=1, alpha=0.2)
# ax.scatter(data[:,0], data[:,1], data[:,2], c='r', s=50)
# plt.xlabel('X')
# plt.ylabel('Y')
# ax.set_zlabel('Z')
# ax.axis('equal')
# ax.axis('tight')
# plt.show()