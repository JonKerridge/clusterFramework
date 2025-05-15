package frameworkTestFiles

import cluster_framework.parse.Parser

class ParseMany101 {
  static void main(String[] args) {
    String structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test1"
    Parser parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test2"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test3"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test4"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test5"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test6"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test7"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
    structureSourceFile = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test8"
    parser = new Parser(structureSourceFile)
    assert parser.parse() :"Parsing failed"
  }
}
