#!/bin/bash

#check whether the data files are in place
if [ ! -d ./rawdata/trc2/ ]; then
    mkdir -p ./rawdata/trc2/
fi
trc2csv="./rawdata/trc2/headlines-docs.csv"
if [ ! -f  $trc2csv ]; then
	echo "trc2 dataset not found. It is expected to be in ./rawdata/trc2/headlines-docs.csv"
	echo "will exit now"
	exit 2
fi
cifarBinaryDir="./rawdata/cifar10/cifar-10-batches-bin/"
if [ ! -f $cifarBinaryDir'data_batch_1.bin' ]; then
	echo "Binary cifar dataset not found, will attempt to download it"
	mkdir -p ./rawdata/cifar10/
	cd ./rawdata/cifar10/
    wget http://www.cs.toronto.edu/~kriz/cifar-10-binary.tar.gz
    tar -xvf cifar-10-binary.tar.gz
    cd ../../
fi
cifarPythonDir="./rawdata/cifar10/cifar-10-batches-py/"
if [ ! -f $cifarPythonDir'data_batch_1' ]; then
	echo "Python cifar dataset not found, will attempt to download it"
	mkdir -p ./rawdata/cifar10/
	cd ./rawdata/cifar10/
    wget http://www.cs.toronto.edu/~kriz/cifar-10-python.tar.gz
    tar -xvf cifar-10-python.tar.gz
    cd ../../
fi

echo "All data found, start compiling"

#compile the sources
cd TwisterTries
mvn assembly:assembly -DdescriptorId=jar-with-dependencies
cd ../
jarFile=TwisterTries/target/TwisterTries-1.0.0-jar-with-dependencies.jar
echo "Compiling done, starting preprocessing"

#Pre-processing
if [ ! -f data/cifar10 ] || [ ! -f data/trc2 ]; then
    java -XX:-AggressiveOpts -Xmx120g -Xms63g -Xss5g -jar $jarFile preprocess $cifarBinaryDir $trc2csv
else
    echo "Preprocessed data found"
fi

echo "Preprocessing done, starting experiments"

#experiments
java -XX:-AggressiveOpts -Xmx100g -Xms63g -Xss5g -jar $jarFile BkJDR
echo "experiment 1-4 done, starting experiment 5"
rm -f ./output/timing/cifar10/*
rm -f ./output/timing/trc2/*
#experiments for up to 500,000 articles
java -XX:-AggressiveOpts -Xmx100g -Xms63g -Xss5g -jar $jarFile time
#the large TRC2 experiments are performed separately since otherwise the JVM might go out of memory
java -XX:-AggressiveOpts -Xmx100g -Xms63g -Xss5g -jar $jarFile TRC2TimeLarge 1000000
java -XX:-AggressiveOpts -Xmx100g -Xms63g -Xss5g -jar $jarFile TRC2TimeLarge 1500000
java -XX:-AggressiveOpts -Xmx100g -Xms63g -Xss5g -jar $jarFile TRC2TimeLarge 1675575

echo "experiment 5 done, starting experiment 6"
rm -f ./output/memory/cifar10/*
rm -f ./output/memory/trc2/*
#experiments for up to 500,000 articles
java -XX:-AggressiveOpts -Xmx120g -Xms63g -Xss5g -jar $jarFile memory
#the large TRC2 experiments are performed separately since otherwise the JVM might go out of memory
java -XX:-AggressiveOpts -Xmx120g -Xms63g -Xss5g -jar $jarFile TRC2MemoryLarge 1000000
java -XX:-AggressiveOpts -Xmx120g -Xms63g -Xss5g -jar $jarFile TRC2MemoryLarge 1500000
java -XX:-AggressiveOpts -Xmx120g -Xms63g -Xss5g -jar $jarFile TRC2MemoryLarge 1675575
echo "all experiments done, starting postprocessing"

#Postprocessing
mkdir -p ./output/evaluate
mkdir -p ./plots/fowlkesAndMallows
mkdir -p ./plots/JDR
mkdir -p ./plots/timing
mkdir -p ./plots/memory

cd postprocess
python postprocess.py ..
cd ..
