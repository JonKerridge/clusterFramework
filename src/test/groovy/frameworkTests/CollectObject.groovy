package frameworkTests

import cluster_framework.records.CollectInterface

class CollectObject implements CollectInterface <EmitObject>{

  int sum, count
  PrintWriter printWriter

  CollectObject(List p){
    String fileName = p[0]
    String path = "./data/${fileName}.txt"
    printWriter = new PrintWriter(path)
    this.sum = 0
    this.count = 0
  }

  @Override
  void collate(EmitObject data, List params) {
    println "Collected $data"
    count++
    sum = sum + data.value
    printWriter.println "$data"
  }

  @Override
  void finalise(List params) {
    println "Total sum = $sum from $count data points"
    printWriter.flush()
    printWriter.close()
//    assert sum == 210
//    assert count == 20
  }
}
