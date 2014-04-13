package eu.com.cwsfe.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;

/**
 * Created by Radosław Osiński
 */
public class TestConnector {

    private Cluster cluster;

    public static void main(String[] args) {
        TestConnector client = new TestConnector();
        client.connect("127.0.0.1");
        client.close();
    }

    public void connect(String node) {
        cluster = Cluster.builder()
                .addContactPoint(node)
                        // .withSSL() // Uncomment if using client to node encryption
                .build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
    }

    public void close() {
        cluster.close();
    }

}
