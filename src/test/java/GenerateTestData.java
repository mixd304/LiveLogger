import ModelController.ModelContainer;
import Model.Data.Ordner;
import Model.Data.Verbindung;

import java.util.ArrayList;
import java.util.UUID;

public class GenerateTestData {


    public static void main(String args[]) {
        generateTestData();

        System.out.println("=========================================================================");
        ModelContainer modelContainerNeu = new ModelContainer();
        modelContainerNeu.loadOrdner();


        ArrayList<Ordner> ordnerArrayList = modelContainerNeu.getOrdnerList();
        System.out.println(ordnerArrayList.size());

        for(Ordner ordner: ordnerArrayList) {
            ArrayList<Verbindung> verbindungen = ordner.getList();
            for (Verbindung verbindung: verbindungen) {
                System.out.println(verbindung.getHost());
                System.out.println(verbindung.getBenutzername());
                System.out.println(verbindung.getBezeichnung());
            }
        }
    }

    private static void generateTestData() {
        ModelContainer modelContainer = new ModelContainer();

        // ============================ LINUX TESTDATEN =============================

        Ordner ordner1 = new Ordner();
        ordner1.setBezeichnung("Linux Testserver");
        ordner1.setUuid(UUID.randomUUID());
        Verbindung verbindung1_1 = new Verbindung();
        verbindung1_1.setBezeichnung("Funktioniert mit Passwort");
        verbindung1_1.setBenutzername("root");
        verbindung1_1.setHost("10.4.245.16");
        verbindung1_1.setPasswort("start-1234");
        verbindung1_1.setSafePasswort(true);
        verbindung1_1.setPort(22);
        verbindung1_1.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_1.setKeyfile("");
        verbindung1_1.setPrecommand("");
        verbindung1_1.setBetriebssystem("Linux");
        verbindung1_1.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_1);

        Verbindung verbindung1_2 = new Verbindung();
        verbindung1_2.setBezeichnung("Funktioniert ohne Passwort");
        verbindung1_2.setBenutzername("root");
        verbindung1_2.setHost("10.4.245.16");
        verbindung1_2.setSafePasswort(false);
        verbindung1_2.setPasswort("");
        verbindung1_2.setPort(22);
        verbindung1_2.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_2.setKeyfile("");
        verbindung1_2.setPrecommand("");
        verbindung1_2.setBetriebssystem("Linux");
        verbindung1_2.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_2);

        Verbindung verbindung1_3 = new Verbindung();
        verbindung1_3.setBezeichnung("Falscher Benutzername");
        verbindung1_3.setBenutzername("root_falsch");
        verbindung1_3.setHost("10.4.245.16");
        verbindung1_3.setSafePasswort(true);
        verbindung1_3.setPasswort("start-1234");
        verbindung1_3.setPort(22);
        verbindung1_3.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_3.setKeyfile("");
        verbindung1_3.setPrecommand("");
        verbindung1_3.setBetriebssystem("Linux");
        verbindung1_3.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_3);

        Verbindung verbindung1_4 = new Verbindung();
        verbindung1_4.setBezeichnung("Falscher Host");
        verbindung1_4.setBenutzername("root");
        verbindung1_4.setHost("99.4.245.16");
        verbindung1_4.setSafePasswort(true);
        verbindung1_4.setPasswort("start-1234");
        verbindung1_4.setPort(22);
        verbindung1_4.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_4.setKeyfile("");
        verbindung1_4.setPrecommand("");
        verbindung1_4.setBetriebssystem("Linux");
        verbindung1_4.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_4);

        Verbindung verbindung1_5 = new Verbindung();
        verbindung1_5.setBezeichnung("Falscher LogPath");
        verbindung1_5.setBenutzername("root");
        verbindung1_5.setHost("10.4.245.16");
        verbindung1_5.setSafePasswort(true);
        verbindung1_5.setPasswort("start-1234");
        verbindung1_5.setPort(22);
        verbindung1_5.setLogpath("/opt/custom/tomcat/base/falsch.out");
        verbindung1_5.setKeyfile("");
        verbindung1_5.setPrecommand("");
        verbindung1_5.setBetriebssystem("Linux");
        verbindung1_5.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_5);

        Verbindung verbindung1_6 = new Verbindung();
        verbindung1_6.setBezeichnung("Falsches Passwort");
        verbindung1_6.setBenutzername("root");
        verbindung1_6.setHost("10.4.245.16");
        verbindung1_6.setSafePasswort(true);
        verbindung1_6.setPasswort("falsch");
        verbindung1_6.setPort(22);
        verbindung1_6.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_6.setKeyfile("");
        verbindung1_6.setPrecommand("");
        verbindung1_6.setBetriebssystem("Linux");
        verbindung1_6.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_6);

        Verbindung verbindung1_7 = new Verbindung();
        verbindung1_7.setBezeichnung("Falscher Port");
        verbindung1_7.setBenutzername("root");
        verbindung1_7.setHost("10.4.245.16");
        verbindung1_7.setSafePasswort(true);
        verbindung1_7.setPasswort("start-1234");
        verbindung1_7.setPort(32);
        verbindung1_7.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_7.setKeyfile("");
        verbindung1_7.setPrecommand("");
        verbindung1_7.setBetriebssystem("Linux");
        verbindung1_7.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_7);

        // ============================ WINDOWS TESTDATEN =============================

        Ordner ordner2 = new Ordner();
        ordner2.setBezeichnung("Windows Testserver");
        ordner2.setUuid(UUID.randomUUID());
        Verbindung verbindung2_1 = new Verbindung();
        verbindung2_1.setBezeichnung("Funktioniert - Coming soon");
        verbindung2_1.setBenutzername("root");
        verbindung2_1.setHost("localhost");
        verbindung2_1.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_1.setPasswort("123");
        verbindung2_1.setSafePasswort(true);
        verbindung2_1.setPort(8080);
        verbindung2_1.setBetriebssystem("Windows");
        verbindung2_1.setUuid(UUID.randomUUID());

        Verbindung verbindung2_2 = new Verbindung();
        verbindung2_2.setBezeichnung("Falscher Benutzername - Coming soon");
        verbindung2_2.setBenutzername("root");
        verbindung2_2.setHost("127.0.0.1");
        verbindung2_2.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_2.setPasswort("123abc");
        verbindung2_2.setSafePasswort(true);
        verbindung2_2.setPort(47);
        verbindung2_2.setBetriebssystem("Linux");
        verbindung2_2.setUuid(UUID.randomUUID());

        Verbindung verbindung2_3 = new Verbindung();
        verbindung2_3.setBezeichnung("Falscher Host - Coming soon");
        verbindung2_3.setBenutzername("root");
        verbindung2_3.setHost("10.4.245.16");
        verbindung2_3.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung2_3.setPasswort("start-1234");
        verbindung2_3.setSafePasswort(true);
        verbindung2_3.setPort(1);
        verbindung2_3.setBetriebssystem("Windows");
        verbindung2_3.setUuid(UUID.randomUUID());

        ordner2.addVerbindung(verbindung2_1);
        ordner2.addVerbindung(verbindung2_2);
        ordner2.addVerbindung(verbindung2_3);

        modelContainer.addOrdner(ordner1);
        modelContainer.addOrdner(ordner2);
        modelContainer.safeOrdner();
    }
}
