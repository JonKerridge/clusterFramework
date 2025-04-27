package parserTests_100

import cluster_cli.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class ParseTests100 extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test1"
    Parser parser = new Parser(inFileName)
    boolean result = parser.parse()
    assertTrue ( result)
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test1a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test2"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test2a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test3"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test4"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test4a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test5"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test5a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test5b"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test5c"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test6"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test7"
    parser = new Parser(inFileName)
    assertTrue (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test8a"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
    inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test8b"
    parser = new Parser(inFileName)
    assertFalse (parser.parse())
  }
}
