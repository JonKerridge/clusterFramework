package frameworkTests

import org.junit.Test

class ResultCheck {
  @Test
  public void test(){
    String testName = "1"
    int nodes = 1
    int collectors = 1
    int minValue, maxValue
    minValue = 501
    maxValue = 600
    List <String> fileNames = []
    for ( n in 0 ..< nodes)
      for ( c in 0 ..< collectors)
        fileNames << "./Test${testName}Results-${n}-${c}.cliCout"
    println "Files are $fileNames"
    fileNames.each { String name ->
      println "$name"
      List retrieved = []
      File objFile = new File(name)
      objFile.withObjectInputStream { inStream ->
        inStream.eachObject {
          assert ((it.value >= minValue) && (it.value <= maxValue)): "Value ${it.value} out of range"
//          println "${it.value}"
          retrieved << it.value
        }
        inStream.close()
      } // inStream
      println "$retrieved"
    } // filenames
  } // test
}
