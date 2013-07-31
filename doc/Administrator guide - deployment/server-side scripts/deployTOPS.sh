#!/bin/bash
orgdir=$(pwd)
echo $orgdir
if [ "$1" = "" ] ; then
	package="$(ls /root/TOPS-v*.war | sort -r | head -1)"
else
	package="$1"
fi
echo "Deploying $package ..."
/etc/init.d/tomcat7 stop
/etc/init.d/apache2 stop
cd /var/lib/tomcat7/

# This directory is for database back-ups
if [ ! -d TOPS-back-ups ]; then
mkdir TOPS-back-ups
fi

# Archive current/older
cd webapps
mv TOPS ../TOPS-back-ups/TOPS-back-$(date +%F)

# Deploy newest WAR file
mkdir TOPS
cd TOPS
unzip $package
cd ..
chown -R tomcat7:tomcat7 TOPS
cd $orgdir
/etc/init.d/tomcat7 start
/etc/init.d/apache2 start

