package parserOnlyTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test5c extends GroovyTestCase{

  void test(){
    String inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test5c"
    Parser parser = new Parser(inFileName)
    assertFalse (parser.parse())
  }
}
