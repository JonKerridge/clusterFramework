package frameworkTests

import cluster_framework.run.HostRun

class T7Host {
  static void main(String[] args) {
    String structure = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test7"
    Class emitClass = EmitObject
    Class collectClass = CollectObject
    new HostRun(structure, emitClass, collectClass, "Local").invoke()
  }
}
