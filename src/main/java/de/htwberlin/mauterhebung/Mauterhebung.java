package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;


public class Mauterhebung {

    private long MAUT_ID;
    private long ABSCHNITTS_ID;
    private long FZG_ID;
    private long KATEGORIE_ID;
    private Timestamp BEFAHRUNGSDATUM;
    private float KOSTEN;

    private static final Logger L = LoggerFactory.getLogger(Mauterhebung.class);
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }



    public Mauterhebung(long ABSCHNITTS_ID, long FZG_ID, long KATEGORIE_ID, float KOSTEN){
        this.ABSCHNITTS_ID = ABSCHNITTS_ID;
        this.FZG_ID = FZG_ID;
        this.KATEGORIE_ID = KATEGORIE_ID;
        this.KOSTEN = KOSTEN;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataException("Connection not set");
        }
        return connection;
    }

    public long getMAUT_ID() {
        return MAUT_ID;
    }

    public void setMAUT_ID(int MAUT_ID) {
        this.MAUT_ID = MAUT_ID;
    }

    public long getABSCHNITTS_ID() {
        return ABSCHNITTS_ID;
    }

    public void setABSCHNITTS_ID(int ABSCHNITTS_ID) {
        this.ABSCHNITTS_ID = ABSCHNITTS_ID;
    }

    public long getFZG_ID() {
        return FZG_ID;
    }

    public void setFZG_ID(int FZG_ID) {
        this.FZG_ID = FZG_ID;
    }

    public long getKATEGORIE_ID() {
        return KATEGORIE_ID;
    }

    public void setKATEGORIE_ID(int KATEGORIE_ID) {
        this.KATEGORIE_ID = KATEGORIE_ID;
    }

    public Timestamp getBEFAHRUNGSDATUM() {
        return BEFAHRUNGSDATUM;
    }

    public void setBEFAHRUNGSDATUM(Timestamp BEFAHRUNGSDATUM) {
        this.BEFAHRUNGSDATUM = BEFAHRUNGSDATUM;
    }

    public float getKOSTEN() {
        return KOSTEN;
    }

    public void setKOSTEN(float KOSTEN) {
        this.KOSTEN = KOSTEN;
    }

    public void insert() {

        int id = getNextMautId();

        String sql = String.format("insert into MAUTERHEBUNG (MAUT_ID, ABSCHNITTS_ID, FZG_ID, KATEGORIE_ID, " +
                "BEFAHRUNGSDATUM, KOSTEN) values (%d, %d, %d, %d, ?, %s)",
                id, this.ABSCHNITTS_ID, this.FZG_ID, this.KATEGORIE_ID, String.valueOf(this.KOSTEN) );
        L.info(sql);

        try (PreparedStatement statement = getConnection().prepareStatement(sql)){
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));

            statement.executeUpdate();

        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }

    private int getNextMautId(){

        var sql = "SELECT COUNT (*) FROM MAUTERHEBUNG";

        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) + 1000;
                }
                else {
                    return 0;
                }

            }
        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }

}
