package com.snoworca.fanCtrl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LmSensorsTempMonitor extends Thread {

    private volatile int interval = 1000;
    private volatile boolean isAlive = false;
    private ArrayList<OnReadListener> onReadListeners = new ArrayList<>();
    private AtomicReference<OnErrorCallback> onErrorCallbackAtomicReference = new AtomicReference<>();
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    public LmSensorsTempMonitor() {
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setOnErrorCallback(OnErrorCallback onErrorCallback) {
        onErrorCallbackAtomicReference.set(onErrorCallback);
    }

    public void addListener(OnReadListener onReadListener) {
        ReentrantReadWriteLock.WriteLock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            onReadListeners.add(onReadListener);
        } finally {
            lock.unlock();
        }
    }

    public boolean removeListener(OnReadListener onReadListener) {
        ReentrantReadWriteLock.WriteLock lock = readWriteLock.writeLock();
        lock.lock();
        try {
            return onReadListeners.remove(onReadListener);
        } finally {
            lock.unlock();
        }
    }


    @Override
    public synchronized void start() {
        isAlive = true;
        super.start();
    }

    public void end() {

        isAlive = false;
    }



    @Override
    public void run() {
        Pattern pattern = Pattern.compile("[+][0-9]+([.][0-9]{1,})?°C");
        while(isAlive && !isInterrupted()) {
            try {
                String result = Systool.exec("sensors");
                float maxTemp = 0.0f;

                StringReader reader = new StringReader(result);
                BufferedReader read = new BufferedReader(reader);
                String line = null;
                while ((line = read.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        String strTemp = matcher.group();
                        strTemp = strTemp.replace("+", "").replace("°C", "");
                        float temp = Float.valueOf(strTemp);
                        maxTemp = Math.max(temp, maxTemp);
                    }
                }
                broadcastTemp(maxTemp);
                Thread.sleep(interval);
            }
            catch (Exception e) {
                e.printStackTrace();
                isAlive = false;
                OnErrorCallback onErrorCallback = onErrorCallbackAtomicReference.get();
                if(onErrorCallback != null) {
                    onErrorCallback.onErrorCallback(e);
                }
            }
        }
    }

    private void broadcastTemp(float temp) {
        ReentrantReadWriteLock.ReadLock lock = readWriteLock.readLock();
        lock.lock();
        try {
            onReadListeners.forEach(listener -> listener.onReadTemperature(temp));
        } finally {
            lock.unlock();
        }


    }


    public interface OnErrorCallback {
        public void onErrorCallback(Exception e);
    }

    public interface OnReadListener {
        public void onReadTemperature(float temp);
    }


}
