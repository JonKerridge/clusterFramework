package cluster_framework;

/**
 * A package that enables the creation of parallel processing application
 * on a network of workstations connected by an ethernet.
 *
 * The application is defined using a command line style domain specific language.
 *
 * The specification comprises, in minimal case:
 *
 * version v#
 * emit -n(odes) n# -w(orkers) w# [-p emitParameterString] [IP-address ..]
 * work  -n(odes) n# -w(orkers) w# -m(ethod) methodName [-p workParameterString] [IP-address ..]
 * collect -n(odes) n# -w(orkers) -f(ile) outputFileName [-cp collateParameterString] [-fp finaliseParameterString] [IP-address ..]
 *
 * In the above case there is a single work cluster and represents a processing farm
 *
 * The number of work clusters can be greater than 1 in which case the processing architecture
 * is a parallel pipeline. Each work cluster can have a different number of nodes and workers per node
 *
 * where
 * v# is the version number of the cluster_framework_2 library being used of the form a.b.c
 * n# is the number of nodes in the cluster
 * w# is the number of workers or internal processes in each node of the cluster
 * methodName is the name of a method in the data object class
 * outputFileName is the name of a file to which results will be written
 *
 * [] means optional
 * .. means repeated
 *
 * IP-address is the address to which a node will be allocated and n# such addresses must be specified
 * all parameter strings are of the form type,..!value,.. (the number of types and values must be the same)
 *  the i'th value will be interpreted as being of the i'th type
 * emitParameterString the parameters that will be passed to the emit data object's create() method
 * workParameterString  the parameters that will be passed to the method called methodName
 * collateParameterString the parameters that will be passed to the collect object's collate() method
 * finaliseParameterString the parameters that will be passed to the collect object's finalise() method
 *
 * The class Parser's parse() method is used to convert the specification into  a file that can be processed
 * by the HostRun class, which builds the host node and manages the distribution of the application software
 * to the other processing nodes created from the specification.
 *
 * The workstation processing nodes are invoked using the NodeRun class which has the single parameter
 * comprising the IP-address of the hostNode.
 *
 * The software can be tested on a single workstation prior to full implementation on a network, in which case
 * NodeRun is given two parameters; the host IP-address and the IP-address of the node being created.
 * All IP-addresses are of the form 127.0.0.n and typically the host node is 127.0.0.1.  Obviously running
 * the software in this way will not show any performance improvement but does allow checking of the
 * application prior to distribution over a real network.
 *
 * The test package for cluster_framework_2 shows how the application can be tested on a single workstation,
 * simulating multiple nodes.
 */