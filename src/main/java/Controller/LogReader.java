package Controller;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.Properties;

public class LogReader {
    public LogReader() {
    }

    public void readLinux(String pfad, String host, String user, String password) {
        try {
            //String command = "tail -f -n 2000 /opt/custom/tomcat/base/logs/catalina.out";
            String command = "tail -f -n 2000 " + pfad;
            host = "10.4.245.16";
            user = "root";
            password = "start-1234";

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
                    System.out.println(line);
                }
                bufferedReader.close();
                inputReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void readWindows(String pfad, String host, String user, String password) {

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
}
