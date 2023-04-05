package client;

import java.io.*;
import java.net.Socket;
import java.lang.*;
import java.util.Scanner;


public class Client {

    private Socket clientSocket;
    private OutputStreamWriter input;
    private BufferedWriter writer;


    public Client(String address, int port) throws IOException {
        this.clientSocket = new Socket(address, port);
        this.input = new OutputStreamWriter(clientSocket.getOutputStream());
        this.writer = new BufferedWriter(input);
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 1337);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            System.out.println("Envoi de : " + line);
            client.writer.append(line + "\n");

            client.writer.flush();
        }

        client.writer.close();
    }
}