package Model;

import java.util.UUID;

public class Verbindung {
    private UUID uuid;
    private String bezeichnung;
    private String host;
    private int port;
    private String benutzername;
    private String passwort;
    private boolean safePasswort;
    private String keyfile;
    private String logpath;
    private String betriebssystem;

    public Verbindung() {

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBenutzername() {
        return benutzername;
    }

    public void setBenutzername(String benutzername) {
        this.benutzername = benutzername;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBetriebssystem() {
        return betriebssystem;
    }

    public void setBetriebssystem(String betriebssystem) {
        this.betriebssystem = betriebssystem;
    }

    public boolean isSafePasswort() {
        return safePasswort;
    }

    public void setSafePasswort(boolean safePasswort) {
        this.safePasswort = safePasswort;
    }

    public String getKeyfile() {
        return keyfile;
    }

    public void setKeyfile(String keyfile) {
        this.keyfile = keyfile;
    }

    @Override
    public String toString() {
        return this.getBezeichnung();
    }

    public void print() {
        System.out.println("Verbindung{" +
                "uuid=" + uuid +
                ", bezeichnung='" + bezeichnung + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", benutzername='" + benutzername + '\'' +
                ", passwort='" + passwort + '\'' +
                ", safePasswort=" + safePasswort +
                ", logpath='" + logpath + '\'' +
                ", betriebssystem='" + betriebssystem + '\'' +
                '}');
    }
}
