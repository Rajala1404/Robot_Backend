package main;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

import com.pi4j.Pi4J;
import com.pi4j.io.serial.*;
import utils.DisplayAnimations;
import utils.Logger;
import utils.SerialHandler;

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
    public String command = "MSS";
    public InetAddress lastClientIP = InetAddress.getByName("0.0.0.0");
    public int lastClientPort = 0;
    public List<InetAddress> trustedDevices = new ArrayList<>();
    private File logFile;

    public boolean booting = true;

    public Serial getSerial() {
        return serial;
    }

    public Serial serial;

    public Main() throws Exception {}

    public static void main(String[] args) throws Exception {
        boot();
    }

    private static void boot() throws Exception {
        Main main = new Main();
        DisplayAnimations display = new DisplayAnimations();
        var serial = main.serial;
        SerialHandler serialHandler = new SerialHandler();
        new Logger().createLogFile();
        Logger.info("Backend Version: 0.1.1");
        var pi4j = Pi4J.newAutoContext();
        Logger.info("Trying to create Serial...");
        serial = pi4j.create(Serial.newConfigBuilder(pi4j)
                .use_115200_N81()
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .id("my-serial")
                .device("UART1")
                .provider("pigpio-serial")
                .device("/dev/ttyS0")
                .build());
        serial.open();
        main.serial = serial;
        Logger.info("Serial created");
        Logger.info("Waiting for opening of Serial");
        while (!serial.isOpen()) {
            Thread.sleep(250);
        }
        Logger.info("Serial opened.");
        Logger.info("Starting Serial reader...");
        serialHandler.receive(serial);
        Logger.info("Successfully started Serial reader.");
        Logger.info("Trying Handshake with Controller...");
        serial.write("S");
        display.BootAnimation(main);
        main.displayText(2, "................");
        Thread.sleep(200);
        main.displayText(2, "#...............");
        Thread.sleep(200);
        main.displayText(2, "##..............");
        Thread.sleep(200);
        main.displayText(2, "###.............");
        Thread.sleep(200);
        main.displayText(2, "####............");
        Thread.sleep(200);
        main.displayText(2, "######..........");
        Thread.sleep(200);
        main.displayText(2, "########........");
        Thread.sleep(200);
        main.displayText(2, "#########.......");
        Thread.sleep(200);
        main.displayText(2, "##########......");
        Thread.sleep(200);
        main.displayText(2, "###########.....");
        Thread.sleep(200);
        main.displayText(2, "############....");
        Thread.sleep(200);
        main.displayText(2, "#############...");
        Thread.sleep(200);
        main.displayText(2, "##############..");
        Thread.sleep(200);
        main.displayText(2, "###############.");
        Thread.sleep(500);
        Logger.info("Trying to start server...");
        for (int i = 0; i < 9; i++) {
            try {
                main.server();
                Logger.info("Server started successfully!");
                break;
            } catch (Exception e) {
                if (i < 8) {
                    Logger.fatal("Failed to start server! Trying again...");
                    Logger.error("Stacktrace: " + e.getMessage());
                } else {
                    Logger.fatal("Failed to start server! RESET & REBOOT!");
                    Logger.error("Stacktrace: " + e.getMessage());
                    // TODO: Add Reset Function
                }
            }
        }
        main.displayText(2, "################");
        main.booting = false;
        main.displayText(1, "DONE!");
        Thread.sleep(200);
        main.displayText(1,"     Ready!     ");
        main.displayText(2,"Version    0.1.1");
    }

    private void server() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(Main.PORT);
                } catch (SocketException e) {
                    Logger.error("Failed to start server!\nStacktrace: " + e.getMessage());
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
                    if (!clientMessage.equals("CONNECTED")) Logger.info("[IP: " + lastClientIP + " | Port: " + lastClientPort +"] " + clientMessage);

                    Controller(lastClientIP);
                }
                if (socket.isBound()) socket.close();
            }
        });
        if(!thread.isAlive()) thread.start();
    }

    private void Controller(InetAddress ip){
        final String finalCommand = command;
        if (finalCommand.equals("TRUST")) connectionTrust(ip);

        if (trustedDevices.contains(ip)) {
            switch (finalCommand) {
                case "MFF" -> moveFF();
                case "MFR" -> moveFR();
                case "MFL" -> moveFL();
                case "MBB" -> moveBB();
                case "MBR" -> moveBR();
                case "MBL" -> moveBL();
                case "MRR" -> moveRR();
                case "MLL" -> moveLL();
                case "MTR" -> moveTR();
                case "MTL" -> moveTL();
                case "MSS" -> moveSS();
                case "CONNECT" -> startConnection(ip);
                case "CONNECTED" -> connectionTest(ip);
                case "REBOOT_NOW" -> rebootNow();
                case "REBOOT_S" -> rebootBySeconds(finalCommand);
                default -> {
                    if (finalCommand.startsWith("SAY")) say(finalCommand);
                    else Logger.warning("Command: " + finalCommand + " IS NOT VALID!");
                }
            }
        }
    }

    private void moveFF() {
        send("D[ff]");
    }

    private void moveFR() {
        send("D[fr]");
    }

    private void moveFL() {
        send("D[fl]");
    }

    private void moveBB() {
        send("D[bb]");
    }

    private void moveBR() {
        send("D[br]");
    }

    private void moveBL() {
        send("D[bl]");
    }

    private void moveRR() {
        send("D[rr]");
    }

    private void moveLL() {
        send("D[ll]");
    }

    private void moveTR() {
        send("D[tr]");
    }

    private void moveTL() {
        send("D[tl]");
    }

    private void moveSS() {
        send("D[ss]");
    }

    private void say(String command) {
        displayText(1, "Saying:");
        displayText(2, command.substring(3));
    }

    private void rebootNow() {
        try  {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("reboot now");
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                Logger.info("Now Rebooting");
            } else {
                Logger.error("Failed to Reboot! Exit Code: " + exitCode);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            Logger.error(Arrays.toString(e.getStackTrace()));
            Logger.error("Failed to Reboot!");
        }
    }

    private void rebootBySeconds(String command) {
        try  {
            Runtime r = Runtime.getRuntime();
            int seconds = 1;
            Process p = r.exec("reboot " + seconds);
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                Logger.info("Now Rebooting...");
                serial.write("R");
            } else {
                Logger.error("Failed to Reboot! Exit Code: " + exitCode);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            Logger.error(Arrays.toString(e.getStackTrace()));
            Logger.error("Failed to Reboot!");
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
                    Logger.error(e.getMessage());
                    Logger.error(Arrays.toString(e.getStackTrace()));
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
                    Logger.debug("Sending Ack to: " + ip + ":" + PORT_SEND_TRUST);
                    sendPacket = new DatagramPacket(sendData, sendData.length, ip, PORT_SEND_TRUST);
                    Logger.debug("Sent Ack!");
                    clientSocket.send(sendPacket);
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                    Logger.error(Arrays.toString(e.getStackTrace()));
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
                        Logger.error(e.getMessage());
                        Logger.error(Arrays.toString(e.getStackTrace()));
                        return;
                    }
                    try {
                        String string = "connected";
                        sendData = string.getBytes();
                        sendPacket = new DatagramPacket(sendData, sendData.length, ip, PORT_SEND_TEST);
                        clientSocket.send(sendPacket);
                    } catch (Exception e) {
                        Logger.error(e.getMessage());
                        Logger.error(Arrays.toString(e.getStackTrace()));
                    } finally {
                        if (clientSocket.isBound()) clientSocket.close();
                    }
                }
            }
        });
        if (!(thread.isAlive())) thread.start();
    }

    public void displayText(int line, String text) {
        int maxLength = 16;
        int maxCLength = 20;
        StringBuilder truncatedText = new StringBuilder();
        truncatedText.append("T[").append(line).append(";").append(text, 0, Math.min(text.length(), maxLength));
        truncatedText.append(" ".repeat(maxCLength - truncatedText.length()));
        truncatedText.append("]");

        text = truncatedText.toString();

        Logger.debug("Displaying: " + text);

        serial.write(text);
    }

    public void send(String send) {
        serial.write(send);
    }
}