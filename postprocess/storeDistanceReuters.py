#!/usr/bin/python
# -*- coding: UTF-8 -*-

import cPickle
from lib.generateData import DataGenerator

def computeDistanceReuters(rawPath, distanceMatrixPath):

    print "Computing Distance Matrix for TRC2"
    # take first n element
    n = 1000

    size = 0
    article = []

    with open(rawPath, 'rb') as f:
        for line in f:
            if size >= n:
                break
            lineList = line.split()[1:]
            article.append(set(lineList))
            size += 1

    # calculate the distance matrix
    a = DataGenerator()
    a.setData(article)
    a.computeDistanceMatrix()

    output = open(distanceMatrixPath, 'wb')
    cPickle.dump(a.matrix, output)
    output.close()
