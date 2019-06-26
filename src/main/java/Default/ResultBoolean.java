package Default;

public class ResultBoolean {
    private boolean erfolgreich;
    private String bemerkung;

    public ResultBoolean(){

    };

    public ResultBoolean(boolean erfolgreich, String bemerkung) {
        this.bemerkung = bemerkung;
        this.erfolgreich = erfolgreich;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public boolean isErfolgreich() {
        return erfolgreich;
    }

    public void setErfolgreich(boolean erfolgreich) {
        this.erfolgreich = erfolgreich;
    }
}
