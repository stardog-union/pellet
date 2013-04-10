#!/bin/bash

if [ "$#" != "2" ]; then
  echo Invalid number of arguments to this script
  exit 1
fi

QADIR=$1
PROJECT_DIRNAME=$2


echo Performing fuzz tests
echo QADIR is $QADIR
echo PROJECT_DIRNAME is $PROJECT_DIRNAME

if [ ! -d $QADIR ]; then
  echo $QADIR is not a directory or it does not exist
  exit 1
fi

cd $QADIR

if [ "$?" != "0" ]; then
  echo "Unable to enter $QADIR"
  exit 1
fi

ZIPFILE=${PROJECT_DIRNAME}.zip

if [ ! -e $ZIPFILE ]; then
  echo There is no zipfile with the expected name $ZIPFILE
  exit 1
fi

unzip $ZIPFILE

if [ "$?" != "0" ]; then
  echo "unzip $ZIPFILE did not complete successfully"
  exit 1
fi

if [ ! -d $PROJECT_DIRNAME ]; then
  echo "There is no $PROJECT_DIRNAME after unzipping"
  exit 1
fi

cd $PROJECT_DIRNAME

# verify that the required files are there

for file in HOW_TO_FILE_PELLET_BUG_REPORT.txt BUGS.txt CHANGES.txt LICENSE.txt pellet.bat pellet.sh README.txt agpl-3.0.txt; do
  echo Checking whether $file exists
  if [ ! -e $file ]; then
    echo "There is no required file $file in the unzipped archive"
    exit 1
  fi
done

# try to run pellet help

sh pellet.sh help

if [ "$?" != "0" ]; then
  echo "Unable to execute sh pellet.sh help"
  exit 1
fi

for pellet_command in classify consistency dig explain extract info lint modularity query realize trans-tree unsat; do
  if [ `sh pellet.sh help | grep ${pellet_command} | wc -l` -lt 1 ]; then 
    echo "pellet help does not report \"${pellet_command}\" command in the available subcommands in help"
    exit 1
  fi
done

# TODO other fuzz tests here

