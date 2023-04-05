package server;

import javafx.util.Pair;
import server.models.*;

import java.io.*;
import java.net.*;
import java.util.*;

import static server.models.Course.filterBySession;

/**
 * Server class that waits for connections from a port and processes client requests.
 */
public class Server {

    /**
     * Command Line needed to register user to a new class
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Command Line needed to load classes available for a specific semester
     */
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Server class constructor that creates the Server & creates ServerSocket & its server attributes.
     *
     * @param port port to listen to.
     * @throws IOException if I/O error while creating socket.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Adds a new eventHandler to server's ArrayList handlers.
     *
     * @param h eventHandler to be added.
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Starts the server & waits for incoming requests from client.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Waits for client requests & process them
     *
     * @throws IOException            if I/O error when reading input stream.
     * @throws ClassNotFoundException if different object type from input stream
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Take in a command line and split it into one command and one argument
     *
     * @param line the command line to be processed
     * @return Pair object for the command & the argument
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Cut client connection & close input/output streams.
     *
     * @throws IOException if I/O error while trying to close streams.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }


    /**
     * Process the commands "REGISTER_COMMAND" and "LOAD_COMMAND" from the command line
     *
     * @param cmd command to be executed
     * @param arg argument to be passed with the command
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     *
     * @param session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String session) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cours.txt"));
            List<Course> courseList = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {

                //Splits the line
                String[] addCourse = line.split("/t");
                Course newCourse = new Course(addCourse[1], addCourse[0], addCourse[2]);
                courseList.add(newCourse);
            }
            reader.close();

            //Filters the list of classes for one specific session
            List<Course> filteredCourses = filterBySession(courseList, session);

            objectOutputStream.writeObject(filteredCourses);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receives the registrationForm from the client & uses the information given to add new line to inscription.txt
     */
    public void handleRegistration() {
        try {
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            FileWriter writer = new FileWriter("inscription.txt", true);
            writer.write(registrationForm.toFormat());
            writer.write(System.lineSeparator());
            writer.close();

            objectOutputStream.writeObject("Inscription complétée.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
