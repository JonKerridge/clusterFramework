package cluster_cli.records

class ClassDefinitions implements Serializable{
  Class <EmitInterface> emitClass  // the class associated with the emit phase
  Class <CollectInterface> collectClass // the class associated with the collect phase
  Class <SharedDataInterface> sharedDataClass //added v2 for the shared data in worker nodes, null if not require
  String version  // the version must match for Parser and Host and Nodes
}
