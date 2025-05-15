package parserOnlyTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test2a extends GroovyTestCase{

  void test(){
    String inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test2a"
    Parser parser = new Parser(inFileName)
    assertFalse (parser.parse())
  }
}
