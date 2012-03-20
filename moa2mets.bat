@echo off
::-----------------------------------------------------------------
:: Windows batch script to execute the moa2 to mets converter
::
:: IMPORTANT: edit this top section to reflect where you have
:: your java jvm and the converter if it is 
:: different from where this script assumes.
::

:: where your java JVM resides (java.exe)
Set JAVA_HOME=c:\j2sdk1.4.2_06\bin

:: the name of the directory containing the converter
:: classes, lib, etc. directories
Set M2M_DIR=c:\moa2mets

:: YOU SHOULDN'T NEED TO CHANGE ANYTHING BELOW THIS LINE
::-----------------------------------------------------------------


:: Add JAVA runtime bin folder to your existing path environment variable
set path=%path%;%JAVA_HOME%
set LIB=%M2M_DIR%\lib

:: Set classpath to include required jars from converter
set classpath=%M2m_DIR%\classes;%LIB%\app.jar;%LIB%\mets.jar;%LIB%\ojdbc14.jar;%LIB%\pdx.jar;%LIB%\util.jar;%LIB%\xercesImpl.jar;%LIB%\xml.jar;%LIB%\xmlParserAPIs.jar

:: Run the application
java -Xmx1000m edu.harvard.hul.ois.pdx.Moa2Mets -c %M2M_DIR%\conf\moa2mets.conf %1 %2 %3 %4 %5 %6 %7 %8
