package Model.Data;

import Default.ResultBoolean;

import java.util.ArrayList;
import java.util.UUID;

public class Ordner {
    private UUID uuid;
    private String bezeichnung;
    private ArrayList<Verbindung> verbindungsList = new ArrayList<Verbindung>();

    public Ordner() {
    }

    public Ordner(String bezeichnung) {
    }

    public ResultBoolean addVerbindung(Verbindung verbindung) {
        if(this.verbindungsList.add(verbindung)) {
            System.out.println("[INFO]   Die Verbindung wurde erfolgreich zum Ordner " + this.bezeichnung + " hinzugefügt");
            return new ResultBoolean(true, "Neue Verbindung erstellt!");
        }
        else {
            System.out.println("[FEHLER] Die Verbindung konnte nicht zum Ordner " + this.bezeichnung + "hinzugefügt werden");
            return new ResultBoolean(false, "Fehler beim Erstellen der Verbindung!");
        }
    }

    public ResultBoolean deleteVerbindungByUUID(UUID uuid) {
        for (Verbindung verbindung: verbindungsList) {
            if(verbindung.getUuid().equals(uuid)) {
                verbindungsList.remove(verbindung);

                System.out.println("[INFO]   Die Verbindung mit der UUID " + uuid + " wurde erfolgreich aus dem Ordner " + this.bezeichnung + " gelöscht");
                return new ResultBoolean(true, "Verbindung gelöscht!");
            }
        }
        System.out.println("[FEHLER] Die Verbindung mit der UUID " + uuid + " konnte nicht aus dem Ordner " + this.bezeichnung + " gelöscht werden");
        return new ResultBoolean(false, "Fehler beim Löschen der Verbindung!");
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ArrayList<Verbindung> getList() {
        return verbindungsList;
    }

    public void setList(ArrayList<Verbindung> list) {
        this.verbindungsList = list;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public String toString() {
        return this.getBezeichnung();
    }

    public void print() {
        System.out.println("Ordner{" +
                "uuid=" + uuid +
                ", bezeichnung='" + bezeichnung + '\'' +
                ", verbindungsList=" + verbindungsList +
                '}');
    }
}
