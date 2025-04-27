package parserTests_100

import cluster_cli.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test6Pass extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_cli/src/test/groovy/parserTests_100/test6"
    Parser parser = new Parser(inFileName)
    assertTrue (parser.parse())
  }
}
