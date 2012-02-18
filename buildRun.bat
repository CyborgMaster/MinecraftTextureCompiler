javac -cp snakeyaml-1.10.jar MinecraftTerrainCompiler.java || goto :EOF
java -cp "snakeyaml-1.10.jar;." MinecraftTerrainCompiler %*

:EOF