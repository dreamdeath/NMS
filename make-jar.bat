@echo off
SET DEVELOPMENT_HOME=D:\Dev\workspace_other\AMUZLAB_NMS\

cd %DEVELOPMENT_HOME%
call mvnw -e clean	
call mvnw -e install
