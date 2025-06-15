package cluster_framework.records
/**
 * Defines the interface used by the Emitter processes in the Emit Cluster to
 * create objects that will be inserted into the subsequent worker processes
 * in the complete, Emitter, Worker and Collector network.
 *<p>
 * Any object implementing this interface will have a constructor that can be passed
 * any required parameters from the *.clic DSL file specified in the emit specification.
 * Note that if there is more than one Emitter process in the emit cluster,
 * which can comprise many EmitNodes, then each Emitter process will have
 * its own set of parameters specified in the *.clic file
 * <p>
 * For example consider an emit Cluster comprising 2 nodes,
 * each with 2 internal Emitter (worker) processes. Then assuming the constructor
 * requires two integer parameters a possible emit specification would be:
 * <p>
 * emit -n 2 -w 2 -p int,int!512,1024!1024,2048!2048,4096!4096,8192
 * <p>
 * The parameter combinations would be passed in sequence to the 4 Emitter process
 * that would be created with 2 on each separate node in the Emit cluster.
 * <p>
 * Note the use of ! to subdivide the string into first the type definitions of the parameters
 * and then the individual parameter strings.  The multiple parameter values are separated by a comma.
 * <p>
 * If the Emitter processes use a file to supply the source data for the application
 * using the SourDataInterface then a typical emit specification would pass no parameters
 * but would specify the required number of comma separated source file names.
 * Consider the following which
 * has a single emit node with 2 emitter processes each being allocate a different file.
 * <p>
 * emit -nodes 1 -workers 2  -f ./data/areas500a.loc,./data/areas500b.loc
 *
 *
 * @param <T>  The class definition of the data objects to be created
 *
 * Any object implementing this interface will also have to provide methods
 * that can be called by Worker processes.  Such methods can be passed parameter values
 * from within the *.clic DSL specification file.
 * The FIRST parameter of any such method must contain a parameter of type SourceDataInterface,
 * this will be followed by a List containing the values from the parameter list.  If no
 * SourceDataInterface is used then the parameter will be passed as null.
 */
interface EmitInterface <T> extends Serializable {
  /**
   * create() is used to generate a new instance of data objects in the Emitter process,
   * once all the objects have been created it should return null
   *
   * The create method will use another constructor for the object that creates the data objects passed to
   * the rest of the process network.  The properties of the constructor will be created from
   * data held within the base object instance initialised when the object was initially constructed.
   *
   * If the application uses a source data file then the
   *
   * @param sourceData an object containing source data for the emitted objects, obtained
   * from an object implementing the SourceDataInterface or null otherwise
   * @return A new instance of type T or null once all the data objects have been created
   */
  T create(Object sourceData)


}