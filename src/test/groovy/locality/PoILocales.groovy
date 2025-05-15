package locality

import cluster_framework.records.WorkDataInterface

class PoILocales implements WorkDataInterface<Location> {

  List <Location> pois
  int nPoi

  PoILocales(String fileName){
    pois = []
    File objFile = new File("./$fileName")
    objFile.withObjectInputStream { inStream ->
      inStream.eachObject {
        pois << it
      }
    }
    nPoi = pois.size()
  }

  @Override
  Location getNextWorkData(int index) {
    // assume index is valid!!
    return pois[index]
  }

  @Override
  int getWorkDataSize() {
    return nPoi
  }
}
