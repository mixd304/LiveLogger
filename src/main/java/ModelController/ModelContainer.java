package ModelController;

import Model.Data.Ordner;
import Default.ResultBoolean;
import Model.Data.Verbindung;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ModelContainer {
    private String speicherort = "ordner.json";
    private ArrayList<Ordner> ordnerList = new ArrayList<>();
    private String keyStr = "gDePtWkw1RAShgF1dLKLi5wS2RVNd2as";

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     *
     */
    public ResultBoolean deleteVerbindungByUUID(UUID uuid) {
        for (Ordner ordner: this.ordnerList) {
            for (Verbindung verbindung: ordner.getList()) {
                if(verbindung.getUuid().equals(uuid)) {
                    ordner.deleteVerbindungByUUID(uuid);
                    System.out.println("[INFO]   Die Verbindung mit der UUID " + uuid + " wurde erfolgreich gelöscht!");
                    return new ResultBoolean(true, "Verbindung gelöscht!");
                }
            }
        }
        System.out.println("[FEHLER] Die Verbindung mit der UUID " + uuid + " konnte nicht gelöscht werden!");
        safeOrdner();
        return new ResultBoolean(false, "Fehler beim Löschen der Verbindung!");
    }

    /**
     *
     */
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

    /**
     *
     */
    public Ordner getOrdnerByUUID(UUID uuid) {
        for (Ordner ordner: ordnerList) {
            if(ordner.getUuid().equals(uuid)) {
                return ordner;
            }
        }
        return null;
    }

    /**
     *
     */
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

    /**
     *
     */
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

    /**
     *
     */
    public boolean editOrdner(Ordner old_ordner, Ordner new_ordner) {
        for(Ordner ordner: this.ordnerList) {
            if(ordner.equals(old_ordner)) {
                ordnerList.set(ordnerList.indexOf(old_ordner), new_ordner);
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
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

    /**
     *
     */
    public boolean safeOrdner() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (Ordner ordner: this.ordnerList) {
                for (Verbindung verbindung:ordner.getList()) {
                    if(verbindung.isSafePasswort()) {
                        // byte-Array erzeugen
                        byte[] key = (keyStr).getBytes("UTF-8");
                        // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SHA
                        MessageDigest sha = MessageDigest.getInstance("SHA-256");
                        key = sha.digest(key);
                        // nur die ersten 128 bit nutzen
                        key = Arrays.copyOf(key, 16);
                        // der fertige Schluessel
                        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                        byte[] encrypted = cipher.doFinal(verbindung.getPasswort().getBytes());
                        BASE64Encoder myEncoder = new BASE64Encoder();
                        verbindung.setPasswort(myEncoder.encode(encrypted));

                    } else {
                        verbindung.setPasswort("");
                    }
                }
            }

            mapper.writeValue(new File(this.speicherort), this.ordnerList);
            System.out.println("[INFO]   Liste mit Ordnern erfolgreich gespeichert!");

            loadOrdner();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[Fehler] beim Speichern der Liste mit Ordnern!");
        return false;
    }

    /**
     *
     */
    public boolean loadOrdner() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.ordnerList = mapper.readValue(new File(this.speicherort),  new TypeReference<List<Ordner>>() {});
            System.out.println("[INFO]   Liste mit Ordnern erfolgreich geladen!");
            for (Ordner ordner: this.ordnerList) {
                for (Verbindung verbindung:ordner.getList()) {
                    if(verbindung.isSafePasswort()) {
                        // byte-Array erzeugen
                        byte[] key = keyStr.getBytes("UTF-8");
                        // aus dem Array einen Hash-Wert erzeugen mit MD5 oder SH
                        MessageDigest sha = MessageDigest.getInstance("SHA-256");
                        key = sha.digest(key);
                        // nur die ersten 128 bit nutzen
                        key = Arrays.copyOf(key, 16);
                        // der fertige Schluessel
                        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

                        // BASE64 String zu Byte-Array konvertieren
                        BASE64Decoder myDecoder = new BASE64Decoder();
                        byte[] crypted = myDecoder.decodeBuffer(verbindung.getPasswort());

                        // Entschluesseln
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                        byte[] cipherData = cipher.doFinal(crypted);
                        verbindung.setPasswort(new String(cipherData));
                    } else {
                        verbindung.setPasswort("");
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("[INFO]   Liste mit Ordnern existiert nicht!");
        } catch (Exception e) {
            System.out.println("[Fehler] beim Laden der Liste mit Ordnern!");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     */
    public ArrayList<Ordner> getOrdnerList() {
        return ordnerList;
    }

    /**
     *
     */
    public void setOrdnerList(ArrayList<Ordner> ordnerList) {
        this.ordnerList = ordnerList;
    }

    /**
     *
     */
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
