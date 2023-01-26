package com.snoworca.fanCtrl;

import java.io.File;
import java.io.IOException;

public class FanCtrl implements LmSensorsTempMonitor.OnErrorCallback, LmSensorsTempMonitor.OnReadListener {
    private Config config;
    private PinCtrl pinCtrl;
    private LmSensorsTempMonitor tempMonitor;
    private SimpleLogger logger;

    public FanCtrl() {

    }

    public void start() throws IOException {
        config = new Config();
        config.read();
        pinCtrl = new PinCtrl(config.getPinNumber());
        logger = SimpleLogger.create(new File(config.getLogFile()));
        logger.write("info", "Start FanCtrl.");
        logger.write("info", config.toString());
        if(logger.isError()) {
            logger.write("error", "Log file could not be written.");
        }
        tempMonitor = new LmSensorsTempMonitor();
        tempMonitor.setInterval(config.getInterval());
        tempMonitor.setOnErrorCallback(this);
        tempMonitor.addListener(this);
        tempMonitor.start();

    }


    @Override
    public void onErrorCallback(Exception e) {
        logger.write("error", "Failed to get temperature.", e);
    }

    @Override
    public void onReadTemperature(float temp) {
        if(temp > config.getFanOnTemp() && !pinCtrl.isOn()) {
            pinCtrl.on();
            logger.write("info", "Fan operating temperature criteria exceeded.(" + temp + ">" + config.getFanOnTemp() +  ") Run the fan.");
        }
        else if(temp < config.getFanOffTemp() && pinCtrl.isOn()) {
            pinCtrl.off();
            logger.write("info", "The fan stop temperature has fallen below the criterion.(" + temp + "<" + config.getFanOffTemp() +  ") Stop the fan.");
        }
    }
}
