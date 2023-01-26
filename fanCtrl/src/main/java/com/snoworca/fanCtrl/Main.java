package com.snoworca.fanCtrl;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        args = new String[]{"run"};
        boolean existArgs = args != null && args.length > 0;

        if(existArgs && "stop".equalsIgnoreCase(args[0])) {
            oldProcessKill();
        }
        else if(existArgs && ("start".equalsIgnoreCase(args[0]) || "run".equalsIgnoreCase(args[0]) ) ) {
            oldProcessKillAndCreatePidFile();
        } else {
            System.out.println("fanctrl.sh [start|run|stop]");
            System.exit(1);
            return;
        }

        FanCtrl fanCtrl = new FanCtrl();
        fanCtrl.start();
    }

    public static void oldProcessKill() throws IOException {
        File file = new File(".pid");
        if (file.exists()) {
            String pid = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if(Systool.isProcessAlive(pid)) {
                System.out.println("Process is alive. pid: " + pid);
                while (!Systool.killProcess(pid)) {
                    try {
                        System.out.println("Process is alive. pid: " + pid);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Process is killed. pid: " + pid);
            }
        }
        file.delete();
    }

    public static void oldProcessKillAndCreatePidFile() throws IOException {
        oldProcessKill();
        File file = new File(".pid");
        file.delete();
        Files.write(file.toPath(), (Systool.pid() + "").getBytes());
        System.out.println("New process pid: " + Systool.pid());
    }

}