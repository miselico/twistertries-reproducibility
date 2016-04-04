import numpy as np
import pylab

def plotTiming(dataPath, figurePath):
    rawdata = np.loadtxt(dataPath)

    #item_count adding_time twisting_time
    sorteddata = sorted(zip(rawdata[:,0],rawdata[:,1],rawdata[:,2]))

    adding={}
    twisting={}
    for i in sorteddata:
        if i[0] in adding:
            adding[i[0]].append(i[1])
            twisting[i[0]].append(i[2])
        else:
            adding[i[0]] = [i[1]]
            twisting[i[0]] = [i[2]]

    d=[]

    for i in adding.iterkeys():
        averageAdding = np.average(adding[i]) / 1000.0
        averageTwisting = np.average(twisting[i]) / 1000.0
        d.append((i, averageAdding, averageTwisting))
        
    data = np.array(sorted(d))

    #print data
    
    # plot
    pylab.figure()
    pylab.plot(data[:,0], data[:,1], 'ob')

    pylab.plot(data[:,0], data[:,1]+data[:,2], 'ob')

    pylab.fill_between(data[:,0], data[:,1],  data[:,1]+data[:,2], facecolor='red', interpolate=True )
    pylab.fill_between(data[:,0], [0 for i in range(len(adding))],  data[:,1], facecolor='green', interpolate=True )

    p1 = pylab.Rectangle((0, 0), 1, 1, fc="green")
    p2 = pylab.Rectangle((0, 0), 1, 1, fc="red")
    pylab.legend([p1, p2], ['adding time', 'twisting time'], loc='upper left')

    pylab.xlabel("size")
    pylab.ylabel('time used (seconds)')
    pylab.xlim([data[0, 0], data[-1, 0]])


    pylab.savefig(figurePath, format='svg')
