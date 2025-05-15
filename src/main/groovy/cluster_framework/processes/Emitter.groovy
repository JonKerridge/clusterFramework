package cluster_framework.processes

import cluster_framework.records.EmitInterface
import cluster_framework.records.ExtractParameters
import cluster_framework.records.SourceDataInterface
import cluster_framework.records.TerminalIndex
import jcsp.lang.CSProcess
import jcsp.lang.ChannelOutput

class Emitter implements CSProcess{
  ChannelOutput output
  Class classDef, sourceDef
  List emitParams
  int internalIndex, nodeIndex, clusterIndex
  String sourceDataFileName

  @Override
  void run() {
//    println "Emitter: $sourceDataFileName"
//    int totalBytes
    Object sourceData
    sourceData = null
    if (sourceDataFileName != null){
      assert (sourceDef != null):"Source Data File Name specified $sourceDataFileName but no source class specified"
      // the declared constructor has a single String containing filename as parameter
      Class[] cArg = new Class[1]
      cArg[0] = String.class
      sourceData = sourceDef.getDeclaredConstructor(cArg).newInstance(sourceDataFileName)
    }
    List parameterValues = ExtractParameters.extractParams(emitParams[0] as List, emitParams[1] as List)
//    println "Emit [$clusterIndex, $nodeIndex, $internalIndex] parameters $parameterValues, source $sourceDataFileName"
    Class[] cArg = new Class[1]
    cArg[0] = List.class
    Object emitClass = classDef.getDeclaredConstructor(cArg).newInstance(parameterValues)
    Object ec
    if (sourceData  == null)
      ec = (emitClass as EmitInterface).create(null)
    else
      ec = (emitClass as EmitInterface).create((sourceData as SourceDataInterface).getSourceData())
//    totalBytes = 0
    while (ec != null) {
//      ByteArrayOutputStream bOut = new ByteArrayOutputStream()
//      ObjectOutputStream oOut = new ObjectOutputStream(bOut)
//      oOut.writeObject(ec)
//      oOut.close()
//      totalBytes = totalBytes + bOut.toByteArray().length
//      println "Emit writing $ec with size ${bOut.toByteArray().length}"
      output.write(ec)
//      println "Emit written $ec"
      if (sourceData  == null)
        ec = (emitClass as EmitInterface).create(null)
      else
        ec = (emitClass as EmitInterface).create((sourceData as SourceDataInterface).getSourceData() )

    }
    output.write(new TerminalIndex(
        nodeIP: "emitter" ,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex
      )
    )
//    println "Emit has terminated "
  } // run
}
