package WorkNetInvoke

/**
 * Before running this code the HostNode MUST executed so that it can print out
 * the IP-address of the host node and also confirm the number of nodes required.
 *
 *
 * A user is expected to create a jar artifact of this
 * code so that it can be invoked directly from a command
 * line using
 *
 * java -jar NetNode.jar host-ip-address
 *
 * This NodeRun.jar can be used to load ANY application because the nodes do not
 * vary between application as all the required classes are passed from the host node
 *
 */

class NodeRun {
    static void main(String[] args) {
        String IPaddress = args[0]
        new cluster_framework.run.NodeRun(IPaddress).invoke()
    }
}
