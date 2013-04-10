#!/bin/bash

if [ "$#" != "3" ]; then
  echo Invalid number of arguments to this script
  exit 1
fi

RELEASE=$1
DESTINATION=$2
REGISTRATION=$3

ZIPFILE=pellet-${RELEASE}.zip
PREFIX=pellet`echo $RELEASE | sed 's/\.//g'`
REFERRER_KEY=${PREFIX}.referrer
REFERRER_VALUE=http://clarkparsia.com/pellet/download/pellet-${RELEASE}
REDIRECT_KEY=${PREFIX}.redirect
REDIRECT_VALUE=http://dl.clarkparsia.com/downloads/\{HASH\}/pellet-${RELEASE}.zip

if [ -e "$DESTINATION/$ZIPFILE" ]; then
  echo "There is already a file named $ZIPFILE in $DESTINATION"
  exit 1
fi

cp /home/dl/$ZIPFILE $DESTINATION
chmod 644 $DESTINATION/$ZIPFILE

if [ `grep $REFERRER_KEY < $REGISTRATION | wc -l` -gt "0" ]; then
  echo "There are already entries for this release at the registration servlet properties"
  exit 1
fi

cp -f $REGISTRATION $REGISTRATION.tmp
cp -f $REGISTRATION $REGISTRATION.bak
echo "$REFERRER_KEY = $REFERRER_VALUE" >> $REGISTRATION.tmp
echo "$REDIRECT_KEY = $REDIRECT_VALUE" >> $REGISTRATION.tmp

mv -f $REGISTRATION.tmp $REGISTRATION

MD5SUM=`md5sum $ZIPFILE | awk '{ print $1; }'`
SIZE=`stat -c "%s" $ZIPFILE | perl -e 'while(<>) { print(sprintf("%.1f", $_ / (1024 * 1024))); }'`


echo "Now create a page 'Pellet $RELEASE Release Download' under 'Pellet: The Open Source OWL Reasoner' | 'Pellet Relases & Source Code"
echo "The slug for this page should be pellet-$RELEASE"
echo "The body of the page should contain the following tag"
echo "<r:snippet name=\"registration-form\"/>"
echo
echo "Also create an additional tab named 'registration_msg' that contains the following message:"
echo 
echo "<p>Please register to download Pellet $RELEASE. After submitting, your browser should automatically begin downloading the Pellet $RELEASE distribution&mdash; a $SIZE MB ZIP file. The MD5 checksum for the file is <tt>$MD5SUM</tt></p>"




