@echo off
rem this is used by btelnyy, do not modify
mvn package &xcopy .\target\*.jar D:\Minecraft\Test\plugins /Y
