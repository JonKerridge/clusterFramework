package parserTests_101

import cluster_cli.parse.Parser

class ParseMany101 {
  static void main(String[] args) {
    String structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test1"
    Parser parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test2"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test3"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test4"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test5"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test6"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test7"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test8"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
  }
}
