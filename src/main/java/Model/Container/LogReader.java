package Model.Container;

import com.jcraft.jsch.*;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Properties;

public class LogReader {
    private ListView<String> listView;
    boolean running;

    public LogReader(ListView<String> listView) {
        this.listView = listView;
    }

    public void readLinux(String pfad, String host, String user, String password) {
        running = true;
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

                System.out.println(running);
                while (((line = bufferedReader.readLine()) != null) && running) {

                    this.listView.getItems().add(line + "\n");
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
        running = true;
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
        while (running) {
            try {
                this.listView.getItems().add("Windows Log Zeile");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.running = false;
        Thread.currentThread().interrupt();
        System.out.println("LogReader gestoppt");
    }

    public void testAppend() {
        this.listView.getItems().add("TestAPPEND");
        this.listView.getItems().add("TestAPPEND");
    }

    public ListView<String> getListView() {
        return listView;
    }
    public void setListView(ListView<String> listView) {
        this.listView = listView;
    }
}
