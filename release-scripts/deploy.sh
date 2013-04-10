#!/bin/bash

# This script copies the build artifact (pellet distribution zip file) to the remote directory along with the installation script (deploy-remote.sh)
# The installation script is then remotely executed via ssh

if [ "$#" != "7" ]; then
  echo Invalid number of arguments to this script
  exit 1
fi

# Parameters of this script

ZIPFILE=$1                 # the path the zip file relative to the project root
RELEASE=$2                 # the release version (e.g., 2.2.0)
HOST=$3                    # the host where the artifact should be deployed
USER=$4                    # the user name with which to connect 
DIRECTORY=$5               # the directory where the artifact and the installation script should be initially copied (usually the home directory of the USER)
DEPLOY_DOWNLOAD_DIR=$6     # the directory to which the artifact will ultimately be copied (and be available for download)
REGISTRATION_PROPERTIES=$7 # the file containing the properties of the registration servlet

RELEASE_SCRIPTS=release-scripts
DEPLOY_REMOTE=deploy-remote.sh


scp $ZIPFILE ${RELEASE_SCRIPTS}/${DEPLOY_REMOTE} ${USER}@${HOST}:${DIRECTORY}

if [ "$?" != 0 ]; then
  echo "Copy to the server failed"
  exit 1
fi

ssh ${USER}@${HOST} "/bin/bash ${DIRECTORY}/${DEPLOY_REMOTE} $RELEASE $DEPLOY_DOWNLOAD_DIR $REGISTRATION_PROPERTIES"

if [ "$?" != 0 ]; then
  echo "Execution of the remote script on the server failed"
  exit 1
fi

echo Deployment complete