package cluster_framework.processes

import cluster_framework.records.EmitInterface
import cluster_framework.records.NodeIndex
import cluster_framework.records.TerminalIndex
import groovy_jcsp.ALT
import groovy_jcsp.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.net2.NetAltingChannelInput
import jcsp.net2.NetChannelOutput

class ReadBuffer implements CSProcess{

  //external channels
  NetChannelOutput readyToRead
  NetAltingChannelInput objectInput
  //internal channels
  ChannelOutputList toInternalProcesses
  ChannelInput fromInternalProcesses  //a shared channel input
  int clusterIndex, nodeIndex, nInternals, nPrevious

  @Override
  void run() {
    NodeIndex nodeValue = new NodeIndex(indexValue:  nodeIndex)
//    println "Read Buffer [$clusterIndex, $nodeIndex] starting $nInternals  internal processes"
    List buffer = new List[nInternals]
    boolean running
    int readFrom, writeTo, entries, previousTerminations
    ALT readAlt = new ALT([objectInput, fromInternalProcesses])
    (readFrom, writeTo, entries, previousTerminations) = [0,0,0,0]
    running = true
    boolean [] preCon
    preCon = [true, false]
    // removed initialisation in v1.0.2 to manager
//    // tell manager that the read buffer can accept nInternals objects
//    for ( i in 1 .. nInternals) {
//      readyToRead.write(nodeValue)
//    }
    while (running){
//      println "RB [$clusterIndex, $nodeIndex]: $preCon $entries"
      switch (readAlt.priSelect(preCon)){
        case 0: // object input from previousNodes
          def object = objectInput.read()
          if (!(object instanceof TerminalIndex)) {
//            println "RB has read $object"
            buffer[writeTo] = object as EmitInterface
//            println "RB [$clusterIndex, $nodeIndex]: read ${buffer[writeTo]} in $writeTo"
            writeTo = (writeTo + 1) % nInternals
            entries = entries + 1
          } else {
            // one of the previous nodes has sent a terminal message
            previousTerminations = previousTerminations + 1
            if (previousTerminations == nPrevious) running = false
//            println "RB [$clusterIndex, $nodeIndex]: terminating $running"
          }
          break
        case  1:  //request from one of the internal processes
          int internalIndex = fromInternalProcesses.read() as int
          toInternalProcesses[internalIndex].write(buffer[readFrom])
//          println "RB [$clusterIndex, $nodeIndex]: written ${buffer[readFrom]} in $readFrom"
          readFrom = (readFrom + 1) % nInternals
          entries = entries - 1
          // must accept requests from internal processes while terminating as there
          // could be valid data in buffer still to be processes
          readyToRead.write(nodeValue)
          break
      } // switch
      preCon = [(entries < nInternals), entries > 0]
    } // running
//    println "Read Buffer [$clusterIndex, $nodeIndex]: termination phase"
    // all nodes in previous cluster have terminated but could still be entries in local buffer
    while (entries > 0) {
      int internalIndex = fromInternalProcesses.read() as int
      toInternalProcesses[internalIndex].write(buffer[readFrom])
//          println "RB [$clusterIndex, $nodeIndex]: written ${buffer[readFrom]} in $readFrom"
      readFrom = (readFrom + 1) % nInternals
      entries = entries - 1
    }
//    println "Read Buffer [$clusterIndex, $nodeIndex]: emptied local buffer"
    // local buffer is now empty so terminate the internal processes
    for ( i in 0 ..< nInternals){
      int internalIndex = fromInternalProcesses.read() as int
//      println "Read Buffer [$clusterIndex, $nodeIndex]: terminating internal $internalIndex"
      toInternalProcesses[internalIndex].write(new TerminalIndex())
    }
//    println "Read Buffer [$clusterIndex, $nodeIndex]: ending manager"
//    readyToRead.write(new TerminalIndex())
//    println "Read Buffer [$clusterIndex, $nodeIndex]: terminated"
  } // run()
}
