package cluster_framework.records

/**
 *
 */
class ParseRecord implements Serializable {
  String typeName                 // host, emit, source, work, collect
  String hostAddress
  String version
  List <String> fixedIPAddresses  // used for fixed nodes with specific role
  int nodes       //number of nodes in cluster
  int workers     //number of worker processes in each nodeLoader of cluster
  List <String> sourceDataFileNames //the names of all the individual Emitter source data files v2
  String workMethodName // name of method to be executed in work nodeLoader
  String workDataFileName  //added version 2
  //List <List <String>> workDataParameters   //list of parameters used in work data constructor version 2
  List <List <String>> workParameters  // a list of parameters in their String representation (work only)
  List <List <String>> emitParameters // list of list of parameters used by each Emitter process constructor
  String outFileName  // base name of file to which collected objects are written, multiples differentiated by WorkerID
//  String finaliseNameString  // used in collect type only
  List <List <String>> collectParameters
  // list of list of collectParameters used by each collect method
  List <List <String>> finaliseParameters
  // list of list of finaliseParameters used in each finalise method
//  NetLocation outputManagerLocation // NetChannelLocation of manager that manages output from the Cluster
//  NetLocation inputManagerLocation   // NetChannelLocation of manager that manages input from Cluster
  List <String> allocatedNodeIPs  // completed by host to indicate nodeLoader ips

  ParseRecord(){
    fixedIPAddresses = []
    workParameters = []
    emitParameters = []
    sourceDataFileNames= []
    collectParameters = []
    finaliseParameters = []
    allocatedNodeIPs = []
  } // constructor

  @Override
  String toString() {
    String s = "type= $typeName, host= $hostAddress, version = $version,   " +
        "\n\tnodes= $nodes, workers= $workers, " +
        "\n\tfixed IPs= $fixedIPAddresses " +
        "\n\temitParams= $emitParameters" +
        "\n\tsource data files = $sourceDataFileNames" +
        "\n\twork method= $workMethodName, work params=$workParameters, data file = $workDataFileName, " +
        "\n\tcollect params= $collectParameters," +
        "\n\tfinalise params= $finaliseParameters," +
        "\n\toutFile= $outFileName," +
        "\n\tallocIps= $allocatedNodeIPs"
    return s
  }
}
