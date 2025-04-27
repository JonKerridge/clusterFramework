package cluster_cli

import cluster_cli.parse.Parser

class ParserRun {
  static void main(String[] args) {
    String structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test1"
    Parser parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
  }
}
