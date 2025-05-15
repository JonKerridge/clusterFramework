package frameworkTestFiles

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase
import org.junit.jupiter.api.Test

class Test1Pass extends GroovyTestCase {
  @Test
  void test(){
    String inFileName = "D:/IJGradle/cluster_framework/src/test/groovy/frameworkTestFiles/test1"
    Parser parser = new Parser(inFileName)
    boolean result = parser.parse()
    assertTrue ( result)
  }
}
