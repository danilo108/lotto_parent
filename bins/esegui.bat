@echo  off
:: Check if JAVA_HOME is setted and use the correct version.

Setlocal EnableDelayedExpansion
set _FIRST_CHECK="FALSE"

SET _CORRECT_JAVA_VERSION="11"

call :CheckJava _J_DETECTED
rem echo  Java Version : %_J_DETECTED%
java -jar cerca.jar
if  NOT %_J_DETECTED% == %_CORRECT_JAVA_VERSION% (
	echo  Incorrect java version in PATH. Let's check JAVA_HOME value
    rem Perhaps use a correct java on PATH but incorrect JAVA_HOME
	rem First let's validate if there is a java_home and correct version
    call :check_java_home _JAVA_HOME_OK

    if "!_JAVA_HOME_OK!" == "OK" (
    	echo  JAVA_HOME correctly setted !JAVA_HOME!
    	:: Let's configure PATH to use JAVA_HOME
        echo ---- prepare path ----
        call :set_java_home
    	goto end_prepare
    )
   goto try_oracle
)

goto java_ok


:try_oracle
:: Let's try a final case, jre installed but not added to path
call :check_oracle_data
call :CheckJava _JAVA_DETECTED
if  NOT %_JAVA_DETECTED% == %_CORRECT_JAVA_VERSION% (
     call :java_not_found
)
goto end_prepare



:CheckJava
echo  _____________ CHECKING JAVA VERSION == %_CORRECT_JAVA_VERSION% _________________
:: Let's check if Java is available on path

for /f "tokens=4" %%g in ('java -fullversion 2^>^&1 ^| findstr /i "java"') do (

    for /f "tokens=1-3 delims=._" %%m in ('echo %%g') do (
        set _VERSION=%%m.%%n"
    )
    echo Detected version !_VERSION!
	SET "%1=!_VERSION!"
	goto :EOF
)
SET "%1=NUL"
goto :EOF



:check_java_home
echo  _______________ CHECKING JAVA_HOME ____________________________________________

if "%JAVA_HOME%" == "" (
    SET "%1=NO_OK"
	echo  JAVA_HOME is not set, unable to use it
	goto :EOF
)
for %%A in ("!JAVA_HOME!") do (
	SET _SHORT_JAVA=%%~sA
)

for /f "tokens=2" %%g in ('echo !_SHORT_JAVA! ^| "!_SHORT_JAVA!\bin\javac" -version 2^>^&1 ^| findstr /i "javac"') do (
    echo Detected version %%g
    for /f "tokens=1-3 delims=._" %%m in ('echo %%g') do (
        set _VERSION=%%m.%%n
    )
    echo Detected version !_VERSION! vs %_CORRECT_JAVA_VERSION%
	if  "!_VERSION!" == %_CORRECT_JAVA_VERSION% (
		SET %1=OK
		goto :EOF
	)
)
SET %1=NO_OK
goto :EOF


:check_oracle_data
IF NOT EXIST %ProgramData%\Oracle\Java\javapath\java.exe (
   echo  Oracle Data Path not found. Unable to use it
   goto  :EOF
)
echo  Setting Oracle Data path : %ProgramData%\Oracle\Java\javapath
SET PATH=%ProgramData%\Oracle\Java\javapath;%PATH%
goto :EOF


:normalise
SET "%2=%~f1"
GOTO :EOF

:set_java_home
call :normalise "%JAVA_HOME%" JAVA_HOME
echo  Setting JAVA_HOME as '%JAVA_HOME%'
SET PATH=%JAVA_HOME%\bin;%PATH%
goto :EOF


:java_ok
ENDLOCAL
exit /b 0

:java_not_found
echo  ******************************************************************
echo  ***FATAL ERROR***
echo  Java %_CORRECT_JAVA_VERSION% is not installed and unable to configure it
echo  Install a java environment and/or set JAVA_HOME
echo  ******************************************************************
exit /b 1


:end_prepare
ENDLOCAL & SET PATH=%PATH%
exit /b 0