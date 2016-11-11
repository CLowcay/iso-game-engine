#!/bin/bash

set -e

version=$1
jar=$2
mainClass=$3
baseName=$4
postFix=$5
packageName="$baseName$postFix"
echo javapackager -deploy \
    -BappVersion=$version \
    -Bcategory=Finance \
    -BlicenseType=GPLv3 \
    -Bemail=info@bitsquare.io \
    -native deb \
    -name $packageName \
    -title $packageName \
    -vendor $packageName \
    -outdir build \
    -srcfiles $jar \
    -appclass $mainClass \
    -outfile $packageName



javapackager -deploy \
    -BappVersion=$version \
    -Bcategory=Finance \
    -BlicenseType=GPLv3 \
    -Bemail=info@bitsquare.io \
    -native deb \
    -name $packageName \
    -title $packageName \
    -vendor $packageName \
    -outdir build \
    -srcfiles $jar \
    -appclass $mainClass \
    -outfile $packageName

# -Bicon=client/icons/icon.png \
