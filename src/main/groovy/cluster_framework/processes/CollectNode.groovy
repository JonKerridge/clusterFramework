package cluster_framework.processes


import cluster_framework.records.ParseRecord
import cluster_framework.records.TerminalIndex
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.net2.NetAltingChannelInput
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class CollectNode implements CSProcess{
  String nodeIP
  NetAltingChannelInput fromPreviousNodes
  NetChannelOutput readyToRead
  int nodeIndex, clusterIndex, nInternals, nPrevious
  Class <?> collectClass
  List <ParseRecord> structure
  NetChannelInput fromHost
  NetChannelOutput toHost
  long nodeLoadTime

  @Override
  void run() {
    //println "Collect Node starting: $clusterIndex, $nodeIndex, $nInternals"
    long startTime, endTime
    def requestChannel = Channel.any2one()
    def objectChannels = Channel.one2oneArray(nInternals)
    def objectChannelOutList = new ChannelOutputList(objectChannels)
    List <CSProcess> network = []
    for (i in 0 ..< nInternals) {
      List <List <String>> collectParameters
      List <List <String>> finaliseParameters
      collectParameters = [
          structure[clusterIndex].collectParameters[0],
//up to 1.0.4          structure[clusterIndex].collectParameters[1]
          structure[clusterIndex].collectParameters[((nodeIndex * nInternals) + i) + 1] // from 1.0.5
      ]

      finaliseParameters = [
          structure[clusterIndex].finaliseParameters[0],
//up to 1.0.4          structure[clusterIndex].finaliseParameters[1]
          structure[clusterIndex].finaliseParameters[((nodeIndex * nInternals) + i) + 1] // from 1.0.5
      ]
      network << new Collector(
          requestObject: requestChannel.out(),
          inputObject: objectChannels[i].in(),
          nodeIndex: nodeIndex,
          clusterIndex: clusterIndex,
          internalIndex: i,
          collectClass: collectClass,
          collectParameters: collectParameters,
          finaliseParameters: finaliseParameters,
          outFileName: structure[clusterIndex].outFileName
          )
    } // for nInternals

    network << new ReadBuffer(
        fromInternalProcesses: requestChannel.in(),
        toInternalProcesses: objectChannelOutList,
        nInternals: nInternals,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex,
        readyToRead: readyToRead,
        objectInput: fromPreviousNodes,
        nPrevious: nPrevious
    )
    startTime = System.currentTimeMillis()
    new PAR(network).run()
    endTime = System.currentTimeMillis()
//    println "Collect Node [$clusterIndex, $nodeIndex]: Elapsed time = ${(endTime - startTime)} waiting for host"
    toHost.write(new TerminalIndex(
        nodeIP: nodeIP,
        clusterIndex: clusterIndex,
        nodeIndex: nodeIndex,
        elapsed: (endTime - nodeLoadTime),
        processing: (endTime - startTime)
    ))
  }
}
