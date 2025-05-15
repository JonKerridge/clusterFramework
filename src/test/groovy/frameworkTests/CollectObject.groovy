package frameworkTests

import cluster_framework.records.CollectInterface

class CollectObject implements CollectInterface <EmitObject>{

  int sum, count

  CollectObject(){
    this.sum = 0
    this.count = 0
  }

  @Override
  void collate(EmitObject data, List params) {
    println "Collected $data"
    count++
    sum = sum + data.value
  }

  @Override
  void finalise(List params) {
    println "Total sum = $sum from $count data points"
//    assert sum == 210
//    assert count == 20
  }
}
