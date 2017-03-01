#!/bin/bash

export CLASSPATH=.
for p in lib/*.jar; do
	export CLASSPATH="$CLASSPATH:$p"
done

cp target/mapeditor-0.0.1.jar .
java -Dprism.order=sw -Xmx500m -jar mapeditor-0.0.1.jar "$@"

