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
  Class <?> collectClass // methods only no publicly accessible properties
  String outFileName // name of file to which incoming objects will be written unchanged
  List <List <String>> collectParameters
  List <List <String>> finaliseParameters


  @Override
  void run() {
    ObjectOutputStream outStream
    if (outFileName != null ) {
      String outFileString = "./${outFileName}-${nodeIndex}-${internalIndex}.out"
      println "Collector [$clusterIndex, $nodeIndex, $internalIndex] writing to $outFileString"
      File objFile = new File(outFileString)
      outStream = objFile.newObjectOutputStream()
    }
    else outStream = null

    def collectInstance = collectClass.getDeclaredConstructor().newInstance()

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
        if (outStream != null) outStream.writeObject(object)
      }
    } // running
    //call the finalise method if it exists and close the output stream
    (collectInstance as CollectInterface).finalise(finaliseParameterValues)
    if (outStream != null) {
      outStream.flush()
      outStream.close()
    }
  }
}
