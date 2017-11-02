package MSBenchmark.microServices;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author Benjamin Leonhardt
 * 
 * SOAP interface
 * Hieraus können die Stubs zum deployen der Webservices generiert werden.
 * 
 *
 */
@WebService
public interface umdrehenMSInterface {


	/**
	 * Empfängt einen Auftrag zum bearbeiten
	 * 
	 * @param fbatch - Auftrag mit Stapel der gedreht werden soll
	 * @return - String mit dem Inhalt true, wenn alles wie erwartet empfangen wurde
	 */
	@WebMethod
	public String setDatenStapelUMS(Fortschritt fbatch);
	
	/**
	 * Diese Methode erwartet eine ID für einen Auftrag der fertig bearbeitet ist.
	 * Der Auftrag wird aus der Auftragsliste entfernt und in die AbgeschlosseneAuftragsliste
	 * hinzugefügt.
	 * 
	 * @param id - Des fertig bearbeiteten Auftrags
	 * @return - String mit true, wenn alles wie erwartet funktioniert hat.
	 */
	@WebMethod
	public String auftragAbschliessenUMS(String id);
	

}
