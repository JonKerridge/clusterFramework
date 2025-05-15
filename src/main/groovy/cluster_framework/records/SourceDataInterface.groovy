package cluster_framework.records

/**
 * The interface used by an Emitter process in an EmitNode to get the next
 * source data object to be incorporated into an object of type EmitInterface.
 *
 * It is assumed a file will have been opened containing the required source
 * data objects which can be read as each create() is called from the emit object.
 *
 * The file name will have been passed as a parameter, in the emit specification
 * to the constructor associated with the class implementing this interface.
 * The constructor will then populate the object with source data objects
 *
 * For example the following shows an emit specification in which there
 * are 2 Emitter processes, in a single node.  Each Emitter process will be associated
 * with an object of type SourceDataInterface.  One will be passed the file ./data/areas500a.loc and
 * the other will be passed ./data/areas500b.loc as the parameter to the object's constructor.
 *
 * <p>
 *   emit -nodes 1 -workers 2  -f ./data/areas500a.loc,./data/areas500b.loc
 *
 *
 * @param <T> The type of the object manipulated in the interface.  Typically, the constructor
 * will create an iterable collection (List most probably) of objects of type T.
 */


interface SourceDataInterface <T> {


/**
 *A method that gets the next data object from the source data object
 *
 * @return an object that can be incorporated into an instance of an object of type EmitInterface
 *         or null when there are no more objects.  This method will be called from the create method
 *         of an object implementing the EmitInterface.
 */
  T getSourceData()
}