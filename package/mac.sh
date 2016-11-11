#!/bin/bash

set -e

version=$1
jar=$2
mainClass=$3
baseName=$4
postFix=$5
packageName="$baseName$postFix"

javapackager \
    -deploy \
    -BappVersion=$version \
    -Bmac.CFBundleIdentifier=$packageName \
    -Bmac.CFBundleName=$packageName \
    -Bruntime="$JAVA_HOME/../../" \
    -native dmg \
    -name $packageName \
    -title $packageName \
    -vendor $packageName \
    -outdir build \
    -srcfiles $jar \
    -appclass $mainClass \
    -outfile $packageName

#-Bicon=client/icons/mac.icns \
