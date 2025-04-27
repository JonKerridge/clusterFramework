package cluster_cli.processes

import cluster_cli.records.EmitInterface
import cluster_cli.records.ExtractParameters
import cluster_cli.records.TerminalIndex
import jcsp.lang.CSProcess
import jcsp.lang.ChannelOutput

class Emitter implements CSProcess{
  ChannelOutput output
  Class<?> classDef
  List emitParams
  int internalIndex, nodeIndex, clusterIndex

  @Override
  void run() {
    List parameterValues = ExtractParameters.extractParams(emitParams[0] as List, emitParams[1] as List)
    println "Emit [$clusterIndex, $nodeIndex, $internalIndex] parameters $parameterValues"
    Class[] cArg = new Class[1]
    cArg[0] = List.class
    Object emitClass = classDef.getDeclaredConstructor(cArg).newInstance(parameterValues)
    def ec = (emitClass as EmitInterface).create()
    while (ec != null) {
//      println "Emit writing $ec"
      output.write(ec)
//      println "Emit written $ec"
      ec = (emitClass as EmitInterface).create()
    }
    output.write(new TerminalIndex(
        nodeIP: "emitter" ,
        nodeIndex: nodeIndex,
        clusterIndex: clusterIndex
      )
    )
    println "Emit has terminated"
  } // run
}
