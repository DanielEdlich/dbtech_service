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

	/**
	 * Die Methode realisiert einen Algorithmus, der die �bermittelten
	 * Fahrzeugdaten mit der Datenbank auf Richtigkeit �berpr�ft und f�r einen
	 * mautpflichtigen Streckenabschnitt die zu zahlende Maut f�r ein Fahrzeug
	 * im Automatischen Verfahren berechnet.
	 *
	 * Zuvor wird �berpr�ft, ob das Fahrzeug registriert ist und �ber ein
	 * eingebautes Fahrzeugger�t verf�gt und die �bermittelten Daten des
	 * Kontrollsystems korrekt sind. Bei Fahrzeugen im Manuellen Verfahren wird
	 * dar�berhinaus gepr�ft, ob es noch offene Buchungen f�r den Mautabschnitt
	 * gibt oder eine Doppelbefahrung aufgetreten ist. Besteht noch eine offene
	 * Buchung f�r den Mautabschnitt, so wird diese Buchung f�r das Fahrzeug auf
	 * abgeschlossen gesetzt.
	 *
	 * Sind die Daten des Fahrzeugs im Automatischen Verfahren korrekt, wird
	 * anhand der Mautkategorie (die sich aus der Achszahl und der
	 * Schadstoffklasse des Fahrzeugs zusammensetzt) und der Mautabschnittsl�nge
	 * die zu zahlende Maut berechnet, in der Mauterhebung gespeichert und
	 * letztendlich zur�ckgegeben.
	 */
	@Override
	public float berechneMaut(int mautAbschnitt, int achszahl, String kennzeichen)
			throws UnkownVehicleException, InvalidVehicleDataException, AlreadyCruisedException {
		if(isFahrzeugImAutomatikverfahren(kennzeichen)){
			if ((achszahl < 5 && getAutomaticAchszahl(kennzeichen) == achszahl) || (achszahl >= 5 && getAutomaticAchszahl(kennzeichen) >= 5)){

			}
			else {
				throw new InvalidVehicleDataException();
			}

		}
		else if (isFahrzeugImManuellenVerfahren(kennzeichen)){
			if ((achszahl < 5 && getAutomaticAchszahl(kennzeichen) == achszahl) || (achszahl >= 5 && getAutomaticAchszahl(kennzeichen) >= 5)){

			}
			else {
				throw new InvalidVehicleDataException();
			}

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
				else {
					return false;
				}
//					throw new UnkownVehicleException();
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
				if(resultSet.next()) {
					return true;
				}
				else {
					return false;
				}
			}
		} catch (SQLException e) {
			L.error("", e);
			throw new DataException();
		}
	}

	private int getAutomaticAchszahl(String kennzeichen){
		var sql = "select MK.ACHSZAHL from FAHRZEUG F " +
				"join FAHRZEUGGERAT FG on F.FZ_ID = FG.FZ_ID " +
				"join MAUTERHEBUNG M on FG.FZG_ID = M.FZG_ID " +
				"join MAUTKATEGORIE MK on M.KATEGORIE_ID = MK. KATEGORIE_ID " +
				"where F.KENNZEICHEN = '" + kennzeichen + "'";
		L.info(sql);

		try (Statement statement = getConnection().createStatement()){
			try (ResultSet resultSet = statement.executeQuery(sql)) {
				if (resultSet.next()) {
					String achszhl = resultSet.getString(1);

					//var a = Integer.parseInt(String.valueOf(achszhl.charAt(achszhl.length()-1)));
					return Integer.parseInt(String.valueOf(achszhl.charAt(achszhl.length()-1)));

				} else {
					throw new UnkownVehicleException();
				}
			}
		} catch (SQLException e) {
			L.error("", e);
			throw new DataException();
		}
	}

	private int getManualAchszahl(String kennzeichen){
		var sql = "select MK.ACHSZAHL from BUCHUNG B " +
				"join MAUTKATEGORIE MK on B.KATEGORIE_ID = MK. KATEGORIE_ID " +
				"where B.KENNZEICHEN = '" + kennzeichen + "' and B.B_ID = 1";
		L.info(sql);

		try (Statement statement = getConnection().createStatement()){
			try (ResultSet resultSet = statement.executeQuery(sql)) {
				if (resultSet.next()) {
					String achszhl = resultSet.getString(1);

					return Integer.parseInt(String.valueOf(achszhl.charAt(achszhl.length()-1)));

				} else {
					throw new UnkownVehicleException();
				}
			}
		} catch (SQLException e) {
			L.error("", e);
			throw new DataException();
		}
	}




}
