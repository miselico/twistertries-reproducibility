'''
get data from file or generate data itself

compute distance matrix
'''

import numpy as np
import random

class DataGenerator:

    def __init__(self):
        self.numberOfSet = 0
        self.data = []
        self.matrix = np.array([])

    def setData(self, data):
        '''
        data should be a list of sets
        '''
        self.data = data
        self.numberOfSet = len(data)

    def getDataFromFile(self, dataFile):
        '''
        compact to micheal's raw data set
        '''
        fdata = []
        num = 0
        with open(dataFile, 'rb') as f:
            for line in f:
                num += 1
                fdata.append( set(line[(line.find(':')+1):].split()) )
        self.data = fdata
        self.numberOfSet = num

    def randomDataGenerate(self, numberOfSet):
        self.numberOfSet = numberOfSet
        for j in range(self.numberOfSet):
            amount = random.randint(1,5)
            row = []
            for i in range(amount):
                row.append( random.randint(1,1000) )
            self.data.append(set(row))
        # print data

    def computeDistanceMatrix(self):
        distanceMatrix = np.zeros((self.numberOfSet,self.numberOfSet))
        for i in range(self.numberOfSet):
            for j in range(i):
                distanceMatrix[i,j] = self.__jaccordSimi(self.data[i], self.data[j])
                # print distanceMatrix[i,j]
        distanceMatrix = distanceMatrix + np.transpose(distanceMatrix)
        for i in range(self.numberOfSet):
            distanceMatrix[i,i] = 1
        self.matrix = 1 - distanceMatrix

    


    def __jaccordSimi(self, aSet, bSet):
        c = aSet.intersection(bSet)
        m = 0
        try:
            m = float(len(c)) / (len(aSet) + len(bSet) - len(c))
        except ZeroDivisionError:
            print len(aSet), len(bSet), len(c)
        return m
