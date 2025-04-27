package cluster_cli

import cluster_cli.run.HostRun

class T1Host {
  static void main(String[] args) {
    String structure = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test1"
    Class emitClass = EmitObject
    Class collectClass = CollectObject
    new HostRun(structure, emitClass, collectClass, "Local").invoke()
  }
}
