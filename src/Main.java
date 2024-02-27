import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Timestamp;

public class Main {
    public static int PORT = 7392;
    public static int MAX_BYTES = 4096;
    public static String IP = "192.168.84.110";
    public String command = "IDLE";

    private boolean doSay = false;

    public static void main(String[] args) throws IOException {
        Main help = new Main();
        help.Server();
    }

    private void Server() throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(Main.PORT);
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(InetAddress.getByName(Main.IP), Main.PORT));
        System.out.println("Server Started. Listening for Clients on port " + Main.PORT + "...");
        byte[] receiveData = new byte[Main.MAX_BYTES];
        DatagramPacket receivePacket;
        while (true) {
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String clientMessage = new String(receivePacket.getData(),0,receivePacket.getLength());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            command = clientMessage;
            System.out.println("[" + timestamp.toString() + "]" + " [IP: " + IPAddress + " | Port: " + port +"] " + clientMessage);
            Controller();
        }
    }

    private void Controller(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (doSay) {
            say(timestamp);
            return;
        }

        switch (command) {
            case "IDLE" -> idle(timestamp);
            case "FORWARD" -> forward(timestamp);
            case "LEFT" -> left(timestamp);
            case "RIGHT" -> right(timestamp);
            case "BACKWARDS" -> backwards(timestamp);
            case "SAY" -> say(timestamp);
            default -> System.out.println("[" + timestamp.toString() + "] " + "NOT VALID!");
        }
    }

    private void idle(Timestamp timestamp) {
        System.out.println("[" + timestamp.toString() + "] " + "I'm now Idle.");
    }

    private void forward(Timestamp timestamp) {
        System.out.println("[" + timestamp.toString() + "] " + "I'm now going Forward.");
    }

    private void left(Timestamp timestamp) {
        System.out.println("[" + timestamp.toString() + "] " + "I'm now turning Left.");
    }

    private void right(Timestamp timestamp) {
        System.out.println("[" + timestamp.toString() + "] " + "I'm now turning Right.");
    }

    private void backwards(Timestamp timestamp) {
        System.out.println("[" + timestamp.toString() + "] " + "I'm now going Backwards.");
    }

    private void say(Timestamp timestamp) {
        if (doSay) {
            System.out.println("[" + timestamp.toString() + "] " + command);
            doSay = false;
        } else {
            System.out.println("[" + timestamp.toString() + "] " + "What do you have to say?");
            doSay = true;
        }
    }
}