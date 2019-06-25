package Model.Container;

import Controller.DefaultPage_Controller;
import Model.Data.Verbindung;
import ProgrammStart.StartProgramm;
import com.jcraft.jsch.*;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.io.*;
import java.util.Properties;

public class LogReader {
    private ListView<String> listView;
    private boolean running;
    private Thread thread;
    private String output;
    private String passwort;

    /**
     * Konstruktor
     */
    public LogReader() {

    }

    /**
     * Konstruktor
     * @param listView Verweis auf das zu beschreibende ListView (Textfeld) der Oberfläche
     */
    public LogReader(ListView<String> listView) {
        this.listView = listView;
    }

    /**
     * Öffnet einen neuen Thread, welcher parallel zum JavaFX-Thread läuft
     * Stellt eine Verbindung zu einem Linux-Server her
     * Liest sein Logfile zeilenweise in Echtzeit aus und liefert diese Zeilen an die Oberfläche
     * @param verbindung herzustellende Verbindung
     * @see #writeLine(String)
     */
    public void readWindows(Verbindung verbindung) {
        StartProgramm.executor.execute(new Runnable() {
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

    /**
     * Öffnet einen neuen Thread, welcher parallel zum JavaFX-Thread läuft
     * Stellt eine Verbindung zu einem Linux-Server her
     * Liest sein Logfile zeilenweise in Echtzeit aus und liefert diese Zeilen an die Oberfläche
     * @param verbindung herzustellende Verbindung
     * @see #writeLine(String)
     */
    public void readLinux(Verbindung verbindung) {
        StartProgramm.executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                running = true;
                try {
                    //String command = "tail -f -n 200 " + verbindung.getLogpath();
                    String command = verbindung.getLogpath();

                    JSch jsch = new JSch();
                    Session session;
                    Channel channel;
                    if(verbindung.getKeyfile() != "" && verbindung.getKeyfile() != null) {
                        jsch.addIdentity(verbindung.getKeyfile(), verbindung.getPasswort());
                        session = jsch.getSession(verbindung.getBenutzername(), verbindung.getHost(), verbindung.getPort());
                        session.setConfig("PreferredAuthentications", "publickey,gssapi-with-mic,keyboard-interactive,password");

                        Properties config = new Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.connect();

                        // sudo -u tomcat tail -f -n 500 /opt/as/tomcat/base/logs/catalina.out
                        channel = session.openChannel("exec");
                        ((ChannelExec) channel).setCommand(command);
                    } else {
                        session = jsch.getSession(verbindung.getBenutzername(), verbindung.getHost(), verbindung.getPort());
                        session.setPassword(passwort);

                        Properties config = new Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.connect();
                        channel = session.openChannel("exec");
                        ((ChannelExec) channel).setCommand(command);
                    }

                    channel.setInputStream(null);
                    ((ChannelExec) channel).setErrStream(System.err);

                    InputStream input = channel.getInputStream();
                    channel.connect();

                    System.out.println("Channel Connected to machine " + verbindung.getHost() + " server with command: " + command);

                    try {
                        InputStreamReader inputReader = new InputStreamReader(input);
                        BufferedReader bufferedReader = new BufferedReader(inputReader);
                        String line = null;

                        if(bufferedReader.readLine() == null){
                            printError("Logpfad ungültig");
                        } else {
                            System.out.println("Ausgabenstart");
                            System.out.println("THREAD Interrupted = " + thread.isInterrupted());
                            while (((line = bufferedReader.readLine()) != null) && !thread.isInterrupted()) {
                                System.out.println("Line = " + line);
                                writeLine(line);
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        bufferedReader.close();
                        inputReader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        printError("unbekannter Fehler - Fehlerquelle 1");
                    }
                    channel.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    printError("unbekannter Fehler - Fehlerquelle 2");
                }
            }
        });
    }

    /**
     * Schreibt eine Fehlerzeile in die Oberfläche, wenn keine Verbindung hergestellt werden konnte
     * @param fehler Fehlerhinweise, z.B. falscher Port oder falsches Passwort
     */
    private void printError(String fehler) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add("Es konnte keine Verbindung zum Server hergestellt werden!" +
                        "\nFehlerhinweis: " + fehler +
                        "\nBitte überprüfen Sie die Verbindungsdaten!");
            }
        });
        stop();
    }

    /**
     * Schreibt eine Zeile in das entsprechende Listview der Oberfläche
     * @param line zu schreibende Zeile
     */
    private void writeLine(String line) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add(line);
            }
        });
    }

    /**
     * Stoppt den Thread und somit die Verbindung zum Server und das Auslesen des Logfiles
     */
    public void stop() {
        running = false;
        thread.interrupt();
        thread.stop();
    }
    public void setListView(ListView<String> listView) {
        this.listView = listView;
    }
    public void setOutput(String output) {
        this.output = output;
    }

    public void startReading(Verbindung verbindung) {
        if(!verbindung.isSafePasswort()) {
            this.passwort = DefaultPage_Controller.passwörter.get(verbindung);
        } else {
            this.passwort = verbindung.getPasswort();
        }

        if(verbindung.getBetriebssystem().equals("Linux")) {
            readLinux(verbindung);
        } else if(verbindung.getBetriebssystem().equals("Windows")) {
            readWindows(verbindung);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listView.getItems().add("Ausgewähltes Betriebssystem wird (noch) nicht unterstützt!");
                }
            });
        }
    }
}
