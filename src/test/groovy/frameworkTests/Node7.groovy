package frameworkTests

import cluster_framework.run.NodeRun

class Node7 {
  static void main(String[] args) {
    new NodeRun("127.0.0.1", "127.0.0.7").invoke()
  }
}
