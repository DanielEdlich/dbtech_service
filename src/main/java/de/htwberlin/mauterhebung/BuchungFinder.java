package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.AlreadyCruisedException;
import de.htwberlin.exceptions.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BuchungFinder {

    private static final Logger L = LoggerFactory.getLogger(BuchungFinder.class);
    private Connection connection;

    public BuchungFinder(Connection connection) {
        this.setConnection(connection);
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

    public Buchung findByKennzeichenMautAbschnitt(String kennzeichen, int mautAbschnitt){
        String sql = String.format("select * from BUCHUNG where BEFAHRUNGSDATUM is null AND kennzeichen = '%s' AND ABSCHNITTS_ID = %d", kennzeichen, mautAbschnitt);
        try (Statement statement = getConnection().createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    if(resultSet.getTimestamp("BEFAHRUNGSDATUM") == null) {
                        return new Buchung(
                                resultSet.getInt("BUCHUNG_ID"),
                                resultSet.getInt("B_ID"),
                                resultSet.getInt("ABSCHNITTS_ID"),
                                resultSet.getInt("KATEGORIE_ID"),
                                resultSet.getString("KENNZEICHEN"),
                                resultSet.getTimestamp("BUCHUNGSDATUM"),
                                resultSet.getTimestamp("BEFAHRUNGSDATUM"),
                                resultSet.getFloat("KOSTEN")
                        );
                    }
                    else throw new  AlreadyCruisedException();
                }
            }
        }catch (SQLException e) {
            L.error("", e);
            throw new DataException();
        }
        return null;
    }
}
