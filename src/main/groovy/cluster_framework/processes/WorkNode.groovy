package cluster_framework.processes

import cluster_framework.records.ParseRecord
import cluster_framework.records.TerminalIndex
import cluster_framework.records.WorkDataInterface
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

  // local work data
  Class localDataClass

  @Override
  void run() {
    long startTime, endTime
    String workDataFileName
    workDataFileName = structure[clusterIndex].workDataFileName
    WorkDataInterface workData
    workData = null
    if (workDataFileName != null){
      // create an instance of the localData to be shared read only with nodes
      assert (localDataClass != null):"Work Data file $workDataFileName specified but no WorkDataInterface type object specified"
      // the declared constructor has a single String containing filename as parameter
      Class[] cArg = new Class[1]
      cArg[0] = String.class
      workData = localDataClass.getDeclaredConstructor(cArg).newInstance(workDataFileName)

    }

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

    // all Worker processes use the same parameters
    List<List<String>> workParameters
    workParameters = [structure[clusterIndex].workParameters[0],
                      structure[clusterIndex].workParameters[1]]

    for ( i in 0 ..< nInternals){
      network << new Worker(
          toReadBuffer: requestChannel.out(),
          fromReadBuffer: objectChannels[i].in(),
          toWriteBuffer: internalsToWriteBuffer.out(),
          methodName: methodName,
          workParam: workParameters,
          nodeIndex: nodeIndex,
          clusterIndex: clusterIndex,
          workerIndex: i,
//          localDataClass: localDataClass,
//          workDataFileName: workDataFileName
          localData: workData
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
