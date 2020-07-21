'''
code for finding clusters in raw point cloud data
'''
import numpy as np
from scipy.spatial.distance import pdist, squareform
clusterdown=np.loadtxt("path/to/your/cluster_data",dtype=np.float64) 

spatial_distance0 =pdist(clusterdown, 'euclidean')
spatial_distance0=squareform(spatial_distance0, force='no', checks=True)
order0=np.zeros((1000,1000))
name=np.zeros(1000,np.int)
for i in range(1000):
    name[i]=i

#sort
for k in range(1000):
    item=spatial_distance0[k]
    name2=name
    dictionary=dict(zip(name2,item))
    dictionary1 = sorted(dictionary.items(), key=lambda item: item[1])
    nameorder=np.zeros(1000)
    for i in range(1000):
        nameorder[i]=dictionary1[i][0]
    order0[k]=nameorder

print order0

clusterdown=np.loadtxt("/path.to/your/downsampled_data",dtype=np.float64)

spatial_distance1 =pdist(clusterdown, 'euclidean')
spatial_distance1=squareform(spatial_distance1, force='no', checks=True)
order1=np.zeros((1000,1000))
name=np.zeros(1000,np.int)
for i in range(1000):
    name[i]=i

#sort
for k in range(1000):
    item=spatial_distance1[k]
    name2=name
    dictionary=dict(zip(name2,item))
    dictionary1 = sorted(dictionary.items(), key=lambda item: item[1])
    nameorder=np.zeros(1000)
    for i in range(1000):
        nameorder[i]=dictionary1[i][0]
    order1[k]=nameorder

print order1

clusterdown=np.loadtxt("/path.to/your/downsampled_data",dtype=np.float64)

spatial_distance2 =pdist(clusterdown, 'euclidean')
spatial_distance2=squareform(spatial_distance2, force='no', checks=True)
order2=np.zeros((1000,1000))
name=np.zeros(1000,np.int)
for i in range(1000):
    name[i]=i

#sort
for k in range(1000):
    item=spatial_distance2[k]
    name2=name
    dictionary=dict(zip(name2,item))
    dictionary1 = sorted(dictionary.items(), key=lambda item: item[1])
    nameorder=np.zeros(1000)
    for i in range(1000):
        nameorder[i]=dictionary1[i][0]
    order2[k]=nameorder

print order2