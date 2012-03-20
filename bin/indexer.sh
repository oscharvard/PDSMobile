#!/bin/sh

PATH=/usr/bin:/bin:/usr/local/bin

# validate input parameters
case $1 in
    dev) ;;
    qa) ;;
    prod) ;;
    *)
        echo "usage: $0 dev|qa|prod"
        exit 1
        ;;
esac

# determine if this script can run on this host
instance=$1
grepString="pds_${instance}_indexgen";
cronList=/drsmisc/conf/cron-list
jobHosts=`grep -v '^#' $cronList | grep "$grepString" | cut -f2 -d: | sed -e "s/\,/ /g"`
thisHost=`hostname -s`
workit=no
for jobHost in $jobHosts
do
    if [ "$thisHost" = "$jobHost" ]; then workit=yes; fi
done

if [ "$workit" = "yes" ]; then
    # get the classpath of this directory
	CURPATH=`dirname $0`
	JCPATH=${CURPATH}/../lib
	JAVAHOME=/usr/java6
	JAVAEXEC=${JAVAHOME}/bin/java

	# Add on extra jar files to APPCLASSPATH
	for i in "$JCPATH"/*.jar; do
  		APPCLASSPATH="$APPCLASSPATH":"$i"
	done
	$JAVAEXEC -Xmx256m -Xms128m -classpath $APPCLASSPATH edu.harvard.hul.ois.pds.index.IndexGenerator -c ${CURPATH}/../conf/pds.conf
else
    echo "pds index gen - $instance instance cannot run on $thisHost";
fi
















