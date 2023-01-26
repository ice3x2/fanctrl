package com.snoworca.fanCtrl;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class PinCtrl  {

    private int pinNumber = 16;
    private AtomicBoolean isOn = new AtomicBoolean(false);
    private Object monitor = new Object();

    public PinCtrl(int pinNumber) throws IOException {
        Systool.exec("gpio mode " + pinNumber + " out");
        this.pinNumber = pinNumber;
    }


    public void on() {
        synchronized (monitor) {
            try {
                Systool.exec("gpio write " + pinNumber + " 1");
                isOn.set(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void off() {
        synchronized (monitor) {
            try {
                Systool.exec("gpio write " + pinNumber + " 0");
                isOn.set(false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isOn() {
        return isOn.get();
    }
}
