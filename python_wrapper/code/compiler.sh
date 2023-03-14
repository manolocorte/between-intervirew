#!/bin/sh
# Script which will compile the 3 given C++ files into a python package installable by pìp

# Adding swig
apt-get update 
apt-get install -y swig 

# Compiling C++ code 
g++ -c -fPIC one.cpp
gcc -shared -o libone.so one.o
swig -python -c++ -I. one.i



