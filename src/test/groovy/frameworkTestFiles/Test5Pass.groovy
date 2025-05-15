package frameworkTestFiles

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test5Pass extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_framework/src/test/groovy/frameworkTestFiles/test5"
    Parser parser = new Parser(inFileName)
    assertTrue (parser.parse())
  }
}
