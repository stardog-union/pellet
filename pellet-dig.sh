#!/bin/sh

java -Xss4m -Xms30m -Xmx200m -classpath lib/pellet.jar org.mindswap.pellet.dig.PelletDIGServer "$@"
