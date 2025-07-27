package frameworkTestFiles

import cluster_framework.parse.Parser

class ParseAllTests {
  static void main(String[] args) {
    String user_dir = System.getProperty("user.dir")
    String testing_dir = user_dir + "\\src\\test\\groovy\\frameworkTestFiles\\"
    List<String> testFiles = ['test1', 'test2', 'test3', 'test4', 'test5', 'test6', 'test7', 'test8']

    testFiles.each { testFile ->
      String structureSourceFile = testing_dir + testFile
      Parser parser = new Parser(structureSourceFile)
      assert parser.parse() :"Parsing failed"

    }
  }
}
