#!/usr/bin/env python
# -*- coding: utf-8 -*-


__author__ = "Mou Hao"

import os

from runFastCluster import fastClusterReuters, fastClusterCifar
from storeDistanceReuters import computeDistanceReuters
from runBkMeasure import runBkMeasure, plotBkMeasure
from runJoinDistance import runJoinDistance
from timing import plotTiming
from memory import plotMemory

def main(rootPath):
   
    '''
    Setting the path
    '''

    # 1. Data that are provided to the evaluation part

    dataCifarPath = os.path.join(rootPath, "rawdata", "cifar10", "cifar-10-batches-py", "data_batch_1")
    dataReutersPath = os.path.join(rootPath, "data", "trc2")

    # timing data and memery data path
    timingCifarResultPath = os.path.join(rootPath, "output", "timing", "cifar10", "timing.txt")
    timingReutersResultPath = os.path.join(rootPath, "output", "timing", "trc2", "timing.txt")

    memoryCifarResultPath = os.path.join(rootPath, "output", "memory", "cifar10", "memory.txt")
    memoryReutersResultPath = os.path.join(rootPath, "output", "memory", "trc2", "memory.txt")
   

    # 2. The path of files during the evaluation

    distanceCifarPath = os.path.join(rootPath, "output", "evaluate", "distanceCifar")
    distanceReutersPath = os.path.join(rootPath, "output", "evaluate", "distanceReuters")

    # the dendrogram by the definite algorithm
    denStdCifarPath = os.path.join(rootPath, "output", "evaluate", "standardDenCifar")
    denStdReutersPath = os.path.join(rootPath, "output", "evaluate", "standardDenReuters")

    # dendrogram of TRC2 1000 size, 
    # with 1 trie and 1 height
    # and 120 trie and 120 height
    den1_1Path = os.path.join(rootPath, "output", "exp1_4", "trc2", "full_data_1_tries_1_height_699983548740754765_seed.dat")
    den120_120Path = os.path.join(rootPath, "output", "exp1_4", "trc2", "full_data_120_tries_120_height_699983548740754765_seed.dat")
   
    # twisted algorithm dendrogram path
    # folder of cifar results
    cifarFolderPath = os.path.join(rootPath, "output", "exp1_4", "cifar10")
    # folder of reuters results
    reutersFolderPath = os.path.join(rootPath, "output", "exp1_4", "trc2")



    # 3. Output figures' path

    TRC2BKFigure1_1Path = os.path.join(rootPath, "plots", "fowlkesAndMallows", "BK1-1.svg")
    TRC2BKFigure120_120Path = os.path.join(rootPath, "plots", "fowlkesAndMallows", "BK120-120.svg")

    cifarJDRFigurePath = os.path.join(rootPath, "plots", "JDR", "cifarJDR.svg")
    reutersJDRFigurePath = os.path.join(rootPath, "plots", "JDR", "TRC2JDR.svg")

    timingCifarFigurePath = os.path.join(rootPath, "plots", "timing", "cifarTiming.svg")
    timingReutersFigurePath = os.path.join(rootPath, "plots", "timing", "TRC2Timing.svg")
    memoryCifarFigurePath = os.path.join(rootPath, "plots", "memory", "cifarMemory.svg")
    memoryReutersFigurePath = os.path.join(rootPath, "plots", "memory", "TRC2Memory.svg")





    '''
    Actual Running Part
    '''
    # 1. compute the distance mateix of REUTERS
    computeDistanceReuters(dataReutersPath, distanceReutersPath)

    # 2. apply fast cluster method on cifar and reuters data
    #    to the get the standard hierarchy clustering result
    fastClusterCifar(dataCifarPath, denStdCifarPath, distanceCifarPath)
    fastClusterReuters(distanceReutersPath, denStdReutersPath)

    # 3. calculate BK
    # 3.1 calculate the BK for trie 1 and height 1
    print "Calculating the BK for trie 1 and height 1"
    runBkMeasure(denStdReutersPath, den1_1Path, TRC2BKFigure1_1Path)
    # 3.2 calcualte the BK for trie 120 and height 120
    print "Calculating the BK for trie 120 and height 120"
    runBkMeasure(denStdReutersPath, den120_120Path, TRC2BKFigure120_120Path)

    # 4. calculate JDR
    # 4.1 JDR for Cifar
    runJoinDistance(distanceCifarPath, denStdCifarPath, cifarFolderPath, os.listdir(cifarFolderPath), cifarJDRFigurePath, 7)
    
    # 4.2 JDR for TRC2
    runJoinDistance(distanceReutersPath, denStdReutersPath, reutersFolderPath, os.listdir(reutersFolderPath), reutersJDRFigurePath, 9)
    
    # 5. timing
    print ("Creating the timing plots")
    plotTiming(timingCifarResultPath, timingCifarFigurePath)
    plotTiming(timingReutersResultPath, timingReutersFigurePath)
    # 6. memory
    print ("Creating the memory plots")
    plotMemory(memoryCifarResultPath, memoryCifarFigurePath)
    plotMemory(memoryReutersResultPath, memoryReutersFigurePath)
    print ("postprocessing done")

if __name__ == '__main__':
    import sys
    rootPath = sys.argv[1]
    main(rootPath)
