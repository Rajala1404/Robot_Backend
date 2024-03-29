import java.io.File;
import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;

import utils.Logger;

public class Main {
    /**
     * Ports:
     * Default Port (for receiving): 6000
     * Trust Send Port: 6001
     * Test Send Port: 6002
     */
    public static int PORT = 6000;
    public static int PORT_SEND_TRUST = 6001;
    public static int PORT_SEND_TEST = 6002;
    public static int MAX_BYTES = 4096;
    public String command = "IDLE";
    public InetAddress lastClientIP = InetAddress.getByName("0.0.0.0");
    public Integer lastClientPort = 0;
    public List<InetAddress> trustedDevices = new ArrayList<>();

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    private File logFile;

    private boolean doSay = false;

    public Main() throws Exception {

    }

    public static void main(String[] args) throws Exception {
        new Logger().createLogFile();
        Main help = new Main();
        help.server();
    }

    private void server() throws IOException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Main.PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        Logger.info("Server started. Listening for Clients on port " + Main.PORT + "...");
        byte[] buffer = new byte[Main.MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                break;
            }
            lastClientIP = packet.getAddress();
            lastClientPort = packet.getPort();
            String clientMessage = new String(packet.getData(),0,packet.getLength());
            command = clientMessage;
            Logger.info("[IP: " + lastClientIP + " | Port: " + lastClientPort +"] " + clientMessage);
            Controller(lastClientIP);
        }
    }

    private void Controller(InetAddress ip){

        if (doSay) {
            say();
            return;
        }

        switch (command) {
            case "TRUST" -> connectionTrust(ip);
            case "CONNECT" -> startConnection(ip);
            case "CONNECTED" -> connectionTest(ip);
            case "IDLE" -> idle();
            case "FORWARD" -> forward();
            case "LEFT" -> left();
            case "RIGHT" -> right();
            case "BACKWARDS" -> backwards();
            case "SAY" -> say();
            default -> Logger.warning("Command: " + command + " IS NOT VALID!");
        }
    }

    private void connectionTrust(InetAddress ip) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean trust = trustedDevices.contains(ip);
                if (!trust) {
                    Scanner input = new Scanner(System.in);
                    while (true) {
                        System.out.print("Do you want to trust this IP? (Y/N): '" + ip + "': ");
                        String cmd = input.nextLine();
                        if (Objects.equals(cmd, "Y") || Objects.equals(cmd, "y")) {
                            trustedDevices.add(ip);
                            Logger.info("Now Trusting: " + ip);
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
                try {
                    String string;
                    if (trust) {
                        string = "true";
                    } else {
                        string = "false";
                    }
                    sendData = string.getBytes();
                    Logger.debug("Sending Ack to: " + ip + " at: " + PORT_SEND_TRUST);
                    sendPacket = new DatagramPacket(sendData, sendData.length, ip, PORT_SEND_TRUST);
                    Logger.debug("Sent Ack!");
                    clientSocket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (clientSocket.isBound()) clientSocket.close();
                }
            }
        });
        if (!(thread.isAlive())) thread.start();
    }

    private void startConnection(InetAddress ip) {

    }

    private void connectionTest(InetAddress ip) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (trustedDevices.contains(ip)) {
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
                    try {
                        String string = "connected";
                        sendData = string.getBytes();
                        sendPacket = new DatagramPacket(sendData, sendData.length, ip, PORT_SEND_TEST);
                        clientSocket.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (clientSocket.isBound()) clientSocket.close();
                    }
                }
            }
        });
        if (!(thread.isAlive())) thread.start();
    }

    private void idle() {
        Logger.info("I'm now Idle.");
    }

    private void forward() {
        Logger.info("I'm now going Forward.");
    }

    private void left() {
        Logger.info("I'm now turning Left.");
    }

    private void right() {
        Logger.info("I'm now turning Right.");
    }

    private void backwards() {
        Logger.info("I'm now going Backwards.");
    }

    private void say() {
        if (doSay) {
            System.out.println(command);
            doSay = false;
        } else {
            System.out.println("What do you have to say?");
            doSay = true;
        }
    }
}