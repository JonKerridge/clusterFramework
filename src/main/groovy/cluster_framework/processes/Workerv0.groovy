package cluster_framework.processes

import cluster_framework.records.ExtractParameters
import cluster_framework.records.TerminalIndex
import cluster_framework.records.WorkDataInterface
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Workerv0 implements CSProcess{

  ChannelOutput toReadBuffer
  ChannelInput fromReadBuffer
  ChannelOutput toWriteBuffer
  int workerIndex, nodeIndex, clusterIndex
  List workParam
  String methodName
  Object sharedData

  @Override
  void run() {
    boolean running
//    println "Worker [$clusterIndex, $nodeIndex, $workerIndex] running using $workParam"
    List parameterValues = ExtractParameters.extractParams(workParam[0] as List, workParam[1] as List)
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
        object.&"$methodName"(sharedData as WorkDataInterface, parameterValues)
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
