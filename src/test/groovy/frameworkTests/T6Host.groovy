package frameworkTests

import cluster_framework.run.HostRun

class T6Host {
  static void main(String[] args) {
    String structure = "D:\\IJGradle\\clusterFramework\\src\\test\\groovy\\frameworkTestFiles/test6"
    Class emitClass = EmitObject
    Class collectClass = CollectObject
    new HostRun(structure, emitClass, collectClass, "Local").invoke()
  }
}
