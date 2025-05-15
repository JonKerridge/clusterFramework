package cluster_framework.processes

import cluster_framework.records.NodeIndex
import cluster_framework.records.TerminalIndex
import groovy_jcsp.ALT
import groovy_jcsp.ChannelOutputList
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput

class Manager implements CSProcess{

  ChannelInput readyToSend, readyToRead
  ChannelOutputList sendTo
  int nWriteNodes, nReadNodes, nReadInternals, clusterIndex
//  List <ParseRecord> structure
//  ChannelOutput toHost
//  ChannelInput fromHost

  @Override
  void run() {
    //println "Manager $clusterIndex running"
    int queueSize = nReadInternals * nReadNodes
    Integer[] indexBuffer = new Integer[queueSize]
    int entries, writeTo, readFrom, terminated
    boolean running
    // added in v1.0.2 initialise the indexBuffer with alternating read node index values
    int indexEntry = 0
    for ( i in 0 ..< nReadInternals)
      for ( n in 0 ..< nReadNodes){
        indexBuffer[indexEntry] = n
        indexEntry = indexEntry + 1
      }
//    println "Manager $clusterIndex: $indexBuffer, $entries"
    (entries, writeTo, readFrom, terminated, running) =
        [queueSize, 0, 0, 0, true]


    boolean [] managerPreCon = [entries > 0, entries < queueSize]
    ALT managerAlt = new ALT([readyToSend, readyToRead])
    while (running) {
//      println "Manager $clusterIndex: $managerPreCon, $entries, $indexBuffer"
      switch (managerAlt.priSelect(managerPreCon)){
        case 0: // a write buffer is ready to send object and queue has entries
          def object = readyToSend.read()
          if (!(object instanceof TerminalIndex)){ // a write node has NOT terminated
            // it needs to be sent a reading node index from indexBuffer
            sendTo[(object as NodeIndex).indexValue].write(new NodeIndex (indexValue: indexBuffer[readFrom]))
            readFrom = (readFrom + 1) % queueSize
            entries = entries - 1
          }
          else{
            terminated = terminated + 1
            if (terminated == nWriteNodes) running = false
//            println "Manager $clusterIndex : termination $terminated, $running"
          }
          break
        case 1: // a read buffer is ready to read an object and indexBuffer is not full
          def object = readyToRead.read()
            // save the reading node's index in the queue
            int readingNodeIndex = (object as NodeIndex).indexValue
            indexBuffer[writeTo] = readingNodeIndex as Integer
            writeTo = (writeTo + 1) % queueSize
            entries = entries + 1
          break
      } // switch
//      println "Manager $indexBuffer"
      managerPreCon = [entries > 0, entries < queueSize]
    } // running
    // manager can now terminate
    //println "Manager $clusterIndex  terminated"
  } // run()

}
