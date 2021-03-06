package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

public class Buchung {

    private long buchung_Id;
    private long b_Id;
    private long abschnitts_Id;
    private long kategorie_Id;
    private String kennzeichen;
    private Timestamp buchungsdatum;
    private Timestamp befahrungsdateum;
    private float kosten;


    private static final Logger L = LoggerFactory.getLogger(Buchung.class);
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Buchung(long buchung_Id, long b_Id, long abschnitts_Id, long kategorie_Id, String kennzeichen, Timestamp buchungsdatum, Timestamp befahrungsdateum, float kosten) {
        this.buchung_Id = buchung_Id;
        this.b_Id = b_Id;
        this.abschnitts_Id = abschnitts_Id;
        this.kategorie_Id = kategorie_Id;
        this.kennzeichen = kennzeichen;
        this.buchungsdatum = buchungsdatum;
        this.befahrungsdateum = befahrungsdateum;
        this.kosten = kosten;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataException("Connection not set");
        }
        return connection;
    }

    public long getBuchung_Id() {
        return buchung_Id;
    }

    public void setBuchung_Id(long buchung_Id) {
        this.buchung_Id = buchung_Id;
    }

    public long getB_Id() {
        return b_Id;
    }

    public void setB_Id(long b_Id) {
        this.b_Id = b_Id;
    }

    public long getAbschnitts_Id() {
        return abschnitts_Id;
    }

    public void setAbschnitts_Id(long abschnitts_Id) {
        this.abschnitts_Id = abschnitts_Id;
    }

    public long getKategorie_Id() {
        return kategorie_Id;
    }

    public void setKategorie_Id(long kategorie_Id) {
        this.kategorie_Id = kategorie_Id;
    }

    public String getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(String kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public Timestamp getBuchungsdatum() {
        return buchungsdatum;
    }

    public void setBuchungsdatum(Timestamp buchungsdatum) {
        this.buchungsdatum = buchungsdatum;
    }

    public Timestamp getBefahrungsdateum() {
        return befahrungsdateum;
    }

    public void setBefahrungsdateum(Timestamp befahrungsdateum) {
        this.befahrungsdateum = befahrungsdateum;
    }

    public float getKosten() {
        return kosten;
    }

    public void setKosten(float kosten) {
        this.kosten = kosten;
    }

    public int getAchszahl(){
        var sql = "select ACHSZAHL from MAUTKATEGORIE where KATEGORIE_ID = " + this.getKategorie_Id();

        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    String achszhl = resultSet.getString("ACHSZAHL");

                    return Integer.parseInt(String.valueOf(achszhl.charAt(achszhl.length()-1)));
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }

    public float getMautsatzProKM() {
        var sql = "select MAUTSATZ_JE_KM from MAUTKATEGORIE where KATEGORIE_ID = " + this.getKategorie_Id();

        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    // Mautsatz is stored in cent
                    return resultSet.getFloat("MAUTSATZ_JE_KM") / 100;
                } else return 0;
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }

    public void update() {
        String sql = "update BUCHUNG set BEFAHRUNGSDATUM = ? where  BUCHUNG_ID = " + this.getBuchung_Id();
        L.info(sql);

        try (PreparedStatement statement = getConnection().prepareStatement(sql)){
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();

        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }

    public void changeStatusToClosed() {
        String sql = "update BUCHUNG set B_ID = 3 where  BUCHUNG_ID = '" + this.getBuchung_Id() + "'";
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
    }
}
