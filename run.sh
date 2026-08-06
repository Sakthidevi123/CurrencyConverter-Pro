#!/usr/bin/env bash
# Compiles and runs Currency Converter Pro without NetBeans.
set -e
mkdir -p build/classes
javac -encoding UTF-8 -cp lib/json-20240303.jar -d build/classes $(find src -name '*.java')
cp -r src/currencyconverter/resources build/classes/currencyconverter/
java -Dfile.encoding=UTF-8 -cp "build/classes:lib/json-20240303.jar" currencyconverter.CurrencyConvert
