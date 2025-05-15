package cluster_framework.processes

import cluster_framework.records.EmitInterface
import cluster_framework.records.NodeIndex
import cluster_framework.records.TerminalIndex
import groovy_jcsp.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class WriteBuffer implements CSProcess {
  ChannelOutputList outputToNodes
  NetChannelOutput readyToSend
  NetChannelInput useNode
  ChannelInput fromInternals
  int nInternals, nodeIndex, clusterIndex

  @Override
  void run() {
    NodeIndex nodeValue = new NodeIndex(indexValue:  nodeIndex)
    int terminated
    boolean running
    (terminated, running) = [0, true]
    while (running) {
      def object = fromInternals.read()
//      println "WB-[$clusterIndex, $nodeIndex]: has read $object"
      if (object instanceof TerminalIndex) {
        terminated = terminated + 1
        if (terminated == nInternals) running = false
//            println "WB-[$clusterIndex, $nodeIndex]: terminating $terminated and $running"
      } else {
        readyToSend.write(nodeValue)
//        println "WB-[$clusterIndex, $nodeIndex]: ready to send written"
        int nodeId = (useNode.read() as NodeIndex).indexValue
//        println "WB-[$clusterIndex, $nodeIndex]: is writing to ${(outputToNodes[nodeId]as NetChannelOutput).getLocation()}"
        outputToNodes[nodeId].write(object as EmitInterface)
//        println "WB-[$clusterIndex, $nodeIndex]: has written $object to $nodeId "
      }

    } // running
//    println "WB-[$clusterIndex, $nodeIndex]: has stopped running "
    TerminalIndex terminalIndex = new TerminalIndex(
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex,
        nodeIP: "writeBuffer")
    // send terminalIndex to each Reading node (work or collect)
    for ( i in 0 ..< outputToNodes.size())
      outputToNodes[i].write(terminalIndex)
    // send terminalIndex to its manager
    readyToSend.write(terminalIndex)
//    println "WB-[$clusterIndex, $nodeIndex]: terminated"
  } // run
}