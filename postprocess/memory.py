#!/usr/bin/env python
# -*- coding: utf-8 -*-

import numpy as np
import pylab
import collections

def plotMemory(dataPath, figurePath):
    data = collections.defaultdict(list)

    with open(dataPath, 'rb') as f:
        for line in f:
            parts = line.strip().split(' ')
            if len(parts) != 2:
                raise Exception("must have two parts")
            amount = int(parts[0])
            memory = int(parts[1])
            data[amount].append(memory)

    d = [[i, np.average(data[i])] for i in sorted(data.keys())]

    d = np.array(d)
    x = d[:, 0] / 10000.0
    y = d[:, 1] / (1024.0 ** 3)
    pylab.figure()
    pylab.scatter(x, y, s=20, c='r')
    slope, shift = pylab.polyfit(x, y, 1)
    nowY = x * slope + shift
    pylab.plot(x, nowY, 'b')

    pylab.xlabel('size(1e4)')
    pylab.ylabel('memory (Gigabyte)')
    #pylab.show()
    pylab.savefig(figurePath, format='svg')
# plotMemory('mem-reu.dat', 'sdf')
