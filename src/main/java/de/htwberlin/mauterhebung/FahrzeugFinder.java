package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FahrzeugFinder
{

    private static final Logger L = LoggerFactory.getLogger(Fahrzeug.class);
    private Connection connection;

    public FahrzeugFinder(Connection connection) {
        this.setConnection(connection);
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    private Connection getConnection()
    {
        if (connection == null)
        {
            throw new DataException("Connection not set");
        }
        return connection;
    }

    public Fahrzeug findByKennzeichen(String kennzeichen)
    {
        var sql = "select * from FAHRZEUG F join FAHRZEUGGERAT FG on F.FZ_ID = FG.FZ_ID " +
                "where STATUS = 'active' and ABMELDEDATUM is null and F.KENNZEICHEN = '" + kennzeichen + "'";

        Fahrzeug fahrzeug = new Fahrzeug();
        L.info(sql);

        try (Statement statement = getConnection().createStatement())
        {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()){
                    fahrzeug.setFZ_ID(resultSet.getLong("FZ_ID"));
                    fahrzeug.setSSKL_ID(resultSet.getLong("SSKL_ID"));
                    fahrzeug.setNUTZER_ID(resultSet.getLong("NUTZER_ID"));
                    fahrzeug.setKENNZEICHEN(kennzeichen);
                    fahrzeug.setFIN(resultSet.getString("FIN"));
                    fahrzeug.setACHSEN(resultSet.getInt("ACHSEN"));
                    fahrzeug.setGEWICHT(resultSet.getInt("GEWICHT"));
                    fahrzeug.setANMELDEDATUM(resultSet.getDate("ANMELDEDATUM"));
                    fahrzeug.setABMELDEDATUM(resultSet.getDate("ABMELDEDATUM"));
                    fahrzeug.setZULASSUNGSLAND(resultSet.getString("ZULASSUNGSLAND"));

                    fahrzeug.setFZG_ID(resultSet.getLong("FZG_ID"));

                    return fahrzeug;
                }
            }
        } catch (SQLException e)
        {
            L.error("", e);
            throw new ServiceException();
        }
        return null;
    }

}
