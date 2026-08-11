@echo off
rem Desktop launcher for XWizard / VeryFastPDF.
rem NOTE: the app is compiled to Java 25 bytecode, so it needs a Java 25+ runtime.
rem       The default 'java' on the system PATH here is Java 8 and will NOT work
rem       (it fails with UnsupportedClassVersionError).
rem Prerequisite: build once with Maven so target\XWizard\WEB-INF\ exists:
rem     mvn package

setlocal
rem Java 25 runtime. Defaults to the bundled JetBrains Runtime (Freeplane).
rem Override by setting JAVA_HOME to any JDK 25+ before running this script.
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Freeplane\runtime"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if not exist "%JAVA_EXE%" (
    echo ERROR: Java not found at "%JAVA_EXE%".
    echo Set JAVA_HOME to a JDK 25+ installation and retry.
    exit /b 1
)

"%JAVA_EXE%" -Xss10M -cp "target\XWizard\WEB-INF\classes;target\XWizard\WEB-INF\lib\*" veryFastPDF.VFPStarter %*
endlocal