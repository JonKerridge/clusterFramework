package cluster_framework.records

class ClassDefinitions implements Serializable{
  Class emitClass  // the class associated with the emit phase
  Class collectClass // the class associated with the collect phase
  Class sourceDataClass // added v2 so source data can come from file
  List <Class> workDataClasses
  //added v2 for the shared data in worker nodes, null if not required
  String version  // the version must match for Parser and Host and Nodes
}
