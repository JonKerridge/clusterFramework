package sourceWorkFileOps

import locality.AreaData

class PrintOutFile {
  static void main(String[] args) {
    String readFileString = "./data/Test7Results-0-0.out"
    File objFile = new File(readFileString)
    objFile.withObjectInputStream { inStream ->
      inStream.eachObject {AreaData ad ->
        println "$ad"
      }
    }
  }
}
