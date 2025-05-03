#!/usr/bin/env sh
clear
export CLASSPATH=$CLASSPATH:$(pwd)/lib/classes
export CLASSPATH=$CLASSPATH:$(pwd)
make clean
make
java Main.Main test.tig
