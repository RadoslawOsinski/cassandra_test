package eu.com.cwsfe.cassandra;

import com.datastax.driver.core.*;

/**
 * Created by Radosław Osiński
 */
public class TestSelectConnector {

    private Cluster cluster;
    private Session session;

    public Cluster getCluster() {
        return cluster;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public static void main(String[] args) {
        TestSelectConnector client = new TestSelectConnector();
        client.connect("127.0.0.1");
        client.setSession(client.getCluster().connect());
//        client.createTestSchema();
//        client.loadData();
        client.testSelect();
        client.close();
    }

    private void testSelect() {
        ResultSet results = session.execute("SELECT * FROM test.playlists " +
                "WHERE id = 2cc9ccb7-6221-4ccb-8387-f22b6a1b354d;");
        System.out.println(String.format("%-30s\t%-20s\t%-20s\n%s", "title",
                "album", "artist",
                "-------------------------------+-----------------------+--------------------"));
        for (Row row : results) {
            System.out.println(String.format("%-30s\t%-20s\t%-20s",
                    row.getString("title"),
                    row.getString("album"), row.getString("artist")));
        }
        System.out.println();

    }

    private void loadData() {
        session.execute(
                "INSERT INTO test.songs (id, title, album, artist, tags) " +
                        "VALUES (" +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'," +
                        "{'jazz', '2013'})" +
                        ";"
        );
        session.execute(
                "INSERT INTO test.playlists (id, song_id, title, album, artist) " +
                        "VALUES (" +
                        "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," +
                        "756716f7-2e54-4715-9f00-91dcbea6cf50," +
                        "'La Petite Tonkinoise'," +
                        "'Bye Bye Blackbird'," +
                        "'Joséphine Baker'" +
                        ");"
        );
    }

    private void createTestSchema() {
        session.execute("CREATE KEYSPACE test WITH replication " +
                "= {'class':'SimpleStrategy', 'replication_factor':3};");
        session.execute(
                "CREATE TABLE test.songs (" +
                        "id uuid PRIMARY KEY," +
                        "title text," +
                        "album text," +
                        "artist text," +
                        "tags set<text>," +
                        "data blob" +
                        ");"
        );
        session.execute(
                "CREATE TABLE test.playlists (" +
                        "id uuid," +
                        "title text," +
                        "album text, " +
                        "artist text," +
                        "song_id uuid," +
                        "PRIMARY KEY (id, title, album, artist)" +
                        ");"
        );
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
