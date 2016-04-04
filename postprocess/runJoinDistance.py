#!/usr/bin/python
# -*- coding: UTF-8 -*-

from __future__ import print_function

from lib.simulateMerge import simulateMerge
import numpy as np
import cPickle
import re
import os
import collections

import matplotlib
matplotlib.use('Agg')

import matplotlib.pyplot as plt

def runJoinDistance(distanceMatrixPath, stdDendrogramPath, densFolder, densFiles, figurePath, plotDensity):
    print ("Computing the Joining Distance Ratios")
    # read distance matrix
    with open(distanceMatrixPath, 'rb') as pkl_file:
        distanceMatrix = cPickle.load(pkl_file)
            
    # stdDendrogramPath is just a path
    stdDen = simulateMerge(stdDendrogramPath, distanceMatrix.copy(), True)
    stdDen.simulation()
    reStd = np.array( stdDen.reBuildData )

    tries = []
    height = []
    ratio = []

    dic = collections.defaultdict(list)
    
    #print 'start simulation'
    for index, fileName in enumerate(densFiles):
        progress = (float(index)/float(len(densFiles)))*100
        print ("%f%% done" % progress, end='\r')
        #print ("%f%% done" % progress)
        t,h = extractTriesAndHeigt(fileName)
        #tries.append(t)
        #height.append(h)
        i = os.path.join(densFolder, fileName)
        den = simulateMerge(i, distanceMatrix.copy(), True)
        den.simulation()
        redata = np.array( den.reBuildData )

        r = reStd.sum(axis=0)[2] / redata.sum(axis=0)[2]

        dic[(t,h)].append(r)
            
    print ("100% done       ")
    
    for i in dic.iterkeys():
        t,h = i
        r = float( np.average( dic[(t,h)] ) )
        tries.append(t)
        height.append(h)
        ratio.append(r)
    JDRplot(np.array(tries), np.array(height), np.array(ratio), figurePath , plotDensity)


#file name exaples:
#cifar-1000_105_tries_1_height_2306559830116571264_seed.dat
#full_data_95_tries_85_height_2347532181164706204_seed.dat
trieNumberExtractor = re.compile(r".*_([0-9]+)_tries.*")
heightExtractor = re.compile(r".*_([0-9]+)_height.*")
def extractTriesAndHeigt(fileName):
    tries = trieNumberExtractor.match(fileName).group(1)
    height = heightExtractor.match(fileName).group(1)
    return tries, height

def JDRplot(tries, height, ratio, figurePath, plotDensity):
    matplotlib.rcParams['xtick.direction'] = 'out'
    matplotlib.rcParams['ytick.direction'] = 'out'

    sumList = map(lambda x,y,z: (int(x), int(y), float(z)), tries, height, ratio)
    sumList.sort()
    
    
#    sumList = map(lambda x: list(x), sumList)
    # a = np.array(sumList)
#    x = [i[0] for i in sumList]
#    y = [i[1] for i in sumList]
#    z = [i[2] for i in sumList]
    
    x = map(lambda xyz: xyz[0], sumList)
    y = map(lambda xyz: xyz[1], sumList)
    z = map(lambda xyz: xyz[2], sumList)
    
#    print (len (x))
    x = np.array(x).reshape(26, 26).T
    y = np.array(y).reshape(26, 26).T
    z = np.array(z).reshape(26, 26).T

    matplotlib.rcParams['contour.negative_linestyle'] = 'solid'
    plt.figure()

    CS = plt.contour(x, y, z, plotDensity,
                     colors='k', # negative contours will be dashed by default
                     )
    plt.clabel(CS, fontsize=9, inline=1)
    
    plt.xlabel("Number of Tries")
    plt.ylabel('Height of Tries')
    
    plt.savefig(figurePath, format="svg")
    #plt.show()


#from mpl_toolkits.mplot3d import Axes3D
#from matplotlib import cm

#def JDRplot2(tries, height, r, figurePath):
#    fig = plt.figure()
#    ax = fig.gca(projection='3d')
#    surf = ax.plot_trisurf(tries, height, r, cmap=cm.jet, linewidth=0.2)

#    fig.colorbar(surf, shrink=0.5, aspect=5)
#    ax.set_xlabel('No. of Tries')
#    ax.set_ylabel('Height of Tries')
#    ax.set_zlabel('JDR ratio')

#    plt.savefig(figurePath, format="svg")
