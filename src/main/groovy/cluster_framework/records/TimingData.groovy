package cluster_framework.records

class TimingData implements  Serializable{
  String nodeIP, nodeType, methodName
  int clusterIndex, nodeIndex
  Long nodeElapsed
  Map <Integer, Long> processingTimes

  String toString(){
    List <Integer> sortedTimesKeys
    String s
    s = "Cluster: $clusterIndex, $nodeIP, $nodeIndex, $nodeType, $methodName, $nodeElapsed, ,"
    sortedTimesKeys = processingTimes.sort()*.key
    sortedTimesKeys.each {Integer k->
      s = s + "$k, ${processingTimes.get(k)}, "
    }
    return s
  }

}
