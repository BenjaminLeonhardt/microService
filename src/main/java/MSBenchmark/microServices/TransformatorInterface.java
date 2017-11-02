package MSBenchmark.microServices;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author Benjamin Leonhardt
 *
 * SOAP interface
 * Hieraus können die Stubs zum Deployen der Webservices generiert werden.
 * 
 *
 */
@WebService
public interface TransformatorInterface {
	
	/**
	 * Diese Methode empfängt einen Stapel mit Strings und startet einen neuen Thread
	 * damit der Stapel bearbeitet werden kann.
	 * 
	 * @param batch - Stapel mit den Strings die zu verarbeiten sind.
	 * @return - String mit true, wenn alles wie erwartet funktioniert hat.
	 */
	@WebMethod
	public String batchVerarbeitung(ArrayList<String> batch);
	
	/**
	 * Diese Methode empfängt vom UmdrehenMS einen Stapel fertig bearbeiteter Strings
	 * und leitet diesen an die Persistenz weiter.
	 * 
	 * @param batch - Stapel mit den verarbeiteten Strings die weiterzuleiten sind.
	 * @return - String mit true, wenn alles wie erwartet funktioniert hat.
	 */
	@WebMethod
	public String batchWeiterreichen(ArrayList<String> batch);
	
	/**
	 * Diese Methode erwartet eine ID für einen Auftrag der fertig bearbeitet ist.
	 * Der Auftrag wird aus der Auftragsliste entfernt und in die AbgeschlosseneAuftragsliste
	 * hinzugefügt.
	 * 
	 * @param id - Des fertig bearbeiteten Auftrags
	 * @return - String mit true, wenn alles wie erwartet funktioniert hat.
	 */
	@WebMethod
	public String auftragAbschliessen(String id);
	
	/**
	 * Diese Methode empfängt einen Stapel mit ID und Zeiten die während des 
	 * Speicherns in die SQL Datenbank gemessen wurden. Die Zeiten werden der
	 * Liste des Auftrags mit der passenden ID hinzugefügt.
	 * 
	 * @param batchFertig - IDs eines Auftrags mit dessen gemessenen Zeiten
	 * @return - String mit true, wenn alles wie erwartet funktioniert hat.
	 */
	@WebMethod
	public String batchSpeichernFertig(ArrayList<String> batchFertig);

	
}
