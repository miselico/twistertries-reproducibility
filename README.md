This repository contains the files needed for repoducing the results of the 
"Twister Tries: Approximate Hierarchical Agglomerative Clustering for Average Distance in Linear Time"
presented at SIGMOD'15

Contents of the repository
=======================
* README.md : This file
* run.sh : The script to compile all code, run all experiments and perform postprocessing
* postprocess : directory containing all postprocessing code.
* TwisterTries : directory containing a maven project with implementation of the twister tries algorithm and code of the experiments.

Steps to reproduce the results
===============================
(Details are in the following sections)
1. Install needed software
2. Extract the archive (Package.zip)
3. Obtain the needed dataset(s) and place them in the specified directories
4. Run the experiments `bash ./run.sh`
5. Have a lot of patience. Running all experiments and postprocessing took about 25 hours on the hardware described above.
6. Check the output for errors: if not enough memory is available for the largest experiments, they will fail. 
The postporcessing will, however, continue with the results available. 
This is not necessarily problematic to show the linear behavior.
7. Compare the figures produced with the ones in the paper.


Install needed software
=======================
Java
-----

The experiments in the paper were performed using OpenJDK 8 64-bit (1.8.0_25) Server VM. 
The Java VM ran on hardware with two Intel Xeon E5-2670 processors (totaling 16 multi-threaded cores).

Any Java VM 7+ and a modern CPU should be sufficient as long as there is enough memory (the machine we used had 128Gb).
The main goal of the large experiments is to show that the twister tries algorithm scales linear in the the number of items to be clustered.

It is assumed that the system defaults to en_US.UTF-8 encoding

maven
-----
Install maven (tested with 3.13)
Maven is used for resolving dependencies, building and running tests.

Python
------
Python 2.7.6 was used for the postporcessing. The following libraries have to be installed:
* numpy
* fastcluster
* scipy
* cPickle
* matplotlib
* pylab

Obtain the needed dataset(s)
============================
Two datasets are used for the experiments and postprocessing.

The first on is the cifar-10 dataset from http://www.cs.toronto.edu/~kriz/cifar.html
The binary version is used by the twister tries algorithm, the python version by the postprocessing code.
If you have these datasets already, you can place the uncompressed version in ./rawdata/cifar10/cifar-10-batches-bin/ and ./rawdata/cifar10/cifar-10-batches-py/ , respectively.
If the datasets are not there, the script will automatically attempt to download these datasets.

The second dataset is the TRC2 dataset from http://trec.nist.gov/data/reuters/reuters.html
Instruction for obtaining the dataset can be found from that same page.
The file containing all article text (`/headlines-docs.csv`) should be placed in `./rawdata/trc2/`.
If it appears too hard to obtain this dataset, one could try to use another dataset, which should give similar results.
A csv file containing one line for each text fragment to be clustered should be prepared.
Each line should be of the form "date","message","text", where "date" and "message" will be ignored.
The first line of the file will be ignored (headers).
The current setup expects at least 1,675,575 different articles (after preprocessing). This could be changed manually in run.sh to a minimum of 500,000

Run the experiments 
===================
To run the experiments, execute `bash ./run.sh`. This first compiles the java code and creates a runnable jar file.
Then the following steps are executed (from miselico.clusteringLSH.runs.twistertriearticle.Main):

Pre-processing
--------------
Cifar-10: no real pre-processing needed, miselico.clusteringLSH.input.Cifar10Reader reads the provided binary format directly. 
However, the 6 batch files are concatenated to one large data file first.

TRC2: miselico.clusteringLSH.input.preprocess.PreprocessTRC2 implements the needed preprocessing.

Experiments
------------
The experiments use the preprocessed data from ./data/ and generate their output in ./output/

### Experiment 1-4 ###

The clustering code is in miselico.clusteringLSH.runs.twistertriearticle.BkAndJDRExperiments .

### Experiment 5-6 ###

The experiments are divided in runs for memory usage and timing.
 
Timing: miselico.clusteringLSH.runs.twistertriearticle.TimingLargeRun  

Memory : miselico.clusteringLSH.runs.twistertriearticle.MemoryLargeRun

Post-processing
---------------

The post processing is implemented in python. The script `postprocess.py` is started as part of the master script `run.sh`
This reads the output from ./output/ and generates the graphs in ./plots/

Compare the figures produced with the ones in the paper.
==========================================

The experiments have succeeded if
1. The 4 figures for 
Bk (./plots/fowlkesAndMallows/BK1-1.svg and ./plots/fowlkesAndMallows/BK120-120.svg) and 
JDR (./plots/JDR/cifarJDR.svg and ./plots/JDR/TRC2JDR.svg ) 
look very close to the ones in the paper (twister tries is a randomized algorithm, hence the results will vary slightly)
2. The 4 figures for 
timing (./plots/timing/cifarTiming.svg and ./plots/timing/TRC2Timing.svg) and 
memory (./plots/memory/cifarMemory.svg and ./plots/memory/TRC2Memory.svg)
usage show linear behavior


