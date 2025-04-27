package cluster_cli.processes

import cluster_cli.records.ExtractParameters
import cluster_cli.records.TerminalIndex
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Worker implements CSProcess{

  ChannelOutput toReadBuffer
  ChannelInput fromReadBuffer
  ChannelOutput toWriteBuffer
  int workerIndex, nodeIndex, clusterIndex
  List updateParam
  String methodName

  @Override
  void run() {
    boolean running
//    println "Worker [$clusterIndex, $nodeIndex, $workerIndex] running using $updateParam"
    List parameterValues = ExtractParameters.extractParams(updateParam[0] as List, updateParam[1] as List)
//    println "Worker [$clusterIndex, $nodeIndex, $workerIndex] running using parameters: $parameterValues"
    running = true
    while (running){
      toReadBuffer.write(workerIndex)
//      println "Worker [$clusterIndex, $nodeIndex, $workerIndex] sent request"
      def object = fromReadBuffer.read()
//      println "Worker [$clusterIndex, $nodeIndex, $workerIndex] received response $object"
      if (object instanceof TerminalIndex)
        running = false
      else {
        object.&"$methodName"(parameterValues)
//        println "Worker [$clusterIndex, $nodeIndex, $workerIndex] done update $object"
        toWriteBuffer.write(object)
//        println "Worker [$clusterIndex, $nodeIndex, $workerIndex] sent output $object to WB"
      }
    } // running
//    println "Worker [$clusterIndex, $nodeIndex, $workerIndex] has stopped running"
    toWriteBuffer.write(new TerminalIndex(
        nodeIP: "worker",
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex
      )
    )
    //println "Worker [$clusterIndex, $nodeIndex, $workerIndex] terminated"

  }
}
