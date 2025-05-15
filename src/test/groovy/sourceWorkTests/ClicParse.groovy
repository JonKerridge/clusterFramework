package sourceWorkTests

import cluster_framework.parse.Parser

String inFileName = "./TestB"
Parser parser = new Parser(inFileName)
boolean result = parser.parse()
if (result) println "parsing OK"
else println "parsing FAILED!"
