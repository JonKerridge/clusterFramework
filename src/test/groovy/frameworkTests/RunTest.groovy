package frameworkTests

import cluster_framework.run.HostRun

class RunTest {
  static void main(String[] args) {
    String structure = args[0]
    Class emitClass = EmitObject
    Class collectClass = CollectObject
    new HostRun(structure, emitClass, collectClass, "Local").invoke()
  }
}
