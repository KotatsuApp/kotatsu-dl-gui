#!/bin/bash
set -e
rm -rf ./build/libs
./gradlew jar
rm -rf ./build/dist
JAR_FILE=$(find ./build/libs -name '*.jar')
VERSION=$(echo "$JAR_FILE" | grep -o -e "[0-9.]*[0-9]")
mkdir -p ./build/dist/{windows,linux}
cp -p "$JAR_FILE" ./build/dist/windows
cp -p ./kotatsu-dl.bat ./build/dist/windows
cp -p "$JAR_FILE" ./build/dist/linux
cp -p ./kotatsu-dl.sh ./build/dist/linux
tar -czvf "./build/dist/kotatsu-dl-$VERSION-linux.tar.gz" ./build/dist/linux
zip -r "./build/dist/kotatsu-dl-$VERSION-windows.zip" ./build/dist/windows