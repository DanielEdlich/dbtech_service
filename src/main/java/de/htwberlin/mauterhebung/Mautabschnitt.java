package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class Mautabschnitt {

    private int ABSCHNITTS_ID;
    private float LAENGE;
    private String START_KOORDINATE;
    private String ZIEL_KOORDINATE;
    private String NAME;
    private String ABSCHNITTSTYP;

    private static final Logger L = LoggerFactory.getLogger(Mautabschnitt.class);
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataException("Connection not set");
        }
        return connection;
    }


    public Mautabschnitt(){}

    public int getABSCHNITTS_ID() {
        return ABSCHNITTS_ID;
    }

    public void setABSCHNITTS_ID(int ABSCHNITTS_ID) {
        this.ABSCHNITTS_ID = ABSCHNITTS_ID;
    }

    public float getLAENGE() {
        return LAENGE;
    }

    public void setLAENGE(int LAENGE) {
        this.LAENGE = LAENGE;
    }

    public String getSTART_KOORDINATE() {
        return START_KOORDINATE;
    }

    public void setSTART_KOORDINATE(String START_KOORDINATE) {
        this.START_KOORDINATE = START_KOORDINATE;
    }

    public String getZIEL_KOORDINATE() {
        return ZIEL_KOORDINATE;
    }

    public void setZIEL_KOORDINATE(String ZIEL_KOORDINATE) {
        this.ZIEL_KOORDINATE = ZIEL_KOORDINATE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getABSCHNITTSTYP() {
        return ABSCHNITTSTYP;
    }

    public void setABSCHNITTSTYP(String ABSCHNITTSTYP) {
        this.ABSCHNITTSTYP = ABSCHNITTSTYP;
    }


    public float getLaengeInMeter(){
        return LAENGE;
    }

    public float getLaengeInKm() {
        return LAENGE / 1000;
    }
}
