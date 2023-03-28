package server;

/**
 * ServerLauncher Class used to launch a new Server on the specified local port 1337
 */
public class ServerLauncher {

    /** Local Port 1337 used for the launch of the server*/

    public final static int PORT = 1337;

    /**
     * Starts a new Server with port 1337 & catch any "Throwable" errors at the launch of the server.
     * @param args command line
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}