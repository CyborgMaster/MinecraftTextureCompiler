@echo off

javac -d build -cp lib/snakeyaml-1.10.jar src/MinecraftTerrainCompiler.java || GOTO :EOF
jar cfm terrainCompiler.jar src/manifest.txt -C build .

:EOF