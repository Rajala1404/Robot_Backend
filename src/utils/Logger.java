package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static utils.ConsoleColors.*;

public class Logger {
    private File logFile;
    private PrintWriter printWriter;
    public static void debug(String s) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(timestamp);
        String callClass = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        String out = "[" + formattedTimestamp + "] " + "[" + callClass + "] " + "[DEBUG] " + s;
        System.out.println(GREEN + out + RESET);
        new Logger().writeLog(out);
    }
    public static void info(String s) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(timestamp);
        String callClass = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        String out = "[" + formattedTimestamp + "] " + "[" + callClass + "] " + "[INFO] " + s;
        System.out.println(GREEN + out + RESET);
        new Logger().writeLog(out);
    }
    public static void warning(String s) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(timestamp);
        String callClass = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        String out = "[" + formattedTimestamp + "] " + "[" + callClass + "] " + "[WARN] " + s;
        System.out.println(GREEN + out + RESET);
        new Logger().writeLog(out);
    }
    public static void error(String s) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(timestamp);
        String callClass = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        String out = "[" + formattedTimestamp + "] " + "[" + callClass + "] " + "[ERROR] " + s;
        System.out.println(GREEN + out + RESET);
        new Logger().writeLog(out);
    }
    public static void fatal(String s) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = sdf.format(timestamp);
        String callClass = Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName();
        String out = "[" + formattedTimestamp + "] " + "[" + callClass + "] " + "[FATAL] " + s;
        System.out.println(GREEN + out + RESET);
        new Logger().writeLog(out);
    }

    public static void start() {
    }

    public void createLogFile() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String formattedTimestamp = sdf.format(timestamp);
        String fileName = "logs/" + formattedTimestamp + ".log";
        try {
            logFile = new File(fileName);
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            logFile.createNewFile();
            printWriter = new PrintWriter(logFile);
            printWriter.write("Log Started at: " + timestamp + "\n");
            printWriter.close();
            File latestLogFile = new File("/tmp/RoboterBackend/latest.log");
            if (!latestLogFile.getParentFile().exists()) {
                latestLogFile.getParentFile().mkdirs();
            }
            latestLogFile.createNewFile();
            printWriter = new PrintWriter(latestLogFile);
            printWriter.write(fileName);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void writeLog(String s) {
        String fileName = "";
        try {
            fileName = Files.readString(Paths.get("/tmp/RoboterBackend/latest.log"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logFile = new File(fileName);
        try {
            printWriter = new PrintWriter(logFile);
            printWriter.append(s);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
