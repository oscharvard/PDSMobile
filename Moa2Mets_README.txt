Use the moa2mets.bat batch file to start a moa2 to mets conversion.

usage: 

moa2mets.bat [-m mode(drs/local)] [-o output] [-j (jp2/jpeg)] [-g yes] id/filename [path]

Command line options:

-m : (local or drs) Mode for converting MOA2 files. For predeposited MOA2's
      this should always be 'local'

-j : (jp2 or jpeg) This sets the mimetype of files as specified with <File HARVMIME="color-deliverable">
     in the MOA2 to either jp2 or jpeg in the resulting METS. 
     The default when this is not specified is jpeg.
     NOTE:  A mix of jpeg and jp2's in the MOA2 will produce incorrect results.

-o : output location for converted file. This should be a fully qualified
     path name.

-g : mets output validation.  This is optional, and will validate the METS file.

id/filename : The DRS ID, or the local filename of the file to be converted.  
              When -m is set to local this needs to be the local filename,
              WITH the file extension, EXCLUDING the full path.  
	      For example: 
              This is CORRECT: 002282518.xml
	      This is NOT CORRECT: c:\moa2mets\test\002282518.xml

path :  This is the path to the above filename without any trailing forward or 
        backward slashes. This is required when doing a local file conversion.  
        For Example: 
        This is CORRECT: c:\moa2mets\test
        This is NOT CORRECT: c:\moa2mets\test\


Full Example:

#> moa2mets.bat -m local -o c:\moa2mets\test\output\test3.xml -g yes 002282518.xml c:\moa2mets\test


Notes:

* the moa2mets.bat file needs to be configured properly for your environment.
  This means that you need to set the filepath for Java and the directory 
  containing the converter.  For example:
	Set JAVA_HOME=c:\j2sdk1.4.2_06\bin
	Set M2M_DIR=c:\moa2mets

* If -o is not set, the output will be displayed on the command line.

* When doing a local conversion the filename and path parameters need to be set properly

* Output validation (-g option) will only work if the file if -o is also set

* A 'Non Standard Date' warning indicates that the date in the <coreDate> tag is not set as 
  an attribute, but rather as xmlData, and will be ignored.