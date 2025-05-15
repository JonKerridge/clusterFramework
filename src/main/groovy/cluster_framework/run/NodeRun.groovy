package cluster_framework.run

import cluster_framework.processes.CollectNode
import cluster_framework.processes.EmitNode
import cluster_framework.processes.WorkNode
import cluster_framework.records.*
import groovy_jcsp.ChannelOutputList
import groovy_jcsp.PAR
import jcsp.lang.CSProcess
import jcsp.lang.CSTimer
import jcsp.net2.*
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

/**
 * A class used at nodes other than the host node to load an application over
 * a local loop-back or real workstation network.
 *
 * In the case of a real network the only parameter value required by the class
 * is the IP address of the host node.
 *
 * The host node must be invoked before any of the other nodes.
 * The class has a single method called invoke()
 */
class NodeRun {

  String hostIP, localIP
  String version = VersionControl.versionTag
  int portNumber = 56789  //port number used in TCPIP communications

/**
 * Invoke a node
 * @param hostIP the IP address of the host
 */
  NodeRun(String hostIP){
    this.hostIP = hostIP
    this.localIP = null
  }
/**
 * Invoke a node in Local mode
 * @param hostIP the host IP address, usually 127.0.0.1
 * @param localIP the Ip address of the node to be created
 */
  NodeRun(String hostIP, String localIP){
    this.hostIP = hostIP
    this.localIP = localIP
  }
/**
 * invoke() is the only method in the class NodeRun.
 * It is used to run the application loading code at a node by communicating
 * with the host node to obtain the required information and application code.
 * With no parameters defaults to minimal output during application loading phase.
 */
  void invoke(){
    invoke(false)
  }
  /**
   *
   * @param verbose if true prints details of the application loading phase
   */
  void invoke(boolean verbose){
    // added in V1.0.8 to ensure nodes wait 3 seconds until host has started
    CSTimer timer = new CSTimer()
    timer.sleep(3000)
    long startTime
    startTime = System.currentTimeMillis()
    List<ParseRecord> structure 

//Phase 1 send nodeLoader IP to host, and get classes and app structure

// create this nodeLoader and make connections to and from host
    TCPIPNodeAddress nodeAddress
    if (localIP == null)
      nodeAddress = new TCPIPNodeAddress(portNumber) // most global IP address
    else
      nodeAddress = new TCPIPNodeAddress(localIP,portNumber)
    Node.getInstance().init(nodeAddress)
    String nodeIP = nodeAddress.getIpAddress()
    println "Node $nodeIP has started with host $hostIP on port $portNumber using version $version"
    def fromHost = NetChannel.numberedNet2One(1, new CodeLoadingChannelFilter.FilterRX())
    def hostAddress = new TCPIPNodeAddress(hostIP, portNumber)
    def toHost = NetChannel.any2net(hostAddress, 1)
    if (verbose) println "Node $nodeIP has toHost channel ${toHost.getLocation()}" +
                          "\n fromHost channel ${fromHost.getLocation()}"
    toHost.write(nodeIP)
// now get class definitions from host
    ClassDefinitions classDefinitions = (fromHost.read() as ClassDefinitions)
    assert classDefinitions.version == version: "Version mismatch: Parser / Host = ${classDefinitions.version}, Node = $version"
// nodeLoader can now read in the structure object unless some preAllocated nodes have not been started
    Object dataFromHost = fromHost.read()
    if (dataFromHost instanceof TerminalIndex) System.exit(-2)  // instant termination
// termination occurs if required IP addresses have not been assigned to nodes
    structure = (dataFromHost as List<ParseRecord>)
    if (verbose) structure.each {println "$it"}
    Acknowledgement ack
    ack = new Acknowledgement(1, nodeIP)
//    println "sending $ack"
    toHost.write(ack)
    if (verbose) println "Node $nodeIP has completed phase 1"
    ack = fromHost.read() as Acknowledgement
    assert ack.ackValue == 2: "Expected phase 2 start but got ${ack.ackValue}"
// can now start creating net input channels for this node
// first determine to which cluster this node contributes
    int clusterIndex, structureMax
    structureMax = structure.size() - 1
    clusterIndex = 0
    while (!(structure[clusterIndex].allocatedNodeIPs.contains(nodeIP))) clusterIndex++
    if (verbose) println "Node processing cluster $clusterIndex"
// can assume node is present in one cluster; clusterIndex holds index of cluster
// pre-declare the input channels
    NetChannelInput useIndex
    NetAltingChannelInput fromPreviousNode
    useIndex = null
    fromPreviousNode = null
    switch (clusterIndex){
      case 0: // an emit node - write buffer only
        useIndex = NetChannel.numberedNet2One(20)
        if (verbose) println "useIndex: ${useIndex.getLocation()}"
        break
      case structureMax: // a collect node - read buffer only
        fromPreviousNode = NetChannel.numberedNet2One(21, new CodeLoadingChannelFilter.FilterRX())
        if (verbose) println "fromPreviousNode: ${fromPreviousNode.getLocation()}"
        break
      default: // a work node - both buffers
        useIndex = NetChannel.numberedNet2One(20)
        fromPreviousNode = NetChannel.numberedNet2One(21, new CodeLoadingChannelFilter.FilterRX())
        if (verbose) println "useIndex: ${useIndex.getLocation()}" +
                              "\nfromPreviousNode: ${fromPreviousNode.getLocation()}"
        break
    }
// tell host all the node input channels have been created end of phase 2
    ack = new Acknowledgement(2, nodeIP)
    toHost.write(ack)
    if (verbose) println "Node $nodeIP has completed phase 2"
    ack = fromHost.read() as Acknowledgement
    assert ack.ackValue == 3: "Expected phase 3 start but got ${ack.ackValue}"
// can now create the net output channels
    NetChannelOutput readyToSend, readyToRead
    readyToSend = null
    readyToRead = null
    ChannelOutputList toNextNodes = new ChannelOutputList()
    switch (clusterIndex){
      case 0:  // an emit node - write buffer only
        int sendIndex = 100
        readyToSend = NetChannel.any2net(hostAddress, sendIndex)
        for ( i in 0 ..< structure[1].allocatedNodeIPs.size()){
          def nextNodeAddress = new TCPIPNodeAddress(structure[1].allocatedNodeIPs[i], portNumber)
          NetChannelOutput toNext = NetChannel.one2net(nextNodeAddress, 21, new CodeLoadingChannelFilter.FilterTX())
          toNextNodes.append(toNext)
        }
        if (verbose) println "Emit readyToSend: ${readyToSend.getLocation()}"
        if (verbose) for ( i in 0 ..< toNextNodes.size())println "Emit toNext $i: ${(toNextNodes[i] as NetChannelOutput).getLocation()}"
        break
      case structureMax: // a collect node - read buffer only
        int managerIndex = clusterIndex - 1
        int readIndex = (managerIndex * 2) + 101
        readyToRead = NetChannel.any2net(hostAddress, readIndex)
        if (verbose) println "Collect readyToRead: ${readyToRead.getLocation()}"
        break
      default:  // a work node - both buffers
        int sendIndex = (clusterIndex * 2) +100
        readyToSend = NetChannel.any2net(hostAddress, sendIndex)
        int managerIndex = clusterIndex - 1
        int readIndex = (managerIndex * 2) + 101
        readyToRead = NetChannel.any2net(hostAddress, readIndex)
        for ( i in 0 ..< structure[clusterIndex+1].allocatedNodeIPs.size()){
          def nextNodeAddress = new TCPIPNodeAddress(structure[clusterIndex+1].allocatedNodeIPs[i], portNumber)
          NetChannelOutput toNext = NetChannel.one2net(nextNodeAddress, 21, new CodeLoadingChannelFilter.FilterTX())
          toNextNodes.append(toNext)
        }
        if (verbose) println "Work readyToSend: ${readyToSend.getLocation()}"
        if (verbose) println "Work readyToRead: ${readyToRead.getLocation()}"
        if (verbose) for ( i in 0 ..< toNextNodes.size())println "Work toNext $i: ${(toNextNodes[i] as NetChannelOutput).getLocation()}"
        break
    }
    if (verbose) println "Node $nodeIP has completed phase 3"
    ack = new Acknowledgement(3, nodeIP)
    toHost.write(ack)

    ack = fromHost.read() as Acknowledgement
    assert ack.ackValue == 4: "Expected phase 4 start but got ${ack.ackValue}"
    if (verbose) println "Node $nodeIP is invoking node process"
// now start phase 4 where the correct Node Process is created
    CSProcess process
    switch (clusterIndex){
      case 0: //emit node
        assert structure[clusterIndex].typeName == "emit":
            "Node $nodeIP in $clusterIndex: emit expected, got ${structure[clusterIndex].typeName}"
        int nodeIndex = 0
        while( structure[clusterIndex].allocatedNodeIPs[nodeIndex] != nodeIP) nodeIndex++
        process = new EmitNode(
            nodeIP: nodeIP,
            clusterIndex: clusterIndex,
            nodeIndex: nodeIndex,
            nInternals: structure[clusterIndex].workers,
            toNextNode: toNextNodes,
            readyToSend: readyToSend,
            useIndex: useIndex,
            objectClass: classDefinitions.emitClass,
            sourceClass: classDefinitions.sourceDataClass ,
            structure: structure,
            nodeLoadTime: startTime,
            fromHost: fromHost,
            toHost: toHost
        )
        break
      case structureMax:  // collect node
        assert structure[clusterIndex].typeName == "collect":
            "Node $nodeIP in $clusterIndex: collect expected, got ${structure[clusterIndex].typeName}"
        int nodeIndex = 0
        while( structure[clusterIndex].allocatedNodeIPs[nodeIndex] != nodeIP) nodeIndex++
        process = new CollectNode(
            nodeIP: nodeIP,
            fromPreviousNodes: fromPreviousNode,
            nPrevious: structure[clusterIndex - 1].nodes,
            readyToRead: readyToRead,
            clusterIndex: clusterIndex,
            nodeIndex: nodeIndex,
            nInternals: structure[clusterIndex].workers,
            collectClass: classDefinitions.collectClass,
            structure: structure,
            nodeLoadTime: startTime,
            fromHost: fromHost,
            toHost: toHost
        )
        break
      default: // work node
        Class localClass
        localClass = null
        if (classDefinitions.workDataClasses != null)
          localClass = classDefinitions.workDataClasses[clusterIndex-2]
        assert structure[clusterIndex].typeName == "work":
            "Node $nodeIP in $clusterIndex: work expected, got ${structure[clusterIndex].typeName}"
        int nodeIndex = 0
        while( structure[clusterIndex].allocatedNodeIPs[nodeIndex] != nodeIP) nodeIndex++
        process = new WorkNode(
            nodeIP: nodeIP,
            outputToNodes: toNextNodes,
            readyToSend: readyToSend,
            useNode: useIndex,
            fromPreviousNodes: fromPreviousNode,
            nPrevious: structure[clusterIndex - 1].nodes,
            readyToRead: readyToRead,
            fromHost: fromHost,
            toHost: toHost,
            nInternals: structure[clusterIndex].workers,
            nodeIndex: nodeIndex,
            clusterIndex: clusterIndex,
            nodeLoadTime: startTime,
            structure: structure,
            methodName: structure[clusterIndex].workMethodName,
            localDataClass:  localClass
        )
        break
    }
    new PAR([process]).run()
    println "Node $nodeIP total elapsed time = ${System.currentTimeMillis() - startTime} milliseconds"
  } //invoke

  /**
   * Invoke a Node
   * @param args [0] is the hostIP address
   *             [1] is the IP address of the node when running in Local mode using loop back addresses
   */
  static void main(String[] args) {
      switch (args.size()){
        case 1:
          new NodeRun(args[0]).invoke()
          break
        case 2:
          new NodeRun(args[0], args[1]).invoke()
          break
        default:
          println "NodeRun: wrong number of arguments, ${args} supplied" +
              "\n\targs[0] specifies the hostIP address" +
              "\n\targs[1] specifies IP address of node when using Local loop back testing"
      }

  }
}
