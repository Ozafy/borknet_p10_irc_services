@echo off
rem 
rem Used for compiling on windows.
rem 

 set CLASSPATH=F:\Work;
rem set CLASSPATH=D:\Java;

 javac -target 1.6 -source 1.6 *.java
 javac -target 1.6 -source 1.6 core/*.java
 javac -target 1.6 -source 1.6 core/commands/*.java
 javac -target 1.6 -source 1.6 core/modules/bob/*.java
 javac -target 1.6 -source 1.6 core/modules/s/*.java
 javac -target 1.6 -source 1.6 core/modules/v/*.java
pause