package Model.Container;

import Controller.DefaultGUIController;
import ProgrammStart.Main;
import com.jcraft.jsch.*;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Properties;

public class LogReader {
    private ListView<String> listView;
    private boolean running;
    Thread thread;
    private String output;


    public LogReader(ListView<String> listView) {
        this.listView = listView;
    }

    public void readWindows(String pfad, String host, String user, String password) {
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                running = true;
                /*try {
                    File file = new File(pfad);
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                    long len = randomAccessFile.length();
                    for(int i = 0; i < len; i++) {
                        System.out.print((char) randomAccessFile.readByte());
                    }
                    while(true) {
                        long len_neu = randomAccessFile.length();
                        if(len_neu > len) {
                            for(int i = 0; i < len_neu - len; i++) {
                                System.out.print((char) randomAccessFile.readByte());
                            }
                        }
                        len = len_neu;
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                while (!thread.isInterrupted()) {
                    writeLine(output);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void readLinux(String pfad, String host, String user, String password) {
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                running = true;
                try {
                    String command = "tail -f -n 50 " + pfad;

                    JSch jsch = new JSch();
                    Session session = jsch.getSession(user, host, 22);
                    Properties config = new Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.setPassword(password);
                    session.connect();

                    Channel channel = session.openChannel("exec");
                    ((ChannelExec) channel).setCommand(command);
                    channel.setInputStream(null);
                    ((ChannelExec) channel).setErrStream(System.err);

                    InputStream input = channel.getInputStream();
                    channel.connect();

                    System.out.println("Channel Connected to machine " + host + " server with command: " + command);

                    try {
                        InputStreamReader inputReader = new InputStreamReader(input);
                        BufferedReader bufferedReader = new BufferedReader(inputReader);
                        String line = null;

                        while (((line = bufferedReader.readLine()) != null) && !thread.isInterrupted()) {
                            writeLine(line);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        bufferedReader.close();
                        inputReader.close();
                    } catch (Exception e) {
                        printError(listView);
                    }
                    channel.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    printError(listView);
                }
            }
        });
    }

    private void printError(ListView<String> listView) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add("Es konnte keine Verbindung hergestellt werden!\n" +
                        "Bitte überprüfen Sie die Verbindungsdaten!");
            }
        });
        stop();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void writeLine(String line) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add(line);
            }
        });
    }

    public void stop() {
        running = false;
        thread.interrupt();
        thread.stop();
    }

    public ListView<String> getListView() {
        return listView;
    }
    public void setListView(ListView<String> listView) {
        this.listView = listView;
    }
    public void setOutput(String output) {
        this.output = output;
    }
}
