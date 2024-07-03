package utils;

import com.pi4j.io.serial.Serial;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialReader implements Runnable {
    private final Serial serial;

    private boolean continueReading = true;

    public SerialReader(Serial serial) {
        this.serial = serial;
    }

    public void stopReading() {
        continueReading = false;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));
        try {
            String line = "";
            while (continueReading) {
                var available = serial.available();
                if (available > 0) {
                    for (int i = 0; i < available; i++) {
                        byte b = (byte) br.read();
                        if (b < 32) {
                            if (!line.isEmpty()) {
                                Logger.info("Data: '" + line + "'");
                                line = "";
                            }
                        } else {
                            line += (char) b;
                        }
                    }
                } else {
                    Thread.sleep(10);
                }
            }
        } catch (Exception e) {
            Logger.error("Error reading data from serial: " + e.getMessage());
        }
    }
}
