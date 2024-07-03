package utils;

import main.Main;

import com.pi4j.io.serial.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialHandler {
    public void send(String send) throws Exception {
        Serial serial = new Main().getSerial();
        serial.open();
        serial.write(send);
        serial.close();
    }

    public void receive(Serial serial) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Serial final_Serial = serial;
                SerialReader serialReader = new SerialReader(final_Serial);
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

