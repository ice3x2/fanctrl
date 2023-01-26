package com.snoworca.fanCtrl;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean existArgs = args != null && args.length > 0;
        if(existArgs && "stop".equalsIgnoreCase(args[0])) {
            oldProcessKill();
            return;
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
        File file = new File(Systool.currentDirFile(), ".pid");
        if (file.exists()) {
            String pid = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if(Systool.isProcessAlive(pid)) {
                Systool.killProcess(pid);
                System.out.println("Process is killed. pid: " + pid);
            }
        }
    }

    public static void oldProcessKillAndCreatePidFile() throws IOException {
        oldProcessKill();
        File currentDir = Systool.currentDirFile();
        File file = new File(currentDir, ".pid");
        file.delete();
        Files.write(file.toPath(), (Systool.pid() + "").getBytes());
        System.out.println("New process pid: " + Systool.pid());
    }

}