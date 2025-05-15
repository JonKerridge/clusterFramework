package sourceWorkTests

import cluster_framework.run.HostRun
import locality.AreaData
import locality.AreaLocales
import locality.AreaPoICollect
import locality.PoILocales

class Test11111Host {
    static void main(String[] args) {
      String structure = "./src/test/groovy/sourceWorkTests/Test11111"
      Class emitClass = AreaData
      Class sourceData = AreaLocales
      List <Class> workData = [PoILocales, PoILocales, PoILocales]
      Class collectClass = AreaPoICollect
      new HostRun(structure, emitClass, sourceData, workData, collectClass, "Local").invoke()
    }
}
