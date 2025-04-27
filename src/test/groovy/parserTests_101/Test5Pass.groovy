package parserTests_101

import cluster_cli.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test5Pass extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_101/test5"
    Parser parser = new Parser(inFileName)
    assertTrue (parser.parse())
  }
}
