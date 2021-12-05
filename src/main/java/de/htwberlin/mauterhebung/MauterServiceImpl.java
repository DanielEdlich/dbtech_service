package de.htwberlin.mauterhebung;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.htwberlin.exceptions.AlreadyCruisedException;
import de.htwberlin.exceptions.DataException;
import de.htwberlin.exceptions.InvalidVehicleDataException;
import de.htwberlin.exceptions.UnkownVehicleException;

/**
 * Die Klasse realisiert den AusleiheService.
 *
 * @author Patrick Dohmeier
 */
public class MauterServiceImpl implements IMauterhebung {

	private static final Logger L = LoggerFactory.getLogger(MauterServiceImpl.class);
	private Connection connection;

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private Connection getConnection() {
		if (connection == null) {
			throw new DataException("Connection not set");
		}
		return connection;
	}

	@Override
	public float berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
			throws UnkownVehicleException, InvalidVehicleDataException, AlreadyCruisedException {
		if(isFahrzeugImAutomatikverfahren(kennzeichen)){

		}
		else if (isFahrzeugImManuellenVerfahren(kennzeichen)){

		}
		else {
			throw new UnkownVehicleException();
		}




		// TODO Auto-generated method stub
		return 0;
	}

	private boolean isFahrzeugImAutomatikverfahren(String kennzeichen){
		var sql = "select ABMELDEDATUM, STATUS from FAHRZEUG F join FAHRZEUGGERAT FG on F.FZ_ID = FG.FZ_ID where F.KENNZEICHEN = '"
			+ kennzeichen + "'";
		L.info(sql);

		try (Statement statement = getConnection().createStatement()){
			try (ResultSet resultSet = statement.executeQuery(sql)){
				if(resultSet.next()){
					if (resultSet.getString("STATUS").equals("active") && resultSet.getObject("ABMELDEDATUM") == null){
						return true;
					}
				}
				else
					throw new UnkownVehicleException();
			}
		} catch (SQLException e) {
			L.error("", e);
			throw new DataException();
		}

		return false;
	}

	private boolean isFahrzeugImManuellenVerfahren(String kennzeichen){
		var sql = "select * from BUCHUNG where B_ID = 1 AND KENNZEICHEN = '" + kennzeichen + "'";

		L.info(sql);

		try (Statement statement = getConnection().createStatement()) {
			try (ResultSet resultSet = statement.executeQuery(sql)) {
				if(resultSet.next()) return true;
					else return false;
			}
		} catch (SQLException e) {
			L.error("", e);
			throw new DataException();
		}
	}






}
