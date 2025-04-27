package parserTests_100

import cluster_cli.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test1a extends GroovyTestCase{
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test1a"
    Parser parser = new Parser(inFileName)
    assertFalse (parser.parse())
  }
}
