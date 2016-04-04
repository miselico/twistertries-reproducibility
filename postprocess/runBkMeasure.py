#!/usr/bin/env python
# -*- coding: utf-8 -*-

from lib.bkMeasure import *
import numpy as np
#http://stackoverflow.com/a/3054314
import matplotlib
matplotlib.use('Agg')
import pylab

def runBkMeasure(pathstd, denPath, figurePath):
    '''
    return a tuple of three list
    '''
    dendogram1 = np.loadtxt(pathstd)
    dendogram2 = np.loadtxt(denPath)

    # run algorithm
    bkM = bkMeasure(dendogram1, dendogram2)
    bkM.computeBk()
    print "B_k computed"
    plotBkMeasure(bkM.Bk, bkM.expectBk, bkM.varBk, figurePath)

def plotBkMeasure(bk, ek, vk, figurePath):
    #print bk
    #print ek
    #print vk
    
    k = list(range(len(bk)))
    
    #for i,j in enumerate(bk):
    pylab.ioff()
    pylab.figure()
    pylab.plot(k, bk, '.', label='Bk')

    pylab.plot(k, ek, label='E(Bk)')
    #pylab.plot(k, ek+2*np.sqrt(vk), '-.r', label='limit range')
    #pylab.plot(k, ek-2*np.sqrt(vk), '-.r')
    #for i in range(len(ek)):
    pylab.fill_between(k, ek+4*np.sqrt(vk), ek-4*np.sqrt(vk), facecolor='red', interpolate=True )

    # figure setting
    pylab.xlim(2,k[-1])
    pylab.ylim(0,1.0)
    pylab.legend(loc='upper right')
    pylab.xlabel('Number of Clusters')
    pylab.ylabel('Bk')
    # pylab.title('Bk measure between two algorithm')

    # show result
    pylab.savefig(figurePath, format='svg')

