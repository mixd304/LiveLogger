import Model.Container.ModelContainer;
import Model.Ordner;
import Model.Verbindung;

import java.util.ArrayList;
import java.util.UUID;

public class ModelControllerTest {


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

        Ordner ordner1 = new Ordner();
        ordner1.setBezeichnung("Linux Testserver");
        ordner1.setUuid(UUID.randomUUID());
        Verbindung verbindung1_1 = new Verbindung();
        verbindung1_1.setBezeichnung("Funktioniert");
        verbindung1_1.setBenutzername("root");
        verbindung1_1.setHost("10.4.245.16");
        verbindung1_1.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_1.setPasswort("start-1234");
        verbindung1_1.setPort(0);
        verbindung1_1.setBetriebssystem("Linux");
        verbindung1_1.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_1);

        Verbindung verbindung1_2 = new Verbindung();
        verbindung1_2.setBezeichnung("Falscher Benutzername");
        verbindung1_2.setBenutzername("bob");
        verbindung1_2.setHost("10.4.245.16");
        verbindung1_2.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_2.setPasswort("start-1234");
        verbindung1_2.setPort(0);
        verbindung1_2.setBetriebssystem("Linux");
        verbindung1_2.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_2);

        Verbindung verbindung1_3 = new Verbindung();
        verbindung1_3.setBezeichnung("Falscher Host");
        verbindung1_3.setBenutzername("root");
        verbindung1_3.setHost("10.4.245.99");
        verbindung1_3.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_3.setPasswort("start-1234");
        verbindung1_3.setPort(0);
        verbindung1_3.setBetriebssystem("Linux");
        verbindung1_3.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_3);

        Verbindung verbindung1_4 = new Verbindung();
        verbindung1_4.setBezeichnung("Falscher LogPath");
        verbindung1_4.setBenutzername("root");
        verbindung1_4.setHost("10.4.245.16");
        verbindung1_4.setLogpath("/opt/bob/babo");
        verbindung1_4.setPasswort("start-1234");
        verbindung1_4.setPort(0);
        verbindung1_4.setBetriebssystem("Linux");
        verbindung1_4.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_4);

        Verbindung verbindung1_5 = new Verbindung();
        verbindung1_5.setBezeichnung("Falsches Passwort");
        verbindung1_5.setBenutzername("root");
        verbindung1_5.setHost("10.4.245.16");
        verbindung1_5.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung1_5.setPasswort("start");
        verbindung1_5.setPort(0);
        verbindung1_5.setBetriebssystem("Linux");
        verbindung1_5.setUuid(UUID.randomUUID());
        ordner1.addVerbindung(verbindung1_5);






        Ordner ordner2 = new Ordner();
        ordner2.setBezeichnung("Windows Testserver");
        ordner2.setUuid(UUID.randomUUID());
        Verbindung verbindung2_1 = new Verbindung();
        verbindung2_1.setBezeichnung("Funktioniert - Coming soon");
        verbindung2_1.setBenutzername("root");
        verbindung2_1.setHost("localhost");
        verbindung2_1.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_1.setPasswort("123");
        verbindung2_1.setPort(8080);
        verbindung2_1.setBetriebssystem("Windows");
        verbindung2_1.setUuid(UUID.randomUUID());

        Verbindung verbindung2_2 = new Verbindung();
        verbindung2_2.setBezeichnung("Falscher Benutzername - Coming soon");
        verbindung2_2.setBenutzername("root");
        verbindung2_2.setHost("127.0.0.1");
        verbindung2_2.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_2.setPasswort("123abc");
        verbindung2_2.setPort(47);
        verbindung2_2.setBetriebssystem("Linux");
        verbindung2_2.setUuid(UUID.randomUUID());

        Verbindung verbindung2_3 = new Verbindung();
        verbindung2_3.setBezeichnung("Falscher Host - Coming soon");
        verbindung2_3.setBenutzername("root");
        verbindung2_3.setHost("10.4.245.16");
        verbindung2_3.setLogpath("/opt/custom/tomcat/base/logs/catalina.out");
        verbindung2_3.setPasswort("start-1234");
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
