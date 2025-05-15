package cluster_framework.records
/**
 * The interface used by Collector processes in the Collect Cluster
 * @param <T>  the class definition of the data object processed by this application,
 * usually the same as that defined in the EmitInterface, which will be processed in the Collect phase
 */
interface CollectInterface <T> {
  /**
   * collate() is used to aggregate the results of the application and is called as
   * each data object is read by a Collect process
   *
   * @param data the data object to be aggregated of the same type as that created in the Emit cluster
   * @param params any parameters required for collate to function as required
   */
  void collate (T data, List params)
  /**
   * finalise() is called once in the Collect process and is used to provide any
   * post-processing of the data aggregated by collate()
   *
   * @param params any parameters required for finalise to function as required,
   * for example the name of a file into which results should be saved
   */
  void finalise(List params)
}