package MSBenchmark.microServices;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Benjamin Leonhardt
 *
 * Dieses Interface wird für SOAP benötigt. Die Annotation @WebInterface teilt Java mit, dass es sich um einen SOAP-Webservice handelt.
 * 
 */
@WebService
public interface PersistenzInterface {

	/**
	 * Die Methode getDatenBatch übermittelt einen Auftrag vom Typ Fortschritt an den Persistenz-Service.
	 * Dieser beinhaltet alle Daten, die zum abarbeiten nötig sind.
	 * 
	 * @param f - Auftrag, der bearbeitet werden soll
	 * @return - Gibt einen String mit true zurück, wenn der Auftrag angekommen ist und abgearbeitet wird.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	@WebMethod
	public String getDatenBatch(Fortschritt f) throws ClassNotFoundException, SQLException;

	/**
	 * Die Methode setDatenBatch wird zum speichern verwendet. Sobald ein Stapel mit Strings verarbeitet wurde, wird ihm 
	 * über diese Methode an den Persistenz-Service übergeben.
	 * 
	 * @param batch - Der verarbeitete Stapel. ist eine ArrayList gefüllt mit Strings.
	 * @return - Gibt einen String mit true zurück, wenn ein Stapel angekommen ist und abgearbeitet wird.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	@WebMethod
	public String setDatenBatch(ArrayList<String> batch) throws ClassNotFoundException, SQLException;
	
	/**
	 * Die Methode getStatus wird aufgerufen, wenn vom Benutzer der Status zu einem oder allen laufenden Aufträgen abgefragt wird.
	 * Dabei wird zuerst in den Transformator-Service in entweder getFortschritt, getProzesse oder getFertigeProzesse aufgerufen, welcher daraufhin
	 * den Status des Auftrages über die Methode getStatus des Persistenz-Service abfragt.
	 * 
	 * @param id - Id des Auftrages der abgefragt wird.
	 * @return - Ein String, welcher die Zahl des aktuellen Fortschritt der SQL-Datenbank enthält. Wird als JSON Objekt versandt
	 */
	@WebMethod
	public String getStatus(@QueryParam("id") String id);
}
