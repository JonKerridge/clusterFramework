package parserOnlyTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test1Pass extends GroovyTestCase {

  void test(){
    String inFileName = "D:/IJGradle/clusterFramework/src/test/groovy/parserOnlyTests/test1"
    Parser parser = new Parser(inFileName)
    boolean result = parser.parse()
    assertTrue ( result)
  }
}
