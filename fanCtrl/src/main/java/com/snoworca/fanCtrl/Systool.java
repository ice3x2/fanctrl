package com.snoworca.fanCtrl;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Systool {

    private static long PID = -1;
    private static String Hostname = null;

    private static Executor RunProcExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        AtomicInteger counter = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("Process-Executor#" + counter.incrementAndGet() + "");
            return t;
        }
    });




    public static String exec(String cmd, int timeout) {
        CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        RunProcExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String output;
                try {
                   output = exec(cmd);
                } catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    e.printStackTrace(ps);
                    output = baos.toString();
                }
                result.set(output);
                latch.countDown();
            }
        });


        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result.get();
    }

    public static String exec(String cmd) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Process proc = Runtime.getRuntime().exec(cmd);
        InputStream stream = proc.getInputStream();
        OutputStream outStream = proc.getOutputStream();
        InputStream errorStream = proc.getErrorStream();

        byte[] buffer = new byte[1024];
        int cnt = 0;
        if(stream != null) {
            while ((cnt = stream.read(buffer)) > 0) {
                baos.write(buffer, 0, cnt);
            }
        }
        if(errorStream != null) {
            while ((cnt = errorStream.read(buffer)) > 0) {
                baos.write(buffer, 0, cnt);
            }
        }
        SafeClose.execute(stream);
        SafeClose.execute(outStream);
        SafeClose.execute(errorStream);

        return baos.toString();
    }



    public static boolean isProcessAlive(String pid) {
        try {
            String cmd = isWindows() ? "tasklist.exe" : "ps -p " + pid;
            String result = exec(cmd, 30000);
            return result.contains(" " + pid + " ") || result.contains("\t" + pid + "\t");

        } catch (Exception e) {
            return false;
        }
    }

    //


    public static boolean killProcess(String pid) {
        try {
            String cmd = isWindows() ? "taskkill.exe /pid " + pid + " /f" : "kill " + pid;
            exec(cmd, 3000);
            return isProcessAlive(pid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static long pid() {
        if(PID > 0) return PID;
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        long pid = Long.parseLong(jvmName.split("@")[0]);
        PID = pid;
        return pid;
    }


    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName.contains("win") || osName.contains("Win");
    }


    public static String hostname() {
        if(Hostname != null) return Hostname;


        try {
            String cmd = isWindows() ? "hostname.exe" : "hostname";
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStream errorStream = proc.getErrorStream();
            InputStream stream = proc.getInputStream();
            OutputStream outStream = proc.getOutputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int cnt = 0;
            while((cnt = stream.read(buffer)) > 0) {
                baos.write(buffer, 0, cnt);
            }
            try {
                stream.close();
                outStream.close();
                errorStream.close();
            } catch(Exception ignored) {}

            String hostName = baos.toString();
            hostName = hostName.trim();
            hostName = hostName.replace("\\", "").replace("/", "").replace(":", "").replace("*", "").replace("\"", "").replace("?", "").replace("<", "")
                    .replace(">", "").replace("|", "").replace("'", "").replace("$", "").replace("\f", "").replace("\b", "").replace("\n", "").replace("\r", "")
                    .replace("\t", " ");
            if(hostName.isEmpty()) {
                hostName = getHostnameFromInetAddress();
            }
            Hostname = hostName;
            return hostName;
        } catch (Exception e) {
            Hostname = getHostnameFromInetAddress();
            return Hostname;
        }
    }

    private static String getHostnameFromInetAddress() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e1) {
            return "unkownhost";
        }
    }



}
