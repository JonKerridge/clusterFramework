package parserTests_101

import cluster_cli.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test2Pass extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_101/test2"
    Parser parser = new Parser(inFileName)
    boolean result = parser.parse()
    assertTrue ( result)
  }
}
