#!/bin/bash

PATH=/usr/bin:/bin:/usr/local/bin

case $1 in
    dev) ;;
    qa) ;;
    prod) ;;
    *)
        echo "usage: $0 dev|qa|prod"
        exit 1
        ;;
esac

instance=$1

targDir=/home/pds/$instance/tomcat/webapps/pds/temp
targDir2=/drsmisc/pds/$instance/async

grepString="pds_${instance}_cleanup";

cronList=/drsmisc/conf/cron-list

jobHosts=`grep -v '^#' $cronList | grep "$grepString" | cut -f2 -d: | sed -e "s/\,/ /g"`
#echo $jobHosts

thisHost=`hostname -s`

workit=no

for jobHost in $jobHosts
do
    if [ "$thisHost" = "$jobHost" ]; then workit=yes; fi
done

if [ "$workit" = "yes" ]; then
    find $targDir -type f -mtime +1 -exec rm -f {} \; 
    find $targDir2 -type f -mtime +7 -exec rm -f {} \;
else
    echo "pds cleanup - $instance instance cannot run on $thisHost";
fi
