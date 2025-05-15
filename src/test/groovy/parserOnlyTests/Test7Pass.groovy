package parserOnlyTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test7Pass extends GroovyTestCase {

  void test(){
    String inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test7"
    Parser parser = new Parser(inFileName)
    assertTrue (parser.parse())
  }
}
