package parserOnlyTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase

class ParseTests100 extends GroovyTestCase {

  void test(){
    String inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test1"
    Parser parser = new Parser(inFileName)
    boolean result = parser.parse()
    assertTrue ( result)
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test1a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test2"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test2a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test3"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test4"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test4a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test5"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test5a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test5b"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test5c"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test6"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test7"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test8a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test8b"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
  }
}
