package cluster_framework.processes

import cluster_framework.records.ParseRecord
import cluster_framework.records.TerminalIndex
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class EmitNode implements CSProcess{
  String nodeIP
  int clusterIndex, nodeIndex, nInternals
  ChannelOutputList toNextNode
  NetChannelOutput readyToSend
  NetChannelInput useIndex
  Class objectClass, sourceClass
  List <ParseRecord> structure
  NetChannelInput fromHost
  NetChannelOutput toHost
  long nodeLoadTime

  @Override
  void run() {
    //println "Emit Node starting: $clusterIndex, $nodeIndex, $nInternals"
    long startTime, endTime
    def internalsToWriteBuffer = Channel.any2one()
    List <CSProcess> network = []
    WriteBuffer writeBuffer = new WriteBuffer(
        fromInternals: internalsToWriteBuffer.in(),
        outputToNodes: toNextNode,
        readyToSend: readyToSend,
        useNode: useIndex,
        nInternals: nInternals,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex
    )

    for ( i in 0 ..< nInternals) {
      List<List<String>> emitParameters
      emitParameters = [structure[clusterIndex].emitParameters[0],
                        structure[clusterIndex].emitParameters[((nodeIndex * nInternals) + i) + 1]
                       ]
      String sourceDataFileName
//      println "EmitNode: ${structure[clusterIndex].sourceDataFileNames}"
      if (structure[clusterIndex].sourceDataFileNames == null)
        sourceDataFileName = null
      else
        sourceDataFileName = structure[clusterIndex].sourceDataFileNames[(nodeIndex * nInternals) + i]
//println "EmitNode: actual $sourceDataFileName, $nodeIndex, $nInternals"
      network << new Emitter(
          internalIndex: i,
          output: internalsToWriteBuffer.out(),
          clusterIndex: clusterIndex,
          nodeIndex: nodeIndex,
          classDef: objectClass,
          emitParams: emitParameters,
          sourceDef: sourceClass,
          sourceDataFileName: sourceDataFileName)
    }
    network << writeBuffer
    startTime = System.currentTimeMillis()
    new PAR(network).run()
    endTime = System.currentTimeMillis()
    toHost.write(new TerminalIndex(
        nodeIP: nodeIP,
        clusterIndex: clusterIndex,
        nodeIndex: nodeIndex,
        elapsed: (endTime - nodeLoadTime),
        processing:  (endTime - startTime)
    ))
//    println "Emit Node [$clusterIndex, $nodeIndex] elapsed time = ${(endTime - startTime)} waiting for host ..."
//    println "Emit Node [$clusterIndex, $nodeIndex] has terminated"
  } // run()
}
