package cluster_cli

import cluster_cli.records.EmitInterface

class EmitObject implements EmitInterface<EmitObject>, Serializable{
  int value
  int initialValue, finalValue

  // values ranging from (initialValue + 1) to finalValue will be output

  EmitObject(List params){
    initialValue = params[0] as int
    finalValue = params[1] as int
  }

  EmitObject(int value){
    this.value = value
    this.initialValue = 0
    this.finalValue = 0
  }

  void updateMethod(List params){
    value = value + (params[0] as int)
  }

  void updateMethod2(List params){
    int multiplier
    multiplier = (int) (value / (1000 as int))
    value = value - (multiplier * (1000 as int))
  }

  String toString(){
    return "$value"
  }

  @Override
  EmitObject create() {
    initialValue++
    if (initialValue <= finalValue) {
      return new EmitObject(initialValue)
    }
    else
      return null
  }
}
