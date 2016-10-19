#!/bin/bash

if [ -z "$1" ]; then
	echo "Run: $0 <config-file-path>"
	exit 2
fi
configFile="$1"

./gradlew run -PappArgs="['-c','$configFile']"

