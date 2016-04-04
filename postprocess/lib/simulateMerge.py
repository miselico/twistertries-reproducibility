#!/usr/bin/python
# -*- coding: UTF-8 -*-

'''
The idea of this program is to regenerate the process
of dendrogram from the only the merge information and
the row dataset
'''

from generateData import DataGenerator
import numpy as np


class simulateMerge:
    def __init__(self, mergeInfo, rawData, haveMatrix=False):
        '''
        mergeInfo should be file path
        rawData should be file path
        '''
        self.mergeInfo = np.loadtxt(mergeInfo)
        self.reBuildData = []
        self.index = {}
        self.dataSize = len(self.mergeInfo) + 1
        self.clusters = {}
        
        # print 'datasize', self.dataSize
        for i in range(self.dataSize):
            self.index[i] = i
            self.clusters[i] = set([i])
        # print self.clusters[990]
        #print haveMatrix
        
        if haveMatrix == False:
            dataGenerator = DataGenerator()
            dataGenerator.getDataFromFile(rawData)
            dataGenerator.computeDistanceMatrix()
            # DisMatrix is numpy.array
            self.disMatrix = dataGenerator.matrix
        
            del dataGenerator
    
        else:
            self.disMatrix = rawData
            
        
        
    def __computeDistance(self,a,b):
        resout = 0
        len1 = len(a)
        len2 = len(b)
        for i in a:
            for j in b:
                resout += self.disMatrix[i, j]
        return resout/float(len1*len2)

    def simulation(self):
        
        #print 'simualtion merge'
        for i in range(self.dataSize-1):
            row = []
            m,n = self.mergeInfo[i][0], self.mergeInfo[i][1]
            # actualM, actualN = self.index[m], self.index[n]

            row.append(m)
            row.append(n)


            row.append(self.__computeDistance(self.clusters[m], self.clusters[n]))
            #row.append(self.disMatrix[actualM][actualN])
            
            # change index
            # self.index[i+self.dataSize] = actualN
            self.clusters[i+self.dataSize] = self.clusters[m].union(self.clusters[n])



            # update matrix
            #self.disMatrix[actualN] = ( self.disMatrix[actualN] * len(self.clusters[n]) + \
            #                            self.disMatrix[actualM] * len(self.clusters[m]) ) / \
            #                           ( len(self.clusters[n]) + len(self.clusters[m]) )
            #self.disMatrix[i+self.dataSize] = ( self.disMatrix[n] * len(self.clusters[n]) + \
            #                            self.disMatrix[m] * len(self.clusters[m]) ) / \
            #                          ( len(self.clusters[n]) + len(self.clusters[m]) )
            # update cluster size
            row.append( len(self.clusters[i+self.dataSize]) ) 

            self.reBuildData.append(row)

def test():
    mergeInfo = '../data/result_3level.dat_100_tries_100_height_4654654_seed.dat'
    # mergeInfo = '../data/3level_FAST_Clustering.dat'
    rawData = '../data/3level.dat'
    a = simulateMerge(mergeInfo, rawData)
    a.simulation()
    
    # write into file
    wfile = file(mergeInfo[0:-4]+'FULL.dat', 'w')
    for i in a.reBuildData:
        row = [ str(j) for j in i ]
        line = '\t'.join(row) + '\n'
        wfile.write(line)
    wfile.close()


if __name__ == '__main__':
    test()
