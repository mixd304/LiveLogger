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
        ordner1.setBezeichnung("Linux Serverübersicht");
        ordner1.setUuid(UUID.randomUUID());
        Verbindung verbindung1_1 = new Verbindung();
        verbindung1_1.setBezeichnung("Localhost Windows Server");
        verbindung1_1.setBenutzername("root");
        verbindung1_1.setHost("localhost");
        verbindung1_1.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung1_1.setPasswort("12345");
        verbindung1_1.setPort(21);
        verbindung1_1.setBetriebssystem("Windows");
        verbindung1_1.setUuid(UUID.randomUUID());

        Verbindung verbindung1_2 = new Verbindung();
        verbindung1_2.setBezeichnung("Localhost Windows Exchange Server");
        verbindung1_2.setBenutzername("root");
        verbindung1_2.setHost("127.0.0.1");
        verbindung1_2.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung1_2.setPasswort("123abc");
        verbindung1_2.setPort(47);
        verbindung1_2.setBetriebssystem("Windows");
        verbindung1_2.setUuid(UUID.randomUUID());

        Verbindung verbindung1_3 = new Verbindung();
        verbindung1_3.setBezeichnung("Test Server 3");
        verbindung1_3.setBenutzername("root");
        verbindung1_3.setHost("127.0.0.1");
        verbindung1_3.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung1_3.setPasswort("123abc");
        verbindung1_3.setPort(47);
        verbindung1_3.setBetriebssystem("Linux");
        verbindung1_3.setUuid(UUID.randomUUID());

        ordner1.addVerbindung(verbindung1_1);
        ordner1.addVerbindung(verbindung1_2);
        ordner1.addVerbindung(verbindung1_3);



        Ordner ordner2 = new Ordner();
        ordner2.setBezeichnung("Schuelermessquiz");
        ordner2.setUuid(UUID.randomUUID());
        Verbindung verbindung2_1 = new Verbindung();
        verbindung2_1.setBezeichnung("Linux Datenbankserver");
        verbindung2_1.setBenutzername("root");
        verbindung2_1.setHost("localhost");
        verbindung2_1.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_1.setPasswort("12345");
        verbindung2_1.setPort(21);
        verbindung2_1.setBetriebssystem("Linux");
        verbindung2_1.setUuid(UUID.randomUUID());

        Verbindung verbindung2_2 = new Verbindung();
        verbindung2_2.setBezeichnung("Linux WebServer");
        verbindung2_2.setBenutzername("root");
        verbindung2_2.setHost("127.0.0.1");
        verbindung2_2.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_2.setPasswort("123abc");
        verbindung2_2.setPort(47);
        verbindung2_2.setBetriebssystem("Linux");
        verbindung2_2.setUuid(UUID.randomUUID());

        Verbindung verbindung2_3 = new Verbindung();
        verbindung2_3.setBezeichnung("Linux Server 3");
        verbindung2_3.setBenutzername("root");
        verbindung2_3.setHost("127.0.0.1");
        verbindung2_3.setLogpath("C:/Projekte/GEE/FileWriter/log.txt");
        verbindung2_3.setPasswort("123abc");
        verbindung2_3.setPort(47);
        verbindung2_3.setBetriebssystem("Linux");
        verbindung2_3.setUuid(UUID.randomUUID());

        ordner2.addVerbindung(verbindung2_1);
        ordner2.addVerbindung(verbindung2_2);
        ordner2.addVerbindung(verbindung2_3);

        modelContainer.addOrdner(ordner1);
        modelContainer.addOrdner(ordner2);
        modelContainer.safeOrdner();
    }
}
