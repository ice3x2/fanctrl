package com.snoworca.fanCtrl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd/hh:mm:ss.SSS");
    private boolean isError = false;
    private File logFile;
    private FileOutputStream outputStream;

    public static SimpleLogger create(File file) {
        try {
            return new SimpleLogger(file);
        } catch (IOException e) {
            e.printStackTrace();
            SimpleLogger simpleLogger = new SimpleLogger();
            simpleLogger.isError = true;
            return simpleLogger;
        }
    }

    public boolean isError() {
        return isError;
    }

    private SimpleLogger() {}
    private SimpleLogger(File file) throws IOException {
        logFile = file;
        if(!logFile.exists()) {
            logFile.createNewFile();
        }
        outputStream = new FileOutputStream(file);
    }

    private String writeException(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(baos);
        e.printStackTrace(printWriter);
        printWriter.close();
        return baos.toString();
    }

    public void write(String level, String message) {
        write(level, message, null);
    }
    public void write(String level, String message, Exception exception) {

        StringBuilder builder = new StringBuilder();
        String strDate = DATE_FORMAT.format(new Date());
        builder.append(strDate).append("\t[").append(level.toUpperCase()).append("]\t").append(message);
        if(exception != null) {
            builder.append("\n").append(writeException(exception));
        }
        String line = builder.toString();
        byte[] buffer = line.getBytes();
        System.out.println(line);
        if(isError) return;
        try {
            outputStream.write(buffer, 0 ,buffer.length);
        } catch (IOException e) {
            isError = true;
            e.printStackTrace();
        }
    }





}
