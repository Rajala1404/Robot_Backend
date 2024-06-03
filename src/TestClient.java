import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException {
        DatagramPacket sendPacket;
        byte[] sendData;
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(1000);
        Scanner input = new Scanner(System.in);
        while (true) {
            String cmd = input.nextLine();
            if (cmd.equals("QUIT")) {
                clientSocket.close();
                System.exit(1);
            }
            sendData = cmd.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("192.168.0.237"), 6000);
            clientSocket.send(sendPacket);
        }
    }
}
