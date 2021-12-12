package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Fahrzeug {

    private static final Logger L = LoggerFactory.getLogger(Fahrzeug.class);
    private Connection connection;

    private long FZ_ID;
    private long SSKL_ID;
    private long NUTZER_ID;
    private String KENNZEICHEN;
    private String FIN;
    private int ACHSEN;
    private int GEWICHT;
    private Date ANMELDEDATUM;
    private Date ABMELDEDATUM;
    private String ZULASSUNGSLAND;

    private long FZG_ID;

    public long getFZ_ID() {
        return FZ_ID;
    }

    public void setFZ_ID(long FZ_ID) {
        this.FZ_ID = FZ_ID;
    }

    public long getSSKL_ID() {
        return SSKL_ID;
    }

    public void setSSKL_ID(long SSKL_ID) {
        this.SSKL_ID = SSKL_ID;
    }

    public long getNUTZER_ID() {
        return NUTZER_ID;
    }

    public void setNUTZER_ID(long NUTZER_ID) {
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

    // TODO method senseless, value already stored in local variable achszahl
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
        var sql = "select ACHSEN, ACHSZAHL, MK.MAUTSATZ_JE_KM from FAHRZEUG F " +
                "join SCHADSTOFFKLASSE S on F.SSKL_ID = S.SSKL_ID " +
                "join MAUTKATEGORIE MK on S.SSKL_ID = MK.SSKL_ID " +
                "where F.FZ_ID = " + this.getFZ_ID();

        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    var achsen = Integer.parseInt(String.valueOf(
                            resultSet.getString("ACHSEN").charAt(
                                    resultSet.getString("ACHSEN").length()-1)));

                    var achszahl = Integer.parseInt(String.valueOf(
                            resultSet.getString("ACHSZAHL").charAt(
                                    resultSet.getString("ACHSZAHL").length()-1)));

                    if(((achsen < 5 && achszahl == achsen) || (achsen >= 5 && achszahl >= 5))){
                        // Mautsatz is stored in cent
                        return resultSet.getFloat("MAUTSATZ_JE_KM")/100;
                    }
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
        return 0;
    }
    public int getFahrzeugGeraetId(){
        var sql = "select FG.FZG_ID from FAHRZEUG F " +
                "join FAHRZEUGGERAT FG on F.FZ_ID = FG.FZ_ID " +
                "where FG.FZ_ID = " + this.getFZ_ID();
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

    public long getMautkategorieId(){
        var sql = "select ACHSEN, ACHSZAHL, MK.KATEGORIE_ID from FAHRZEUG F " +
                "join SCHADSTOFFKLASSE S on F.SSKL_ID = S.SSKL_ID " +
                "join MAUTKATEGORIE MK on S.SSKL_ID = MK.SSKL_ID " +
                "where F.FZ_ID = " + this.getFZ_ID();
        L.info(sql);

        try (Statement statement = getConnection().createStatement()){
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    var achsen = Integer.parseInt(String.valueOf(
                            resultSet.getString("ACHSEN").charAt(
                                    resultSet.getString("ACHSEN").length()-1)));

                    var achszahl = Integer.parseInt(String.valueOf(
                            resultSet.getString("ACHSZAHL").charAt(
                                    resultSet.getString("ACHSZAHL").length()-1)));

                    if(((achsen < 5 && achszahl == achsen) || (achsen >= 5 && achszahl >= 5))){
                        return resultSet.getLong("KATEGORIE_ID");
                    }
                }
            }
        } catch (SQLException e) {
            L.error("", e);
            throw new ServiceException();
        }
        return 0;
    }

    public void setFZG_ID(long fzg_id) {
        this.FZG_ID = fzg_id;
    }

    public long getFZG_ID() {
        return FZG_ID;
    }
}
