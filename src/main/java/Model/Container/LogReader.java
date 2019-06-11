package Model.Container;

import com.jcraft.jsch.*;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Properties;

public class LogReader {
    private ListView<String> listView;
    Thread thread;

    public LogReader(ListView<String> listView) {
        this.listView = listView;
        thread = Thread.currentThread();
    }

    public void readLinux(String pfad, String host, String user, String password) {
        try {
            System.out.println("COUNT = " + Thread.activeCount());
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

                while (((line = bufferedReader.readLine()) != null)) {
                    this.writeLine(line);
                    Thread.sleep(2);
                }
                bufferedReader.close();
                inputReader.close();
            } catch (IllegalStateException e1) {
                System.out.println("IllegalStateException");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("TEST");
            }

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e1) {
            if(e1.getCause().getMessage().contains("Connection timed out: connect")) {
                System.out.println("Auth fail - Falscher Host");
            } else if(e1.getCause().getMessage().contains("Auth fail")) {
                System.out.println("Auth fail - Falscher Benutzername");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readWindows(String pfad, String host, String user, String password) {
        //Auslesen der Datei
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
        while (true) {
            try {
                this.writeLine("Windows Log Zeile");
                if(!Thread.interrupted()) {
                    Thread.sleep(100);
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeLine(String line) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add(line);
            }
        });
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public ListView<String> getListView() {
        return listView;
    }
    public void setListView(ListView<String> listView) {
        this.listView = listView;
    }
}
