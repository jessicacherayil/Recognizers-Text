@ECHO off

ECHO # Java environment info
CALL mvn -v

ECHO # Clean resources
DEL /S /Q libraries\recognizers-text-number\src\main\java\com\microsoft\recognizers\text\number\resources\*.java
DEL /S /Q libraries\recognizers-text-number-with-unit\src\main\java\com\microsoft\recognizers\text\numberwithunit\resources\*.java
DEL /S /Q libraries\recognizers-text-date-time\src\main\java\com\microsoft\recognizers\text\datetime\resources\*.java
DEL /S /Q libraries\recognizers-text-choice\src\main\java\com\microsoft\recognizers\text\choice\resources\*.java

ECHO # Generate resources
CALL set MAVEN_OPTS=-Dfile.encoding=utf-8
CALL mvn compile exec:java -pl libraries/resource-generator/

ECHO # Building Java platform
CALL mvn package