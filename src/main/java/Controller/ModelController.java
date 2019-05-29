package Controller;

import Model.Ordner;
import Model.ResultBoolean;
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

public class ModelController {
    private String speicherort = "C:/Projekte/GEE/LiveLogger/ordner.json";
    private ArrayList<Ordner> ordnerList = new ArrayList<>();

    public ResultBoolean addOrdner(Ordner ordner) {
        try {
            this.ordnerList.add(ordner);

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

                System.out.println("[INFO]   Der Order mit der UUID " + uuid + " wurde erfolgreich gelöscht!");
                return new ResultBoolean(true, "Ordner gelöscht!");
            }
        }
        System.out.println("[FEHLER] Der Order mit der UUID " + uuid + " konnte nicht gelöscht werden!");
        return new ResultBoolean(false, "Fehler beim Löschen des Ordners!");
    }

    public void deleteVerbindungByUUID(Ordner ordner, UUID uuid) {
        ordner.deleteVerbindungByUUID(uuid);
    }

    public boolean safeOrdner() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Java objects to JSON file
            mapper.writeValue(new File(this.speicherort), this.ordnerList);
            System.out.println("[INFO]   Liste mit Ordnern erfolgreich gespeichert!");

            // Java objects to JSON string - compact-print
            //String jsonString = mapper.writeValueAsString(ordnerList);
            //System.out.println(jsonString);

            // Java objects to JSON string - pretty-print
            //String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ordnerList);
            //System.out.println(jsonInString2);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[Fehler] beim Speichern der Liste mit Ordnern!");
        return false;
    }

    public boolean readOrdner() {
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
}
