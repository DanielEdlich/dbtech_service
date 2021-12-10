package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MautabschnittFinder {

    private static final Logger L = LoggerFactory.getLogger(MautabschnittFinder.class);
    private Connection connection;

    public MautabschnittFinder(Connection connection) {
        setConnection(connection);
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

    public Mautabschnitt findById(int id)
    {
        var sql = "select * from MAUTABSCHNITT MA where MA.ABSCHNITTS_ID = " + id;

        Mautabschnitt mautabschnitt = new Mautabschnitt();
        L.info(sql);

        try (Statement statement = getConnection().createStatement())
        {
            try (ResultSet resultSet = statement.executeQuery(sql))
            {
                mautabschnitt.setABSCHNITTS_ID(resultSet.getInt("ABSCHNITTS_ID"));
                mautabschnitt.setLAENGE(resultSet.getInt("LAENGE"));
                mautabschnitt.setSTART_KOORDINATE(resultSet.getString("START_KOORDINATE"));
                mautabschnitt.setZIEL_KOORDINATE(resultSet.getString("ZIEL_KOORDINATE"));
                mautabschnitt.setNAME(resultSet.getString("NAME"));
                mautabschnitt.setABSCHNITTSTYP(resultSet.getString("ABSCHNITTSTYP"));

                return mautabschnitt;
            }
        } catch (SQLException e)
        {
            L.error("", e);
            throw new ServiceException();
        }
    }

}
