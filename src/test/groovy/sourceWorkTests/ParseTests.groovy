package sourceWorkTests

import cluster_framework.parse.Parser
import groovy.test.GroovyTestCase

class ParseTests extends GroovyTestCase{
  void test(){
    String parseFile = "./src/test/groovy/sourceWorkTests/TestA"
    Parser parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/TestAFail"
    parser = new Parser(parseFile)
    assertFalse(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/TestB"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/TestBFail"
    parser = new Parser(parseFile)
    assertFalse(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/LTest111"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/LTest121"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/LTest141"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/Test181"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/Test211"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/Test241"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/Test1111"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
    parseFile = "./src/test/groovy/sourceWorkTests/Test11111"
    parser = new Parser(parseFile)
    assertTrue(parser.parse())
  }
}
