package cluster_cli.records
/**
 * Defines the interface used by Worker Nodes to preload a shared data object
 * that is the accessed ReadOnly by all the Worker processes in the WorkerNode
 *
 * @param <T> The class definition of the objects to be loaded into WorkerNode(s)
 *
 *
 */

interface SharedDataInterface<T> {
    T loadSharedData()
}