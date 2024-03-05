import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;

public class Main {
    public static int PORT = 6000;
    public static int MAX_BYTES = 4096;
    public String command = "IDLE";

    private boolean doSay = false;

    public static void main(String[] args) throws IOException {
        Main help = new Main();
        help.Server();
    }

    private void Server() throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Main.PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Server Started. Listening for Clients on port " + Main.PORT + "...");
        byte[] buffer = new byte[Main.MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                break;
            }
            InetAddress IPAddress = packet.getAddress();
            int port = packet.getPort();
            String clientMessage = new String(packet.getData(),0,packet.getLength());
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