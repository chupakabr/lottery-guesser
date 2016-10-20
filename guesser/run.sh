#!/bin/bash

if [ -z "$1" ]; then
	echo "Run: $0 <config-file-path> [number-of-simulations]"
	exit 2
fi
configFile="$1"

simulationsNum="$2"
if [ -z "$simulationsNum" ]; then
	simulationsNum=1
fi

./gradlew run -PappArgs="['-c','$configFile','-n','$simulationsNum']"

