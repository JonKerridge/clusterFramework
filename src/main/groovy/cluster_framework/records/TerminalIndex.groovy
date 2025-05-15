package cluster_framework.records

class TerminalIndex implements Serializable{
  String nodeIP
  int clusterIndex
  int nodeIndex  // the index of the Emitter,Worker, Collector or NodeLoader that has terminated
  long elapsed, processing

//  TerminalIndex(String node, int clusterIndex, int nodeIndex, long elapsed) {
//    this.nodeIP = node
//    this.clusterIndex = clusterIndex
//    this.nodeIndex = nodeIndex
//    this.elapsed = elapsed
//  }
  String toString() {
    String s = "Timing for, $nodeIP, clusterIndex, $clusterIndex, " +
        "nodeIndex, $nodeIndex, Elapsed, $elapsed, Processing, $processing "
    return s
  }

}
