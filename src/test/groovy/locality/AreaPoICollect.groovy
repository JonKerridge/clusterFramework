package locality

import cluster_framework.records.CollectInterface

class AreaPoICollect implements CollectInterface<AreaData> {

  List <AreaData> allAreas
  List <Location> accessedPoIs
  PrintWriter printWriter

  AreaPoICollect(List p){
    String fileName = p[0]
    printWriter = new PrintWriter(fileName)
    allAreas = []
    accessedPoIs = []
  }

  /**
   * collate() is used to aggregate the results of the application and is called as
   * each data object is read by a Collect process
   *
   * @param data the data object to be aggregated of the same type as that created in the Emit cluster
   * @param params any parameters required for collate to function as required
   */
  @Override
  void collate(AreaData data, List params) {
    allAreas << data
    printWriter.println "$data"
    data.nearPoIs.each{loc ->
      accessedPoIs << loc
    }
  }

  /**
   * finalise() is called once in the Collect process and is used to provide any
   * post-processing of the data aggregated by collate()
   *
   * @param params any parameters required for collate to function as required,
   * for example the name of a file into which results should be saved
   */
  @Override
  void finalise(List params) {
    List <Integer> poisNotUsed
    poisNotUsed = []
    int current
    current = 0
    println "\n"
    allAreas.each {ad ->
      print "${ad.areaLocation} :: "
      ad.nearPoIs.each {loc ->
        print "$loc; "
      }
      println " "
    }
    def uniquePoIs = accessedPoIs.toUnique { a, b -> a.id <=> b.id}
    def sortedPoIs = uniquePoIs.toSorted{a,b -> a.id <=> b.id}

    while ( current < (sortedPoIs.size()-1)){
      if ( (sortedPoIs[current].id + 1) != sortedPoIs[current+1].id)
        poisNotUsed << sortedPoIs[current+1].id
      current = current + 1
    }

    println "PoIs not accessed = $poisNotUsed "
    printWriter.flush()
    printWriter.close()

  }
}
