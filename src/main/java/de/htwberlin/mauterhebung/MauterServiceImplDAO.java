package de.htwberlin.mauterhebung;

import de.htwberlin.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

public class MauterServiceImplDAO implements IMauterhebung{

    private static final Logger L = LoggerFactory.getLogger(MauterServiceImplDAO.class);
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

        MautabschnittFinder mautabschnittFinder = new MautabschnittFinder(getConnection());
        Mautabschnitt mautabschnitt = mautabschnittFinder.findById(mautAbschnitt);

        FahrzeugFinder fahrzeugFinder = new FahrzeugFinder(getConnection());
        Fahrzeug fahrzeug = fahrzeugFinder.findByKennzeichen(kennzeichen);

        if(fahrzeug != null) {
            fahrzeug.setConnection(getConnection());

            if(compareAchsen(fahrzeug.getACHSEN(), achszahl)){
                var cost = calcMaut(fahrzeug.getMautsatzProKM(), mautabschnitt.getLaengeInKm());

                Mauterhebung mauterhebung = new Mauterhebung(
                        mautabschnitt.getABSCHNITTS_ID(),
                        fahrzeug.getFZG_ID(),
                        fahrzeug.getMautkategorieId(),
                        cost
                );
                mauterhebung.setConnection(getConnection());
                mauterhebung.insert();

                return cost;

            }
            else {
                throw new InvalidVehicleDataException();
            }
        }
        else {
            BuchungFinder buchungFinder = new BuchungFinder(getConnection());
            Buchung buchung = buchungFinder.findByKennzeichenMautAbschnitt(kennzeichen, mautAbschnitt);

            if (buchung != null){
                buchung.setConnection(getConnection());
                if(compareAchsen(buchung.getAchszahl(), achszahl)){

                    var cost = calcMaut(buchung.getMautsatzProKM(), mautabschnitt.getLaengeInKm());

                    buchung.changeStatusToClosed();
                    buchung.update();

                    return cost;
                }
                else {
                    throw new InvalidVehicleDataException();
                }
            }
            else {
                throw new UnkownVehicleException();
            }
        }
    }

    private float calcMaut(float mautsatzProKM, float laenge) {
        return BigDecimal.valueOf(mautsatzProKM * laenge).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    private boolean compareAchsen(int expected, int actual) {
        return ((expected < 5 && actual == expected) || (expected >= 5 && actual >= 5));
    }
}
