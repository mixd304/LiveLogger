package Model.Container;

import ProgrammStart.Main;
import com.jcraft.jsch.*;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Properties;

public class LogReader {
    public LogReader() {
    }

    public static void readLinux(ListView<String> textArea, String pfad, String host, String user, String password) {
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

                while ((line = bufferedReader.readLine()) != null) {
                    textArea.getItems().add(line + "\n");
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

    public static void readWindows(String pfad, String host, String user, String password) {

        //Auslesen der Datei
        try {
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
        }
    }

    public static void testAppend(ListView<String> textArea) {
        textArea.getItems().add("TestAPPEND");
        textArea.getItems().add("TestAPPEND");
    }
}
