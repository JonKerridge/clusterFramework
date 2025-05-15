package frameworkTests

import cluster_framework.parse.Parser

class ParserRun {
  static void main(String[] args) {
    String structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test1"
    Parser parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
  }
}
