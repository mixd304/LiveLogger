package Model.Container;

import Model.Ordner;
import Model.ResultBoolean;
import Model.Verbindung;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModelContainer {
    private String speicherort = "ordner.json";
    private ArrayList<Ordner> ordnerList = new ArrayList<>();

    public ResultBoolean addOrdner(Ordner ordner) {
        try {
            this.ordnerList.add(ordner);
            safeOrdner();
            System.out.println("[INFO]   Neuer Ordner erfolgreich erstellt!");
            return new ResultBoolean(true, "Neuer Ordner angelegt!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[FEHLER] Ordner konnte nicht erstellt werden!");
        return new ResultBoolean(false, "Fehler beim Anlegen des Ordners!");
    }

    public ResultBoolean deleteOrdnerByUUID(UUID uuid) {
        for (Ordner ordner: ordnerList) {
            if(ordner.getUuid().equals(uuid)) {
                ordnerList.remove(ordner);

                System.out.println("[INFO]   Der Ordner mit der UUID " + uuid + " wurde erfolgreich gelöscht!");
                safeOrdner();
                return new ResultBoolean(true, "Ordner gelöscht!");
            }
        }
        System.out.println("[FEHLER] Der Ordner mit der UUID " + uuid + " konnte nicht gelöscht werden!");
        safeOrdner();
        return new ResultBoolean(false, "Fehler beim Löschen des Ordners!");

    }

    public void deleteVerbindungByUUID(UUID uuid) {
        for (Ordner ordner: this.ordnerList) {
            for (Verbindung verbindung: ordner.getList()) {
                if(verbindung.getUuid().equals(uuid)) {
                    ordner.deleteVerbindungByUUID(uuid);
                    break;
                }
            }
        }
        safeOrdner();
    }

    public ResultBoolean addVerbindungToOrdner(Ordner ordner, Verbindung verbindung) {
        verbindung.setUuid(UUID.randomUUID());
        for (Ordner o: this.ordnerList) {
            if(o.equals(ordner)) {
                System.out.println("Ordner = Ordner");
                o.addVerbindung(verbindung);
                return new ResultBoolean(true, "Verbindung erfolgreich angelegt!");
            }
        }
        ordner.setUuid(UUID.randomUUID());
        ordner.addVerbindung(verbindung);
        ordnerList.add(ordner);
        return new ResultBoolean(true, "Verbindung und Ordner erfolgreich angelegt!");
    }

    public Ordner getOrdnerByUUID(UUID uuid) {
        for (Ordner ordner: ordnerList) {
            if(ordner.getUuid().equals(uuid)) {
                return ordner;
            }
        }
        return null;
    }

    public Verbindung getVerbindungByUUID(UUID uuid) {
        for (Ordner ordner: this.ordnerList) {
            for (Verbindung verbindung:ordner.getList()) {
                if(verbindung.getUuid().equals(uuid)) {
                    return verbindung;
                }
            }
        }
        return null;
    }

    public boolean editVerbindung(Verbindung old_verbindung, Verbindung new_verbindung) {
        for (Ordner ordner: this.ordnerList) {
            for (Verbindung verbindung:ordner.getList()) {
                if(verbindung.equals(old_verbindung)) {
                    ordner.getList().set(ordner.getList().indexOf(old_verbindung), new_verbindung);
                    System.out.println("[INFO]   Die Verbindung mit der " + old_verbindung.getUuid() + " wurde erfolgreich editiert!");
                    return true;
                }
            }
        }
        System.out.println("[INFO]   Die Verbindung mit der " + old_verbindung.getUuid() + " konnte nicht editiert werden!");
        return false;
    }

    public Ordner getOrdnerByVerbindung(Verbindung v) {
        for (Ordner ordner: this.ordnerList) {
            for (Verbindung verbindung:ordner.getList()) {
                if(verbindung.equals(v)) {
                    return ordner;
                }
            }
        }
        return null;
    }

    public boolean safeOrdner() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Java objects to JSON file
            mapper.writeValue(new File(this.speicherort), this.ordnerList);
            System.out.println("[INFO]   Liste mit Ordnern erfolgreich gespeichert!");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[Fehler] beim Speichern der Liste mit Ordnern!");
        return false;
    }

    public boolean loadOrdner() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // JSON file to Java Objec
            this.ordnerList = mapper.readValue(new File(this.speicherort),  new TypeReference<List<Ordner>>() {});
            System.out.println("[INFO]   Liste mit Ordnern erfolgreich geladen!");

            // Ausgabe der eingelesenen Daten
            /*String ausgabe = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.ordnerList);
            System.out.println(ausgabe);*/

            return true;
        } catch (FileNotFoundException e) {
            System.out.println("[INFO]   Liste mit Ordnern existiert nicht!");
        } catch (JsonParseException e) {
            System.out.println("[Fehler] beim Laden der Liste mit Ordnern!");
            e.printStackTrace();
        } catch (JsonMappingException e) {
            System.out.println("[Fehler] beim Laden der Liste mit Ordnern!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[Fehler] beim Laden der Liste mit Ordnern!");
            e.printStackTrace();
        }
        return false;
    }

    public String getSpeicherort() {
        return speicherort;
    }

    public void setSpeicherort(String speicherort) {
        this.speicherort = speicherort;
    }

    public ArrayList<Ordner> getOrdnerList() {
        return ordnerList;
    }

    public void setOrdnerList(ArrayList<Ordner> ordnerList) {
        this.ordnerList = ordnerList;
    }

    public void print() {
        for (Ordner ordner: this.ordnerList) {
            System.out.println("Ordner - Bezeichnung: " + ordner.getBezeichnung());
            System.out.println("Ordner - UUID:        " + ordner.getUuid());
            for (Verbindung verbindung: ordner.getList()) {
                System.out.println("  Verb - Bezeichnung: " + verbindung.getBezeichnung());
                System.out.println("  Verb - UUID:        " + verbindung.getUuid());
            }
        }
    }
}
