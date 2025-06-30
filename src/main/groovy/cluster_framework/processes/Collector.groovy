package cluster_framework.processes

import cluster_framework.records.CollectInterface
import cluster_framework.records.EmitInterface
import cluster_framework.records.ExtractParameters
import cluster_framework.records.TerminalIndex
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Collector implements CSProcess{
  ChannelOutput requestObject   // connects to ReadBuffer fromInternalProcesses
  ChannelInput inputObject  // connects to ReadBuffer toInternalProcesses[internalIndex]
  int internalIndex, nodeIndex, clusterIndex
  // class associated with the collector that defines the collate and finalise methods
  Class <?> collectClass
  List <List <String>> classParameters
  List <List <String>> collectParameters
  List <List <String>> finaliseParameters


  @Override
  void run() {
    Class[] cArg = new Class[1]
    cArg[0] = List.class
    List classParameterValues = []
    if (classParameters != null) {
      classParameterValues = ExtractParameters.extractParams(classParameters[0], classParameters[1])
    }
    def collectInstance = collectClass.getDeclaredConstructor(cArg).newInstance(classParameterValues)

    List collectParameterValues, finaliseParameterValues
    collectParameterValues = []
    finaliseParameterValues = []
    if (collectParameters != null )
      collectParameterValues = ExtractParameters.extractParams(collectParameters[0], collectParameters[1])
    if (finaliseParameters != null )
      finaliseParameterValues = ExtractParameters.extractParams(finaliseParameters[0], finaliseParameters[1])

    boolean running
    running = true
    while (running) {
      requestObject.write(internalIndex)
      def object = inputObject.read()
      if (object instanceof TerminalIndex) {
        running = false
      }
      else { // process incoming data object
//        print "Collect $internalIndex: "
        (collectInstance as CollectInterface).collate((object as EmitInterface), collectParameterValues)
      }
    } // running
    //call the finalise method if it exists and close the output stream
    (collectInstance as CollectInterface).finalise(finaliseParameterValues)
  }
}
