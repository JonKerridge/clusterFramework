package sourceWorkTests

import cluster_framework.run.HostRun
import locality.AreaData
import locality.AreaLocales
import locality.AreaPoICollect
import locality.PoILocales

class Test1111Host {
    static void main(String[] args) {
      String structure = "./src/test/groovy/sourceWorkTests/Test1111"
      Class emitClass = AreaData
      Class sourceData = AreaLocales
      List <Class> workData = [PoILocales,PoILocales]
      Class collectClass = AreaPoICollect
      new HostRun(structure, emitClass, sourceData, workData, collectClass, "Local").invoke()
    }
}
