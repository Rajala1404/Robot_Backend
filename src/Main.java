import java.io.IOException;
import java.net.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

public class Main {
    public static int PORT = 6000;
    public static int MAX_BYTES = 4096;
    public String command = "IDLE";
    public InetAddress lastCIP = InetAddress.getByName("0.0.0.0");
    public Integer lastCPort = 0;
    public List<InetAddress> trustedDevices = new ArrayList<>();

    private boolean doSay = false;

    public Main() throws Exception {
    }

    public static void main(String[] args) throws Exception {
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
            lastCIP = packet.getAddress();
            lastCPort = packet.getPort();
            String clientMessage = new String(packet.getData(), 0, packet.getLength());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            command = clientMessage;
            System.out.println("[" + timestamp.toString() + "]" + " [IP: " + lastCIP + " | Port: " + lastCPort + "] " + clientMessage);
            Controller();
        }
    }

    private void Controller() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        if (doSay) {
            say(timestamp);
            return;
        }

        switch (command) {
            case "CONNECTED" -> connectionTest();
            case "TRUST" -> connectionTrust();
            case "IDLE" -> idle(timestamp);
            case "FORWARD" -> forward(timestamp);
            case "LEFT" -> left(timestamp);
            case "RIGHT" -> right(timestamp);
            case "BACKWARDS" -> backwards(timestamp);
            case "SAY" -> say(timestamp);
            default -> System.out.println("[" + timestamp.toString() + "] " + "NOT VALID!");
        }
    }

    private void connectionTrust() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean trust = false;
                InetAddress ip = lastCIP;
                if (trustedDevices.contains(ip)) trust = true;
                if (!trust) {
                    Scanner input = new Scanner(System.in);
                    while (true) {
                        System.out.println("Do you want to trust this IP? (Y/N): '" + ip + "'");
                        String cmd = input.nextLine();
                        if (Objects.equals(cmd, "Y") || Objects.equals(cmd, "y")) {
                            trustedDevices.add(ip);
                            trust = true;
                            break;
                        } else if (Objects.equals(cmd, "N") || Objects.equals(cmd, "n")) {
                            break;
                        } else {
                            System.out.println("Wrong Input! Try Again");
                        }
                    }
                }
                DatagramPacket sendPacket;
                DatagramSocket clientSocket;
                byte[] sendData = new byte[0];
                try {
                    clientSocket = new DatagramSocket();
                    clientSocket.setSoTimeout(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                try  {
                    String string;
                    if (trust) {
                        string = "true";
                    } else {
                        string = "false";
                    }
                    sendData = string.getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, ip, 6000);
                    clientSocket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (!(thread.isAlive())) thread.start();
    }

    private void connectionTest() {

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