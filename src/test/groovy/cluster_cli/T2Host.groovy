package cluster_cli

import cluster_cli.run.HostRun

class T2Host {
  static void main(String[] args) {
    String structure = "D:\\IJGradle\\cluster_cli\\src\\test\\groovy\\parserTests_101/test2"
    Class emitClass = EmitObject
    Class collectClass = CollectObject
    new HostRun(structure, emitClass, collectClass, "Local").invoke()
  }
}
