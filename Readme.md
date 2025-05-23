### Introduction

The software is used to build clustered computing networks. 
The network is defined by a DSL (Domain Specific Language) that uses a 
command line interface based on picocli (https://picocli.info/). 
The user has to supply at least two classes; 
one that defines the data to be processed and 
the other that defines what happens to the final results of processing.
Additionally, a further two classes can be defined which 
define; source data than can be read in as source data for the application and
data that can be incorporated into the processing stage of the application.

The library has been compiled using Groovy 3 and Java 11.

The goal of the library is to create a parallel processing structure which takes 
an existing sequential application code and enables its parallelisation 
with a minimum of effort on behalf of the programmer.  The resulting parallel
architecture comprises an Emit Cluster followed by one or more Work Clusters
then followed by a Collect cluster.  The classes the user has to specify all 
use an Interface definition to ensure correct interaction with the framework.

Each cluster can comprise one or more nodes where a node is a multicore processor.  
The user can specify the number of the cores that are to be allocated to the application.

The parallel architecture created by the framework has a formal proof of correctness.
The proof mechanism uses the FDR4 (https://cocotec.io/fdr/) refinement checking tool 
for parallel systems.  The required process definitions can be found in the folder
*cspmDefinitions*.

The following is the DSL for a simple system with no source data or worker data requirements.

    version 2.0.3
    emit -nodes 1 -workers 2 -p int,int!0,100!100,200
    work -n 1 -w 4 -m updateMethod -p int!500
    collect -n 1 -w 1 -f Test3Results


There is 1 emit node using 2 workers (or cores).  The constructor for the Emit object 
requires two int parameters.  The first worker gets the values [0,100] and the second gets [100,200].
There is one work cluster comprising 1 node and uses 4 cores (workers).  The method that is called in the 
emitted object is called  *updateMethod*, and it uses the int parameter value [500].  The same parameter
value is passed to all the workers.

All parameters, in all cases,
are passed as elements of a List so that any number and type of parameters can be passed as required. 

The collect cluster comprises a single node with one worker (core) process.  All the data processed by the 
application will be saved in a file called *Test3Results*.

The following is the DSL specification for an application that uses both source and work data files.

    version 2.0.3
    emit -nodes 1 -workers 1  -f ./data/areas25000.loc
    work -n 1 -w 4 -m distance -p double!3.0 -f ./data/pois5000.loc
    collect -n 1 -w 1 -f ./data/LTest141Results


Each cluster comprises a single node.  The emit node will get its data from the file *./data/areas25000.loc*. 
The -f option will accept a comma separated list of file names such that each worker instance reads a different
file.  Thus, if there are 2 emit nodes each with 3 workers then 6 file names must be specified.

The work node will get its data from the file *./data/pois5000.loc*.  The worker node will hold a single 
copy of the data file which each worker will access in a read only manner.  The associated *WorkDataInterface* 
ensures this is mode of operation in the way that data is obtained from the shared work data.  Each node in the 
work cluster will read the same file.

*NOTE In Intellij the ./data folder is contained at the top level of the folder structure
directly accessible from the project folder*

### Interfaces

There are four interfaces the user needs to be aware of: *EmitInterface*, *CollectInterface*, *SourceDataInterface*, and the 
*WorkDataInterface*.  The following is a brief overview, the interface documentation gives more detail.

#### Emit Interface

    interface EmitInterface <T> extends Serializable {
    T create(Object sourceData)
    }

The emit cluster creates objects that implement the *EmitInterface* where *T* is the class name of the emitted object.
The *sourceData* parameter will be passed as *null* if no source data file is used. 

#### Collect Interface 

    interface CollectInterface <T> {
    void collate (T data, List params)
    void finalise(List params)
    }

The *CollectInterface* is used by the collect cluster processes and the type *T* is the class that
is emitted from the emit cluster.  The *collate* method is used to process incoming data objects to
create some local transitory collation of the results.  The *finalise* method is used to present the 
collated transitory results directly to the user in terminal output.  The DSL -f parameter specifies
a file into which all the received data objects will be stored for post-processing by another application.
In both cases the *List params* is obtained from the DSL specification.

#### SourceDataInterface

    interface SourceDataInterface <T> {
      T getSourceData()
    }

Am emitter worker process will call the method *getSourceData* when required.  The method returns null when 
all the data have been obtained from the associated data source.  The returned object will be used in the emitted 
object's *create* method, see Emit Interface above. The method is defined in the object that implements 
the *SourceDataInterface*.  This class will have a constructor that is used to read the 
source data file specified in the DSL specification.


#### WorkDataInterface

    interface WorkDataInterface <T> {
    T getNextWorkData(int index)
    int getWorkDataSize()
    }

A worker node reads a file of data that can be accessed for reading only by all the worker processes in that node as an iterable collection.
The number of elements in the collection can be obtained using the method *getWorkDataSize*
The constructor contained within a class implementing this interface will be passed the required file name.
The type *T* is the type of the data that will be returned by the method *getNextWorkData* at the specified subscript *index*.
It is up to the user to decide where and if checking of the iterator bound values is undertaken. The method *getNextWorkData*
may return *null* tyo indicate the end of the work data collection if required.

### Using the Framework

The framework has been designed so that the accuracy of the application can be checked by running the application classes in
a sequential format.  The application can then be tested in parallel using a multicore PC using a loop-back IP network using
addresses of the form 127.0.0.x.  Finally, the architecture can be run on a network of PCs or an HPC system, without
change to the application code, but by simply the manner in which the code is run.  On a PC cluster by using *Remote Desktop* and
on an HPC by using *SLURM* batch files.  To aid in this *jar* files are provided to run a host node and the other nodes.

#### Parsing a DSL file

A parser is provided that takes a DSL specification, with the extension *.clic*, and transforms it into
an object file with the extension *.clicstruct*. This can be input into a host node from which the rest
of the application architecture is created, with no user intervention.  The resulting application process
structure has a formal proof of correctness, meaning that no errors will be introduced as a result of the
parallelisation of the application.

#### RunHost.jar

This invokes a code called *HostRun* which has a single method called *invoke()*. The following example
shows how a  DSL specification is transformed into a call of HostRun.  The application has two work cluster
 both of which use the same work data source file.  The emitted data is obtained from a source data file.

**NOTE A directory (folder) called *data* must be at the top level in the project file structure**

#### DSL - Test1111.clic

    version 2.0.3
    emit -nodes 1 -workers 1  -f ./data/areas1000.loc
    work -n 1 -w 1 -m distance -p double!3.0 -f ./data/pois250.loc
    work -n 1 -w 1 -m adjacent -p double!6.0 -f ./data/pois250.loc
    collect -n 1 -w 1 -f ./data/Test1111Results

The file *./data/areas1000.loc* is used to create a class of type *AreaLocales* for source data.
The file *./data/pois250.loc* is used to create a class of type *PoILocales* for work data.
The class *AreaData* defines the methods *distance* and *adjacent*

#### RunHost jar

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

This version invokes the application on a loop-back network where it is assumed the host runs on the node
with IP address 127.0.0.1.  Note how the required classes definitions are specified.  In particular, note 
the List of class definitions specified for workData.  The smae class appears twice because each work cluster
uses the same work data file.
If this were to be run on a real network then the last parameter would be replaced by "Net" and the 
application would display the IP address of the nost node as this is required to run the clusters.

A more useful jar formulation that just requires the inputting of the structure file can be seen 
at frameworkTests/RunTest where a single program arguemnt is required which holds the path to
the structure file.

    class RunTest {
      static void main(String[] args) {
        String structure = args[0]
        Class emitClass = EmitObject
        Class collectClass = CollectObject
        new HostRun(structure, emitClass, collectClass, "Local").invoke()
      }
    }

This could be made into a .jar and then run from outwith your IDE.

If using the library in your own application then you can construct a .jar file which allows you
to run your application in as easy a way as possible, especially if using a PC cluster or HPC.


#### RunNode jar

    class Node2 {
        static void main(String[] args) {
            new NodeRun("127.0.0.1", "127.0.0.2").invoke()
        }
    }

A node is invoked by a call to *NodeRun*.  The above example shows the invocation of a node in local 
loop-back mode where two IP address are required.  The first is the IP address of the host and the second is 
the IP address of the node being created.  All nodes muxt have unique IP addresses.

In the case of a real network then only one address is required, that of the host node.

There is no need to ensure that all class files are required on all nodes.  Only the host node needs
access to all class files.  The application automatically transfers the required class files round
the network on an as required basis.  This means that the NodeRun.jar file is not application specific
and a single version of the jar will be able to run any application.

The user will have to create as many nodes as the application requires.  The accompanying code has a number
of tests which have predefined versions of Node codes for various nodes.

The jar file can be invoked with

    java -jar NetHost.jar host-ip-address

### Code Provided

As well as all the source code the *test* folder contains a variety of test codes.  
Most of these just use small data sets which do not parallelize well but do show the operation
of the framework.
There are some however in folder *sourceWorkTests* whichhave the prefix L which do show the 
effect of parallelization.  The folder *Sequential* contains a sequential invocation of the application,
with which to compare the parallel performance.  Each *L* test has the required files and there are some 
Node files that can be used to run the required number of nodes.  Each of the tests just creates a single
node in each of the clusters.  What varies is the number of parallel workers in the worker cluster

#### LTest111

This is a version with a single worker node and is essentially the same processing as the 
sequential version.It will take longer to run than the sequential version due to the 
extra communication required and the way in which methods are called in the parallel architecture.

#### LTest121

This version has 2 workers in the worker node and will run faster than both LTest111 
and the sequential version.

#### LTest141

This version has 4 workers in the worker node and will run faster than LTest121.
The improvement in time will not be linear with the number of worker processes

### Library Availability

The library, cluster_framework is available as a downloadable package from 

https://github.com/JonKerridge/clusterFramework

The package identity is

jonkerridge:cluster_framework:2.0.3

An example of the use of the library is also available at 

https://github.com/JonKerridge/cfExperiments