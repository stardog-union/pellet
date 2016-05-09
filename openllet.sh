#!/bin/sh


#!/bin/bash
# This script runs the Pellet CLI.
# Before running this script for the first time
# you may need to run:
# chmod +x pellet.sh
#
# run ./pellet.sh for the usage

if [ ! -d "cli/target/pelletcli/bin" ]; then
mvn -quiet clean install -DskipTests
fi

chmod u+x cli/target/pelletcli/bin/*
cli/target/pelletcli/bin/pellet $@
