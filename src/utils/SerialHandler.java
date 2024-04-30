package utils;

import main.Main;

import com.pi4j.io.serial.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialHandler {
    public void send(String sendString) throws Exception {
        Serial serial = new Main().serial;
        serial.write(sendString);
    }
    public void receive(Serial serial) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SerialReader serialReader = new SerialReader(serial);
                Thread serialReaderThread = new Thread(serialReader, "SerialReader");
                serialReaderThread.setDaemon(true);
                serialReaderThread.start();

                while (serial.isOpen()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                serialReader.stopReading();
            }
        });
    }
}

