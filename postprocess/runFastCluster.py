#!/usr/bin/python
# -*- coding: UTF-8 -*-

import numpy as np
import fastcluster
# import time
from scipy.spatial.distance import pdist, squareform
# import sys
import cPickle


# dataFile = sys.argv[1]
# denPath = sys.argv[3]
# distancePath = sys.argv[2]

def fastClusterCifar(dataFile, denPath, distancePath):
    print "Exact Clustering on CIFAR data"
    
    # Load raw data 
    with open(dataFile, 'rb') as fo:
        dict = cPickle.load(fo)
        data = dict['data'][0:1000]

    # compute the distance matrix
    distance = pdist(data, 'cosine')

    # store distance matrix
    squareformDistance = squareform(distance)
    output = open(distancePath, 'wb')
    cPickle.dump(squareformDistance, output)
    output.close()

    # store dendrogram
    m = fastcluster.linkage(data, 'average', preserve_input=False)
    outputfile = file(denPath, 'w')
    for i in m:
        k = [ str(j) for j in i ]
        outputfile.write('\t'.join(k)+'\n')
    outputfile.close()
    #print 'running complete\n'

def fastClusterReuters(distanceMatrixPath, dendrogramStorePath):

    with open(distanceMatrixPath, 'rb') as pkl_file:
        distanceMatrix = cPickle.load(pkl_file)

    print "Exact Clustering on TRC2 data"

    # fast clustering
    m = fastcluster.linkage(distanceMatrix, 'average', preserve_input=False)

    outputfile = file(dendrogramStorePath, 'w')
    for i in m:
        k = [ str(j) for j in i ]
        outputfile.write('\t'.join(k)+'\n')
    outputfile.close()

    #print 'running complete\n'
