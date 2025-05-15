package cluster_framework.records
/**
 * Defines the interface used by Worker Nodes to access the preloaded shared data object
 * that is the accessed ReadOnly by all the Worker processes in the WorkerNode.
 * It is assumed that the data is held in a data structure that can be accessed using
 * an integer index value.  No data may be changed in this shared data object, but this
 * cannot checked by the framework.
 *<p>
 * The name of a file containing the required data will have been passed to a constructor
 * of the object in which the data is stored.  A reference to the object will be passed to
 * each Worker process in the WorkerNode, which it will then be able to access as a parameter
 * of the WorkerNode.
 *
 * <p>
 *   work -n 1 -w 4 -m distance -p double!3.0 -f ./data/pois250.loc
 * <p>
 * A single WorkerNode is specified with 4 Worker processes.  The method called distance is
 * obtained from an object that implements the EmitInterface and will be passed a single
 * parameter, the double value 3.0.  The file ./data/pois250.loc will be passed to the constructor
 * for the object implementing WorkDataInterface.  The shared data will be passed as the first parameter
 * to any method name referred to in a work specification.  Each Worker process will have access to
 * their own copy of the data held in the file.
 * The method distance has the signature:
 * <pre>{@code
 *
 *   void distance(WorkDataInterface <Location> workData, List p)
 *
 *   }
 * </pre>
 *   where T in this case is Location and the object holding the shared work data is referred to
 *   as workData.  The value of the parameter, 3.0, will be obtained from p[0].
 *
 * @param <T> The class definition of the objects to be loaded into WorkerNode(s)
 *
 */

interface WorkDataInterface <T> {

    /**
     * The method that gets the next item of work data.
     *
     * @param index the subscript of the data item to be returned
     * @return an object from the shared work data such that repeated calls will return the
     * shared data in some user defined sequence
     */
    T getNextWorkData(int index)

    /**
     * To get the number of objects in the shared data so that the create method can
     * iterate through them.
     *
     * @return the number of objects in the shared work data structure
     */
    int getWorkDataSize()
}