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

# create symbolic link for /drsmisc/pds/ search crawler index 
echo "checking for tomcat/webapps/pds/index symbolic link..."
indexlink=`test -h tomcat/webapps/pds/index; echo $?`
if [ "$indexlink" = "1" ]; then
	echo "not found, creating symbolic link to /drsmisc/pds/$instance/index/ from tomcat/webapps/pds/index"
    ln -s /drsmisc/pds/$instance/index tomcat/webapps/pds/index
else
    echo "symbolic link found";
fi

# create symbolic link for /drsmisc/pds/instance/async 
echo "checking for tomcat/webapps/pds/temp/async symbolic link..."
indexlink=`test -h tomcat/webapps/pds/temp/async; echo $?`
if [ "$indexlink" = "1" ]; then
        echo "async dir not found, creating symbolic link to /drsmisc/pds/$instance/async/ from tomcat/webapps/pds/temp/async"
    ln -s /drsmisc/pds/$instance/async tomcat/webapps/pds/temp/async
else
    echo "async symbolic link found";
fi

