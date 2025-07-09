package WorkNetInvoke

import cluster_framework.run.HostRun
import locality.AreaData
import locality.AreaLocales
import locality.AreaPoICollect
import locality.PoILocales

/**
 * The NetHost code must be run first when using s number of PCs.
 * It will print out its IP address, which needs to be passed as an argument to
 * each of the nodes.  It will also confirm the number of separate nodes required.
 *
 * You will see that the Host code does access the class files used in the application.
 * These will be passed to the nodes on as required basis automatically.
 *
 * Assuming a jar artifact called HostNode.jar is created then a host node can be
 * executed using the java command
 *
 * java -jar HostNode.jar clicFileName
 */

class HostNode {
    static void main(String[] args) {
        String structure = args[0]  // name of clic file without suffix
        Class emitClass = AreaData
        Class sourceData = AreaLocales
        List <Class> workData = [PoILocales]
        Class collectClass = AreaPoICollect
        new HostRun(structure, emitClass, sourceData, workData, collectClass, "Net").invoke()
    }

}
