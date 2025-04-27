package cluster_cli.processes

import cluster_cli.records.ParseRecord
import cluster_cli.records.TerminalIndex
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.net2.NetAltingChannelInput
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class WorkNode implements CSProcess{

  // WriteBuffer connections
  ChannelOutputList outputToNodes
  NetChannelOutput readyToSend
  NetChannelInput useNode

  //ReadBuffer connections
  NetAltingChannelInput fromPreviousNodes
  NetChannelOutput readyToRead

  // host connections
  NetChannelInput fromHost
  NetChannelOutput toHost

  // constants
  int nInternals, nodeIndex, clusterIndex, nPrevious
  String nodeIP
  long nodeLoadTime

  // structure for parameters and work method of this nodeLoader
  List <ParseRecord> structure
  String methodName

  // shared data


  @Override
  void run() {
    long startTime, endTime
    // ReadBuffer internal channels
    def requestChannel = Channel.any2one()
    def objectChannels = Channel.one2oneArray(nInternals)
    def objectChannelOutList = new ChannelOutputList(objectChannels)

    //WriteBuffer internal channels
    def internalsToWriteBuffer = Channel.any2one()

    List <CSProcess> network = []
    WriteBuffer writeBuffer = new WriteBuffer(
        fromInternals: internalsToWriteBuffer.in(),
        outputToNodes: outputToNodes,
        readyToSend: readyToSend,
        useNode: useNode,
        nInternals: nInternals,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex
    )
    ReadBuffer readBuffer = new ReadBuffer(
        fromInternalProcesses: requestChannel.in(),
        toInternalProcesses: objectChannelOutList,
        nInternals: nInternals,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex,
        readyToRead: readyToRead,
        objectInput: fromPreviousNodes,
        nPrevious: nPrevious
    )

    for ( i in 0 ..< nInternals){
      List<List<String>> updateParameters
      updateParameters = [structure[clusterIndex].workParameters[0],
                          structure[clusterIndex].workParameters[1]]
//      structure[clusterIndex].workParameters[((nodeIndex * nInternals) + i) + 1]]
      network << new Worker(
          toReadBuffer: requestChannel.out(),
          fromReadBuffer: objectChannels[i].in(),
          toWriteBuffer: internalsToWriteBuffer.out(),
          methodName: methodName,
          updateParam: updateParameters,
          nodeIndex: nodeIndex,
          clusterIndex: clusterIndex,
          workerIndex: i
      )
    }

    network << writeBuffer
    network << readBuffer
    startTime = System.currentTimeMillis()
    new PAR(network).run()
    endTime = System.currentTimeMillis()
    toHost.write(new TerminalIndex(
        nodeIP: nodeIP,
        clusterIndex: clusterIndex,
        nodeIndex: nodeIndex,
        elapsed: (endTime - nodeLoadTime),
        processing: (endTime - startTime))
    )
  } //run
}
