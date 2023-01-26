package com.snoworca.fanCtrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private final static String CONFIG_FILENAME= "fanctrl.properties";
    private final static float DEFAULT_ON_TEMP = 70.0f;
    private final static float DEFAULT_OFF_TEMP = 50.0f;
    private final static int DEFAULT_PIN_NUMBER = 16;
    private final static int DEFAULT_INTERVAL = 1000;
    private final static String DEFAULT_LOG_FILE = "fanCtrl.log";

    private float fanOnTemp = DEFAULT_ON_TEMP;
    private float fanOffTemp = DEFAULT_OFF_TEMP;
    private int pinNumber = DEFAULT_PIN_NUMBER;
    private int interval = DEFAULT_INTERVAL;
    private String logFile = DEFAULT_LOG_FILE;


    public String getLogFile() {
        return logFile;
    }

    public float getFanOnTemp() {
        return fanOnTemp;
    }

    public float getFanOffTemp() {
        return fanOffTemp;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public int getInterval() {
        return interval;
    }

    public void read() {
        File file = new File(CONFIG_FILENAME);
        InputStream inputStream;
        try {
            if (!file.isFile() && !file.canRead()) {
                inputStream = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME);
            } else {
                inputStream = new FileInputStream(file);
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            String strOnTemp = properties.getProperty("fanOn", DEFAULT_ON_TEMP + "").trim();
            String strOffTemp = properties.getProperty("fanOff", DEFAULT_OFF_TEMP + "").trim();
            String strPinNumber = properties.getProperty("fanPin", DEFAULT_PIN_NUMBER + "").trim();
            String strInterval = properties.getProperty("interval", DEFAULT_INTERVAL + "").trim();
            logFile = properties.getProperty("logFile", DEFAULT_LOG_FILE + "").trim();
            fanOnTemp = toFloat(strOnTemp, DEFAULT_ON_TEMP);
            fanOffTemp = toFloat(strOffTemp, DEFAULT_OFF_TEMP);
            pinNumber = toInt(strPinNumber, DEFAULT_PIN_NUMBER);
            interval = toInt(strInterval, DEFAULT_INTERVAL);
            if(interval < DEFAULT_INTERVAL) {
                interval = DEFAULT_INTERVAL;
            }
            if(logFile.isEmpty() || !new File(logFile).canWrite()) {
                logFile = DEFAULT_LOG_FILE;
            }

        } catch (Exception e) {
            fanOnTemp = DEFAULT_ON_TEMP;
            fanOffTemp = DEFAULT_OFF_TEMP;
            interval = DEFAULT_INTERVAL;
            pinNumber = DEFAULT_PIN_NUMBER;
        }
    }

    private float toFloat(String value, float defaultValue) {
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int toInt(String value, int defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String toString() {
        return "Config [fanOnTemp=" + fanOnTemp + ", fanOffTemp=" + fanOffTemp + ", pinNumber=" + pinNumber + ", interval=" + interval + ", logFile=" + new File(logFile).getAbsolutePath() + "]";
    }
}
