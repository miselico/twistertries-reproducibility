'''
This program is written to compare the similarity of dendograms
by differnet Hierarchical Clustering method.

Based on:
A Method for Comparing Two Hierarchical Clusterings - E.B. Fowlkes
'''
import numpy as np


class bkMeasure:
    def __init__(self, dendogram1, dendogram2):
        self.dendogram1 = dendogram1
        self.dendogram2 = dendogram2
        self.Bk = []
        self.expectBk = []
        self.varBk = []
        self.maxK = len(dendogram2)
        # pass
        
    def computeBk(self):
        currentCluster1 = {}
        currentCluster2 = {}
        for i in range(self.maxK+1):
            currentCluster1[i] = set([i])
            currentCluster2[i] = set([i])
        for i in range(self.maxK-1):
            #if i == 25:
			#	print "CUT OFF B_K"
			#	break
            if i%50==0:
				print "Now computing B_k of level %d/%d" % (i,self.maxK-1)
            currentCluster1[i+self.maxK+1] = currentCluster1[self.dendogram1[i][0]].union(currentCluster1[self.dendogram1[i][1]])
            currentCluster2[i+self.maxK+1] = currentCluster2[self.dendogram2[i][0]].union(currentCluster2[self.dendogram2[i][1]])
            currentCluster1.pop(self.dendogram1[i][0])
            currentCluster1.pop(self.dendogram1[i][1])
            currentCluster2.pop(self.dendogram2[i][0])
            currentCluster2.pop(self.dendogram2[i][1])

            Bk, EBk, VBk = self.__computeBi(currentCluster1, currentCluster2)
            self.Bk.append(Bk)
            self.expectBk.append(EBk)
            self.varBk.append(VBk)
        self.Bk.reverse()
        self.expectBk.reverse()
        self.varBk.reverse()

    def __computeBi(self, currentCluster1, currentCluster2):
        m = np.zeros([len(currentCluster1), len(currentCluster2)])
        p = 0
        for i in currentCluster1.iterkeys():
            q = 0
            for j in currentCluster2.iterkeys():
                m[p][q] = len (currentCluster1[i].intersection(currentCluster2[j]) )
                q += 1
            p += 1
        n = self.maxK + 1
        Tk = np.sum(m**2) - n
        Pk = np.sum( np.sum(m,axis=1)**2 ) - n
        Qk = np.sum( np.sum(m,axis=0)**2 ) - n
        Pk1 = np.sum( np.sum(m,axis=1) * (np.sum(m,axis=1)-1) * (np.sum(m,axis=1)-2) )
        Qk1 = np.sum( np.sum(m,axis=0) * (np.sum(m,axis=0)-1) * (np.sum(m,axis=0)-2) )
        
        Bk = Tk / np.sqrt(Pk * Qk)
        EBk = np.sqrt(Pk*Qk) / (n*(n-1))
        VBk = 2/(n*(n-1)) + 4*Pk1*Qk1/( n*(n-1)*(n-2)*Pk*Qk ) + (Pk-2-4*Pk1/Pk)*(Qk-2-4*Qk1/Qk)/(n*(n-1)*(n-2)*(n-3)) - Pk*Qk/(n**2 * (n-1)**2)

        return Bk, EBk, abs(VBk)


