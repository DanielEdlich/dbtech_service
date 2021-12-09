package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class Fahrzeug {

    private static final Logger L = LoggerFactory.getLogger(Fahrzeug.class);
    private Connection connection;

    int FZ_ID;
    int SSKL_ID;
    int NUTZER_ID;
    String KENNZEICHEN;
    String FIN;
    int ACHSEN;
    int GEWICHT;
    Date ANMELDEDATUM;
    Date ABMELDEDATUM;
    String ZULASSUNGSLAND;


    public Fahrzeug(){};


    public int getFZ_ID() {
        return FZ_ID;
    }

    public void setFZ_ID(int FZ_ID) {
        this.FZ_ID = FZ_ID;
    }

    public int getSSKL_ID() {
        return SSKL_ID;
    }

    public void setSSKL_ID(int SSKL_ID) {
        this.SSKL_ID = SSKL_ID;
    }

    public int getNUTZER_ID() {
        return NUTZER_ID;
    }

    public void setNUTZER_ID(int NUTZER_ID) {
        this.NUTZER_ID = NUTZER_ID;
    }

    public String getKENNZEICHEN() {
        return KENNZEICHEN;
    }

    public void setKENNZEICHEN(String KENNZEICHEN) {
        this.KENNZEICHEN = KENNZEICHEN;
    }

    public String getFIN() {
        return FIN;
    }

    public void setFIN(String FIN) {
        this.FIN = FIN;
    }

    public int getACHSEN() {
        return ACHSEN;
    }

    public void setACHSEN(int ACHSEN) {
        this.ACHSEN = ACHSEN;
    }

    public int getGEWICHT() {
        return GEWICHT;
    }

    public void setGEWICHT(int GEWICHT) {
        this.GEWICHT = GEWICHT;
    }

    public Date getANMELDEDATUM() {
        return ANMELDEDATUM;
    }

    public void setANMELDEDATUM(Date ANMELDEDATUM) {
        this.ANMELDEDATUM = ANMELDEDATUM;
    }

    public Date getABMELDEDATUM() {
        return ABMELDEDATUM;
    }

    public void setABMELDEDATUM(Date ABMELDEDATUM) {
        this.ABMELDEDATUM = ABMELDEDATUM;
    }

    public String getZULASSUNGSLAND() {
        return ZULASSUNGSLAND;
    }

    public void setZULASSUNGSLAND(String ZULASSUNGSLAND) {
        this.ZULASSUNGSLAND = ZULASSUNGSLAND;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    private Connection getConnection() {
        if (connection == null) {
            throw new DataException("Connection not set");
        }
        return connection;
    }


    public int getAchszahl(){
        var sql = "select ACHSEN from FAHRZEUG F " +
                        "where F.KENNZEICHEN = '" + this.KENNZEICHEN + "'";
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    String achszhl = resultSet.getString(1);

                    return Integer.parseInt(String.valueOf(achszhl.charAt(achszhl.length()-1)));
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
    public float getMautsatzProKM(){
        var sql = "select MK.MAUTSATZ_JE_KM from FAHRZEUG F " +
                "join SCHADSTOFFKLASSE S on F.SSKL_ID = S.SSKKL_ID " +
                "join MAUTKATEGORIE MK on S.SSKL_ID = MK.SSKL_ID " +
                "where F.Achsen = MK.Achszahl";
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    // Mautsatz is stored in cent
                    return resultSet.getFloat("MAUTSATZ_JE_KM")/100;
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
    public int getFahrzeugGeraetId(){
        var sql = "select FG.FZG_ID from FAHRZEUG F " +
                "join FAHRZEUGGERAT FG on F.FZ_ID = FG.FZ_ID " +
                "where F.KENNZEICHEN = '" + this.KENNZEICHEN + "'";
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    return resultSet.getInt("FZG_ID");
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
    public int getMautkategorieId(){
        var sql = "select MK.KATEGORIE_ID from FAHRZEUG F " +
                "join SCHADSTOFFKLASSE S on F.SSKL_ID = S.SSKKL_ID " +
                "join MAUTKATEGORIE MK on S.SSKL_ID = MK.SSKL_ID " +
                "where F.Achsen = MK.Achszahl AND where F.ZF_ID = " + this.FZ_ID;
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    return resultSet.getInt("KATEGORIE_ID");
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
