@echo off
REM Compiles and runs Currency Converter Pro without NetBeans.
if not exist build\classes mkdir build\classes
javac -encoding UTF-8 -cp lib\json-20240303.jar -d build\classes src\currencyconverter\*.java src\currencyconverter\ui\*.java
xcopy /E /I /Y src\currencyconverter\resources build\classes\currencyconverter\resources >nul
java -Dfile.encoding=UTF-8 -cp "build\classes;lib\json-20240303.jar" currencyconverter.CurrencyConvert
