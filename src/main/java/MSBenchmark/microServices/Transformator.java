package MSBenchmark.microServices;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import grpcServices.FortschrittRequest;
import grpcServices.PersistenzGrpc;
import grpcServices.TransformatorGrpc;
import grpcServices.UmdrehenMSGrpc;
import grpcServices.StapelMsg;
import grpcServices.StringMsg;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import MSBenchmark.microServices.SOAPStubs.persistenz.*;

//http://localhost:8080/microServices/TransformatorService?wsdl
/**
 * @author Benjamin Leonhardt
 * 
 * Die Klasse Transformator erstellt neue Aufträge und dient zur Kommunikation zwischen den Services Persistenz und umdrehenMS 
 * Wenn ein Auftrag ohne den umdrehenMS Service erledigt werden soll, dreht der Transformator Service die Strings selbst um.
 *
 */
@WebService(endpointInterface = "MSBenchmark.microServices.TransformatorInterface", serviceName = "TransformatorService")
@Path("Transformator")
public class Transformator extends TransformatorGrpc.TransformatorImplBase implements TransformatorInterface, Runnable {
	
	public static boolean debugLokal=true;

	private static final String ipPersistenz 				= "127.0.0.1";
	private static final String ipUmdrehenMS				= "127.0.0.1";
	private static final String ExterneIpTransformator 		= "127.0.0.1";
	private static final String ExternerPortTransformator 	= "8080";
	
//	private static final String ipPersistenz 				= "10.15.252.104";
//	private static final String ipUmdrehenMS				= "10.15.255.52";
//	private static final String ExterneIpTransformator 		= "35.187.72.76";
//	private static final String ExternerPortTransformator 	= "30759";
	
	//REST Adressen Persistenz
	private static final String RESTIPPersistenz			= ipPersistenz;
	private static final String RESTportPersiszenz			= debugLokal?"8080":"18080";
	private static final String RESTbaseUrlPersiszenz		= "http://"+RESTIPPersistenz+":"+RESTportPersiszenz;				//REST Adresse und Port für den UmdrehenMS Service
	
	private static final String RESTURLinitGrpcPersistenz 	= "/microServices/webapi/persistenzBA/initGrpc";					//REST Adresse zum initialisieren des gRPC Servers des Persistenz Service
	private static final String RESTURLsqlStatus 			= "/microServices/webapi/persistenzBA/sqlStatus";					//REST Adresse der Methode sqlStatus des Persistenz Service
	private static final String RESTURLanfordernBatch 		= "/microServices/webapi/persistenzBA/anfordernBatch";				//REST Adresse der Methode anfordernBatch des Persistenz Service
	private static final String RESTspeichernBatch 			= "/microServices/webapi/persistenzBA/speichernBatch";				//REST Adresse der Methode speichernBatch des Persistenz Service
	
	//REST Adressen UmdrehenMS
	private static final String RESTIPUmdrehenMS			= ipUmdrehenMS;
	private static final String RESTportUmdrehenMS			= debugLokal?"8080":"38080";
	private static final String RESTbaseUrlUmdrehenMS		= "http://"+RESTIPUmdrehenMS+":"+RESTportUmdrehenMS;				//REST Adresse und Port für den UmdrehenMS Service
	private static final String RESTURLinitGrpcUmdrehenMS 	= "/microServices/webapi/umdrehenMS/initGrpc";						//REST Adresse zum initialisieren des gRPC Servers des UmdrehenMS Service
	private static final String RESTURLumdrehenMSStapel 	= "/microServices/webapi/umdrehenMS/stapel";						//REST Adresse der Methode Stapel des umdrehenMS Service
	private static final String RESTmsStatus 				= "/microServices/webapi/umdrehenMS/msUmdrehenStatus";				//REST Adresse der Methode msUmdrehenStatus des UmdrehenMS Service
	private static final String RESTabschliessen 			= "/microServices/webapi/umdrehenMS/auftragAbschliessen";			//REST Adresse der Methode speichernBatch des Persistenz Service
	
	//SOAP Adressen Persistenz
	private static final String ServiceBaseURL 				= "http://microServices.MSBenchmark/";								//SOAP ServiceBaseURL. Entspricht dem Package Namen in umgekehrter Reihenfolge

	private static final String SOAPIPPersistenz			= ipPersistenz;
	private static final String SOAPPortPersistenz			= debugLokal?"8080":"18080";
	private static final String SOAPbaseUrlPersistenz		= "http://"+SOAPIPPersistenz+":"+SOAPPortPersistenz;				//SOAP Adresse und Port für den Persistenz Service
	private static final String SOAPPersistenzServiceURL 	= SOAPbaseUrlPersistenz + "/microServices/PersistenzService?wsdl";	//SOAP Adresse des Persistenz Service
//	private static final String SOAPPersistenzServiceURL 	= "file:/opt/jboss/wildfly/standalone/PersistenzService.xml";		//Angepasste WSDL Datei für Container
	private static final String SOAPPersistenzServiceName 	= "PersistenzService";												//SOAP Service Name des Persistenz Service
	
	//SOAP Adressen UmdrehenMS
	private static final String SOAPIPUmdrehenMS			= ipUmdrehenMS;
	private static final String SOAPPortUmdrehenMS			= debugLokal?"8080":"38080";
	private static final String SOAPbaseUrlUmdrehenMS		= "http://"+SOAPIPUmdrehenMS+":"+SOAPPortUmdrehenMS;				//SOAP Adresse und Port für den UmdrehenMS Service
	private static final String SOAPUmdrehenMSServiceURL 	= SOAPbaseUrlUmdrehenMS + "/microServices/umdrehenMSService?wsdl";	//SOAP Adresse des UmdrehenMS Service
//	private static final String SOAPUmdrehenMSServiceURL 	= "file:/opt/jboss/wildfly/standalone/umdrehenMSService.xml";		//Angepasste WSDL Datei für Container
	private static final String SOAPUmdrehenMSServiceName 	= "umdrehenMSService";												//SOAP Service Name des UmdrehenMS Service

	//gRPC Server und Port für den Transformator Service
	private static Server gRPCServerTransformator;																				//gRPC Server für die Verbindung zum Persistenz-Service 
	private static final int gRPCServerPortTransformator	= 20051;															//Port zum Persistenz-Service

	//gRPC Adressen zum Persistenz Service
	private static final String gRPCIPPersistenz 			= "127.0.0.1";														//IP-Adresse zum Persistenz-Service
//	private static final String gRPCIPPersistenz 			= "10.15.247.0";
	private static final int gRPCChannelPortPersistenz 		= 10051;															//Port zum Persistenz-Service
	
	//gRPC Adressen zum Umdrehen Service
	private static final String gRPCIPUmdrehenMS 			= "127.0.0.1";														//IP-Adresse zum UmdrehenMS-Service
//	private static final String gRPCIPUmdrehenMS 			= "10.15.242.87";
	private static final int gRPCChannelPortUmdrehenMS 		= 30051;															//Port zum UmdrehenMS-Service
	
	private boolean initGrpc 								= false;															//Hilfsvariable zum Starten des gRPC Servers
	private grpcServices.StapelMsg request;																						//Lokale Variable um eine StapelMsg zwischen speichern zu können

	//Globale Listen und lokale Variablen und Locks
	private static ArrayList<FortschrittgRPC> Auftragsliste;																	//Globale Liste für alle Aufträge
	private static ArrayList<FortschrittgRPC> AbgeschlosseneAuftraege;															//Globale Liste für alle fertig bearbeiteten Aufträgen

	private static Lock lockAuftragsliste 					= new ReentrantLock();												//Lock für den gemeinsamen Zugriff auf die Auftragsliste fortschritt
	private static Lock lockFertigeAuftragsliste			= new ReentrantLock();												//Lock für den gemeinsamen Zugriff auf die Auftragsliste fortschritt
	
	private FortschrittgRPC aktuellerAuftrag;																					//Lokale Variable für einen Auftrag. Wird zum arbeiten mit dem Auftrag verwendet

	private String proto 									= "";																//Hilfsvariable für die Ausgabe des Protokolls als String 
	private String parameter 								= "";																//Hilfsvariable für die Ausgabe ob mit oder ohne UmdrehenMS gearbeitet wird als String

	private static final int REST = 1, SOAP = 2, gRPCSingle = 3, gRPCStream = 4;												//Hilfsvariablen zur besseren Lesbarkeit des Quellcodes
	private static final int ohneTransformator = 1, mitTransformator = 2;
	
	private static final Logger logger 						= Logger.getLogger(Transformator.class.getName());
	
	private static boolean testLaeuft = false;
	
	/**
	 * Standard Konstruktor
	 * 
	 * Initialisiert die Listen, sollten diese leer sein
	 */
	public Transformator() {
		logger.setLevel(Level.ALL);
		if (Auftragsliste == null) {
			Auftragsliste = new ArrayList<FortschrittgRPC>();
		}
		if (AbgeschlosseneAuftraege == null) {
			AbgeschlosseneAuftraege = new ArrayList<FortschrittgRPC>();
		}
	}

	/**
	 * Dieser Konstruktor wird verwendet wenn ein neuer Auftrag erstellt wird
	 * 
	 * @param f - Neuer Auftrag. Wird lokal zwischengespeichert. danach wird die run()-Methode aufgerufen
	 */
	public Transformator(FortschrittgRPC f) {
		this.aktuellerAuftrag = f;
	}

	/**
	 * Dieser Konstruktor wird aufgerufen, wenn ein schon existierender Auftrag weiter bearbeitet wird.
	 * 
	 * @param id - ID des zu bearbeitenden Auftrags
	 */
	public Transformator(long id) {
		auftragMitIDSuchen(id);
	}

	private void auftragMitIDSuchen(long id) {
		//Auftrag wird in der globalen Liste gesucht und lokal zwischengespeichert danach wird die run()-Methode aufgerufen
		for (int i = 0; i < Auftragsliste.size(); i++) {
			lockAuftragsliste.lock();
			if (Auftragsliste.get(i).getStartZeit() == id) {
				this.aktuellerAuftrag = Auftragsliste.get(i);
				lockAuftragsliste.unlock();
				break;
			}
			lockAuftragsliste.unlock();
		}
	}

	/**
	 * Dieser Konstruktor wird aufgerufen, wenn der gRPC Server initialisiert werden soll.
	 * 
	 * @param initGrpc
	 */
	public Transformator(boolean initGrpc) {
		this.initGrpc=initGrpc;
	}
	
	/**
	 * Die Methode sendInitGrpc() sendet an die anderen beiden Microservices eine REST Nachricht, dass diese ihren gRPC Server starten sollen.
	 * Es werden auch noch 2 Threads gestartet, welche 2 gRPC Server für die Kommunikation zu den beiden anderen Microservices ermöglichen.
	 * 
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/initGRPC")
	private String sendInitGrpc() {
		if (gRPCServerTransformator == null ) {
			//Erst werden die beiden Anfragen an die Persistenz und UmdrehenMS Service gesendet
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
			String resp = restTarget.path(RESTURLinitGrpcPersistenz).request(MediaType.APPLICATION_JSON + "; charset=UTF-8").get(String.class);
			if (resp.equals("true")) {
				logger.info("gRPC-Persistenzserver gestartet!");
				resp = "";
			}else{
				logger.severe("FEHLER: Persistenz hat false als Response, beim Starten des gRPC Servers zurückgesendet.");
			}
			restTarget = restClient.target(RESTbaseUrlUmdrehenMS);
			resp = restTarget.path(RESTURLinitGrpcUmdrehenMS).request(MediaType.APPLICATION_JSON + "; charset=UTF-8").get(String.class);
			if (resp.equals("true")) {
				logger.info("gRPC-UmdrehenMSserver gestartet!");
			}else{
				logger.severe("FEHLER: UmdrehenMS hat false als Response, beim Starten des gRPC Servers zurückgesendet.");
			}
			
			//Danach werden 2 Threads gestartet, die die Server des Transformator Services starten
			Thread threadFuerTransformatorServer = new Thread(new Transformator(true));
			threadFuerTransformatorServer.start();

			while (gRPCServerTransformator == null) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.info("gRPC-Transformatorserver gestartet!");
			System.out.println("gRPC-Transformatorserver gestartet!");
		}
		return "Server werden gestartet.";
	}

	/**
	 * Methode zum Starten der gRPC Server
	 */
	private void initGRPC() {
		if (gRPCServerTransformator == null) {
			try {
				gRPCServerTransformator = ServerBuilder.forPort(gRPCServerPortTransformator).addService(new Transformator()).build().start();
				gRPCServerTransformator.awaitTermination();
			} catch (InterruptedException | IOException e) {
				logger.severe("FEHLER: Beim Starten des gRPC Servers des Transformator Service");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Empfängt einen Auftrag über REST als GET-Anfrage.
	 * 
	 * @param stapelgroesse - Die Größe eines zu verarbeitenden Stapels
	 * @param von - Startindex aus der die Persistenz von der SQL Datenbank beginnen soll
	 * @param bis - Endindex bis zu der die Persistenz in der SQL Datenbank lesen soll
	 * @param titel - Titel des Auftrags 
	 * @param protokoll - Mit welchem Protokoll kommuniziert werden soll
	 * @param para - Sagt ob mit oder ohne umdrehenMS Service verarbeitet werden soll
	 * @return - Ein String der eine HTML Seite beinhaltet
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/init")
	public String init(@QueryParam("stapelgroesse") String stapelgroesse, 
			@QueryParam("bis") String bis, @QueryParam("titel") String titel, @QueryParam("protokoll") String protokoll,
			@QueryParam("parameter") String para) throws SQLException, MalformedURLException {
		//Fehler abfangen. Sollte ein Parameter leer sein wird ein Fehler zurück gegeben
		if (stapelgroesse.equals("") || bis.equals("") || protokoll.equals("") || para.equals("")) {
			logger.warning("Neuen Auftrag mit leerem Argument erhalten");
			return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
					+ "Es wurden nicht alle Felder Ausgefüllt." + "</body>\n</html>";
		}
		
		//gRPC Server initialisieren sofern das nicht schon getan wurde
		sendInitGrpc();
		
		//Erstellen des Objekts für den Auftrag mit Fehlerausgabe
		FortschrittgRPC neuerAuftrag;
		int v, b, groesse;
		try {
			v = 0;
			b = Integer.parseInt(bis);
			if (b < v) { //Wenn bis größer ist als von werden diese vertauscht
				int tmp = v;
				v = b;
				b = tmp;
			}
			groesse = Integer.parseInt(stapelgroesse);
			if (groesse > b - v) { //Wenn die Stapelgröße größer ist als die Gesamtgröße wird sie auf die Gesamtgröße reduziert
				groesse = b - v;
			}
			if (groesse <= 0) { //Wenn die Größe kleiner 0 ist, wird ein Fehler zurück gegeben
				logger.warning("Neuen Auftrag negativer Stapelanzahl erhalten");
				return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
						+ "Stapelgröße ist zu klein! Minimum ist 1." + "</body>\n</html>";
			}

			if (v > 1000000 || b > 1000000 || v < 0 || b < 0) {//Wenn von oder bis größer 1.000.000 oder kleiner 0 ist, wird ein Fehler zurückgegeben
				logger.warning("Neuen Auftrag falschen Angaben bei von und bis erhalten");
				return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
						+ "Start oder Endindex sind ausserhalb der Range! Gültige Werte sind von 0 bis 1 000 000."
						+ "</body>\n</html>";
			}
			
			if (!para.equals("1") && !para.equals("2")) { //Wenn der Parameter weder 1 noch 2 ist, wird ein Fehler zurückgegeben
				logger.warning("Neuen Auftrag mit falscher Angabe bei Parameter erhalten");
				return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
						+ "Parameter falsch gesetzt. \n\n" + "Mögliche Eingabe ist: \n"
						+ "1 für Transformator verarbeitet die Daten selbst.\n "
						+ "2 für Umdrehservice verarbeitet die Daten" + "</body>\n</html>";
			}
			if (!protokoll.equals("1") && !protokoll.equals("2") && !protokoll.equals("3")) {//Wenn das Protokoll weder 1,2 noch 3 ist, wird ein Fehler zurückgegeben
				logger.warning("Neuen Auftrag mit falscher Angabe bei Protokoll erhalten");
				return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
						+ "Protokoll falsch. \n\n" + "Mögliche Eingabe ist: \n" + "1 für REST.\n " + "2 für SOAP.\n "
						+ "3 für gRPC. \n" + "</body>\n</html>";
			}
			//Wenn alle Checks erfolgreich passiert sind, wird das Auftragsobjekt erstellt
			neuerAuftrag = new FortschrittgRPC(Integer.parseInt(para), v, b, System.currentTimeMillis(), titel,
					Integer.parseInt(protokoll), groesse);
		} catch (NumberFormatException e) {//Wird geworfen, wenn einer der Strings nicht in eine Zahl umgewandelt werden konnte
			return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Eingabefehler</title></head>\n<body><h2>Eingabefehler</h2>"
					+ "Es müssen Zahlen für die Start, Endindex und Stapelgröße angegeben werden. Gültige Werte sind von 0 bis 1 000 000."
					+ "</body>\n</html>";
		}
		
		//Auftrag wird der Auftragsliste hinzugefügt. Neuer Thread wird gestartet und Bestätigung wird zurückgegeben
		auftragZurListeHinzufuegen(neuerAuftrag);
		Thread threadZumAuftragStarten = new Thread(new Transformator(neuerAuftrag));
		threadZumAuftragStarten.start();
		
		if (neuerAuftrag.getParameter() == ohneTransformator ) {
			parameter = "ohne UmdrehenMS";
		} else if (neuerAuftrag.getParameter() == mitTransformator) {
			parameter = "mit UmdrehenMS";
		} 

		if (neuerAuftrag.getProtokoll() == REST) {
			proto = "REST";
		} else if (neuerAuftrag.getProtokoll() == SOAP) {
			proto = "SOAP";
		} else if (neuerAuftrag.getProtokoll() == gRPCSingle) {
			proto = "gRPC";
		}
		logger.info("Neuen Auftrag erhalten: Protokoll: "+ proto + " "+ parameter + " " + (b - v) + " Datensätze");
		//Seite mit Daten des gestarteten Auftrags werden zurück gesendet
		return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Auftrag wird bearbeitet</title></head>\n<body>"
				+ "<h2>Der Prozess " + neuerAuftrag.getTitel() + " wurde gestartet.\n\n ProzessID ist " +(Auftragsliste.indexOf(neuerAuftrag) + 1) + "</h2>\n"
				+ "Protokoll ist: " + proto + ". Es wird " + parameter + " übertragen. <br>"
				+ "Es werden " + (b - v) + " Datensätze bearbeitet.\n\n<p>"
				+ "Um den Bearbeitungsfortschritt zu erhalten können Sie folgenden Link verwenden:\n"
				+ "<a href=\"http://"+ExterneIpTransformator+":"+ExternerPortTransformator+"/microServices/webapi/Transformator/fortschritt?ProzessID="
				+ (Auftragsliste.indexOf(neuerAuftrag) + 1) + "\">Hier klicken</a><br><br><br><br><br>"
				+ "<h3>Statusanzeigen:</h3>" 
				+ "<a href=\"http://"+ExterneIpTransformator+":"+ExternerPortTransformator+"/microServices/webapi/Transformator/aktiveProzesse\">Liste aller aktiven Prozesse</a>" 
				+ "<br> <a href=\"http://"+ExterneIpTransformator+":"+ExternerPortTransformator+"/microServices/webapi/Transformator/fertigeProzesse\">Liste aller fertigen Prozesse</a>\n</body>\n</html>";
	}

	/**
	 * Die Methode auftragZurListeHinzufuegen() fügt wie der Name schon sagt einen neuen Auftrag der globalen Auftragsliste hinzu
	 * 
	 * @param neuerAuftrag - Der neue Auftrag der hinzugefügt werden soll
	 */
	private void auftragZurListeHinzufuegen(FortschrittgRPC neuerAuftrag) {
		//Liste wird sortiert
		lockAuftragsliste.lock();
		Collections.sort(Auftragsliste, new Comparator<Fortschritt>() {
			@Override
			public int compare(Fortschritt f1, Fortschritt f2) {
				if (f1.getId() > f2.getId()) {
					return 1;
				} else if (f1.getId() < f2.getId()) {
					return -1;
				}
				return 0;
			}
		});
		lockAuftragsliste.unlock();
		//Auftrag bekommt eine Auftragsnummer
		lockAuftragsliste.lock();
		boolean gefunden = false;
		for (int i = 0; i < Auftragsliste.size(); i++) {
			if (Auftragsliste.get(i).getId() != i) {
				neuerAuftrag.setId(i);
				gefunden = true;
				break;
			}
		}
		if (gefunden == false) {
			neuerAuftrag.setId(Auftragsliste.size());
		}
		lockAuftragsliste.unlock();
		//Auftrag wird der Liste hinzugefügt und neu sortiert
		lockAuftragsliste.lock();
		Auftragsliste.add(neuerAuftrag);

		Collections.sort(Auftragsliste, new Comparator<Fortschritt>() {
			@Override
			public int compare(Fortschritt f1, Fortschritt f2) {
				if (f1.getId() > f2.getId()) {
					return 1;
				} else if (f1.getId() < f2.getId()) {
					return -1;
				}
				return 0;
			}
		});
		lockAuftragsliste.unlock();
	}

	/**
	 * Die Methode getFortschritt() gibt den aktuellen Status eines Auftrags zurück
	 * 
	 * @param id - Die Auftragsnummer des abgefragten Auftrags
	 * @return - Gibt einen String als HTML-Dokument zurück mit allen Daten des Auftrags
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/fortschritt")
	public String getFortschritt(@QueryParam("ProzessID") String id) {
		//Client für REST-Anfrage an Persistenz und umdrehenMS Service wird initialisiert sowie Übergabeparameter wird in eine Integer-Variable gecastet
		Client restClient = ClientBuilder.newClient();
		WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
		int i = Integer.parseInt(id) - 1;
		int sqlResp = 0;
		
		//Wenn i out of Bounds oder kleiner 0 oder die Auftragsliste leer ist wird ein Fehler zurückgegeben
		if (i > Auftragsliste.size() - 1 || i < 0 || Auftragsliste.size() == 0) {
			return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Auftrag nicht gefunden</title></head>\n<body>"
					+ "<h2>Auftragdetails</h2>Leider kein Auftrag mit der ID:" + id + " gefunden werden";
		}
		
		//Strings für die Ausgabe werden erstellt
		stringProtoPara(i, true);
		if (Auftragsliste.get(i).isAlsStapel()) {//TODO isStapel wird nicht mehr verwendet und umdrehenMS ist noch nicht drin
			String resp = restTarget.path(RESTURLsqlStatus).queryParam("id", i).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
					.post(Entity.entity(i, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
			sqlResp = Integer.parseInt(resp);
			return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Auftrag wird bearbeitet</title>"
					+ "</head>\n<body>" + "<h2>Auftragdetails</h2>" + "Prozess: " + Auftragsliste.get(i).getTitel()
					+ " &nbsp;&nbsp;\tProzessID: " + (Auftragsliste.get(i).getId() + 1) + "<br><br>\n\n" + "Parameter: "
					+ parameter + " &nbsp;&nbsp; Protokoll: " + proto + " verarbeitet\n<br>" + "Von:"
					+ Auftragsliste.get(i).getVon() + " Bis:" + Auftragsliste.get(i).getBis() + " Gesammt:"
					+ (Auftragsliste.get(i).getBis() - Auftragsliste.get(i).getVon()) + " Datensätzen wurden "
					+ Auftragsliste.get(i).getGerade() + " verarbeitet\n<br>" + "Auftrag zu "
					+ ((Auftragsliste.get(i).getGerade() + Integer.parseInt(resp) * 100)
							/ ((Auftragsliste.get(i).getBis() - Auftragsliste.get(i).getVon()) * 3))
					+ "% abgeschlossen <br><br>" + Auftragsliste.get(i).timeToString() + "</body>\n</html>";
		} else {
			return "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Auftrag wird bearbeitet</title></head>\n<body>"
					+ "<h2>Auftragdetails</h2>" + "Prozess: " + Auftragsliste.get(i).getTitel()
					+ " &nbsp;&nbsp;\tProzessID: " + (Auftragsliste.get(i).getId() + 1) + "<br><br>\n\n" + "Parameter: "
					+ parameter + " &nbsp;&nbsp; Protokoll: " + proto + " verarbeitet\n<br>" + "Von:"
					+ Auftragsliste.get(i).getVon() + " Bis:" + Auftragsliste.get(i).getBis() + " Gesammt:"
					+ (Auftragsliste.get(i).getBis() - Auftragsliste.get(i).getVon()) + " Datensätzen wurden "
					+ Auftragsliste.get(i).getGerade() + " verarbeitet\n<br>" + "Auftrag zu "
					+ ((Auftragsliste.get(i).getGerade() * 100)
							/ (Auftragsliste.get(i).getBis() - Auftragsliste.get(i).getVon()))
					+ "% abgeschlossen <br><br>" + Auftragsliste.get(i).timeToString() + "</body>\n</html>";
		}
	}

	/**
	 * Diese Methode wird durch eine REST-GET-Anfragt gestartet und gibt eine Liste aller aktiven Aufträge als HTML-Dokument zurück
	 * 
	 * @return - String mit Liste aller aktiven Aufträge als HTML-Dokument
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/aktiveProzesse")//TODO Prozesse zu Aufträge abändern
	public String getProzesse() {
		//Client für REST-Anfrage an Persistenz und umdrehenMS Service wird initialisiert
		Client restClient = ClientBuilder.newClient();
		WebTarget restTargetPersistenz = restClient.target(RESTbaseUrlPersiszenz);
		WebTarget restTargetUmdrehenMS = restClient.target(RESTbaseUrlUmdrehenMS);
		//Dem Rückgabe-String wird die Überschrift hinzugefügt, der String resp ist für die Responsen der Anfragen an die anderen 2 Services. 
		//Das SimpleDateFormat ist für die Ausgabe der Start sowie Endzeit. sqlResp bekommt den gecasteten Wert der Antwort der Anfrage an die Persistenz
		String listeAllerAktiveAuftraege = "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Liste aller aktiven Prozesse</title></head>\n<body><h2>Liste aller aktiven Prozesse</h2>";
		String response = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		int sqlResp;
		
		//Es wird jeder Auftrag in der Liste durchgegangen
		for (int i = 0; i < Auftragsliste.size(); i++) {
			//Die Strings für Protokoll und Parameter werden geladen
			lockAuftragsliste.lock();
			stringProtoPara(i, true);
			aktuellerAuftrag = Auftragsliste.get(i);
			lockAuftragsliste.unlock();
			//Anfrage an die Persistenz wird gesendet
			if (aktuellerAuftrag.getSqlGerade() != ((aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) * 2)) {//TODO kann evtl weg
				response = restTargetPersistenz.path(RESTURLsqlStatus).queryParam("id", aktuellerAuftrag.getStartZeit()).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
						.post(Entity.entity(aktuellerAuftrag.getStartZeit(), MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
				aktuellerAuftrag.setSqlGeradeSpeichern(Integer.parseInt(response));
				sqlResp = aktuellerAuftrag.getSqlGeradeSpeichern();
			} else {
				sqlResp = aktuellerAuftrag.getSqlGerade();
			}
			//Wenn mit UmdrehenMS Service gearbeitet wird, wird auch dort eine Anfrage gesendet
			if (aktuellerAuftrag.isTransformatorMS()) {
				if (aktuellerAuftrag.getMsUmdrehenGerade() != ((aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()))) {
					response = restTargetUmdrehenMS.path(RESTmsStatus).queryParam("id", aktuellerAuftrag.getStartZeit()).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
							.post(Entity.entity(aktuellerAuftrag.getStartZeit(), MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
					aktuellerAuftrag.setMsUmdrehenGerade(Integer.parseInt(response));
				} else {
					response = String.valueOf(aktuellerAuftrag.getMsUmdrehenGerade());
				}
				//Dem String listeAllerAktiveAuftraege werden alle Daten hinzugefügt
				listeAllerAktiveAuftraege += "****************************************************************************<br>\n";
				listeAllerAktiveAuftraege += "Prozess: " + aktuellerAuftrag.getTitel() + " &nbsp;&nbsp;\tProzessID: " + (aktuellerAuftrag.getId() + 1) + "<br><br>\n\n"
						+ "Parameter: " + parameter + " &nbsp;&nbsp; Protokoll: " + proto + "\n<br>";
				listeAllerAktiveAuftraege += "gerade: " + response + " sqlgerade: " + sqlResp + " anzahl: " + (aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) + " zu "
						+ ((sqlResp + Integer.parseInt(response)) * 100) / ((aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) * 3)
						+ "% abgeschlossen\n<br>";
				listeAllerAktiveAuftraege += "StartZeit: " + dateFormat.format(aktuellerAuftrag.getStartZeit()) + " Seither verstrichen: "
						+ aktuellerAuftrag.zeitUmwandeln((System.currentTimeMillis() - aktuellerAuftrag.getStartZeit())*1000000) + " \n<br>";
				listeAllerAktiveAuftraege += aktuellerAuftrag.timeToString();
			} else {
				//Dem String listeAllerAktiveAuftraege werden alle Daten hinzugefügt
				listeAllerAktiveAuftraege += "****************************************************************************<br>\n";
				listeAllerAktiveAuftraege += "Prozess: " + aktuellerAuftrag.getTitel() + " &nbsp;&nbsp;\tProzessID: " + (aktuellerAuftrag.getId() + 1) + "\n\n<br><br>"
						+ "Parameter: " + parameter + " &nbsp;&nbsp; Protokoll: " + proto + " \n<br>";
				listeAllerAktiveAuftraege += "gerade: " + aktuellerAuftrag.getGerade() + " sqlgerade: " + response + " anzahl: " + (aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon())
						+ " zu " + (((aktuellerAuftrag.getGerade() + sqlResp) * 100) / ((aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) * 3))
						+ "% abgeschlossen \n<br>";
				listeAllerAktiveAuftraege += "StartZeit: " + dateFormat.format(aktuellerAuftrag.getStartZeit()) + " Seither verstrichen: "
						+ aktuellerAuftrag.zeitUmwandeln((System.currentTimeMillis() - aktuellerAuftrag.getStartZeit())*1000000) + " \n<br>";
				listeAllerAktiveAuftraege += aktuellerAuftrag.timeToString();
			}

		}
		//HTML wird geschlossen und zurückgesandt
		return listeAllerAktiveAuftraege += "</body>\n</html>";
	}

	/**
	 * Diese Methode wird durch eine REST-GET-Anfrage gestartet und gibt eine Liste aller fertig bearbeiteten Aufträgen zurück
	 * 
	 * @return - String mit Liste aller fertig bearbeiteten Aufträge als HTML-Dokument
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/fertigeProzesse")
	public String getFertigeProzesse() {//TODO Prozesse in Aufträge umwandeln
		//String und SimpleDateFormat wird initialisiert
		String listeAllerFertigenAuftraege = "<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"><title>Liste aller fertigen Prozesse</title></head>\n<body><h2>Liste aller fertigen Prozesse</h2>";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		//Danach wird durch die Liste iteriert und für jeden abgeschlossenen Auftrag ein Eintrag in der Tabelle des Ausgabe HTML-Dokuments hinzugefügt
		for (int i = 0; i < AbgeschlosseneAuftraege.size(); i++) {
			lockFertigeAuftragsliste.lock();
			stringProtoPara(i, false);

			listeAllerFertigenAuftraege += "****************************************************************************<br>\n";
			listeAllerFertigenAuftraege += "Prozess: " + AbgeschlosseneAuftraege.get(i).getTitel() + "<br>\n<br>\n" + "Parameter: " + parameter
					+ " &nbsp;&nbsp; Protokoll: " + proto + "\n<br>";
			listeAllerFertigenAuftraege += "Von:" + AbgeschlosseneAuftraege.get(i).getVon() + " Bis:" + AbgeschlosseneAuftraege.get(i).getBis() + " Gesammtmenge: "
					+ (AbgeschlosseneAuftraege.get(i).getBis() - AbgeschlosseneAuftraege.get(i).getVon()) + " \n<br>\n<br>";
			listeAllerFertigenAuftraege += "StartZeit: " + dateFormat.format(AbgeschlosseneAuftraege.get(i).getStartZeit());
			listeAllerFertigenAuftraege += " EndZeit: " + dateFormat.format(AbgeschlosseneAuftraege.get(i).getEndZeit()) + " Gesammtzeit: "
					+ AbgeschlosseneAuftraege.get(i).zeitUmwandeln((AbgeschlosseneAuftraege.get(i).getEndZeit() - AbgeschlosseneAuftraege.get(i).getStartZeit())*1000000) + "\n<br>";

			listeAllerFertigenAuftraege += AbgeschlosseneAuftraege.get(i).timeToString();
			lockFertigeAuftragsliste.unlock();
		}
		//HTML wird geschlossen und zurückgesandt
		return listeAllerFertigenAuftraege += "</body>\n</html>";
	}

	/**
	 * Diese Methode wandelt die Variablen Parameter und Protokoll der Aufträge in Strings für die Ausgabe um.
	 * 
	 * @param index - Index des Elements das Bearbeitet werden soll
	 * @param welcheListe - Entscheidet ob die Auftragsliste oder die AbgeschlosseneAuftragsliste verwendet werden soll
	 */
	public void stringProtoPara(int index, boolean welcheListe) {
		ArrayList<FortschrittgRPC> tmp;
		if (welcheListe == true) {
			tmp = Auftragsliste;
		} else {
			tmp = AbgeschlosseneAuftraege;
		}
		if (tmp.get(index).getParameter() == ohneTransformator) {
			parameter = "Ohne UmdrehenMS";
		} else if (tmp.get(index).getParameter() == mitTransformator) {
			parameter = "Mit UmdrehenMS";
		} 

		if (tmp.get(index).getProtokoll() == REST) {
			proto = "REST";
		} else if (tmp.get(index).getProtokoll() == SOAP) {
			proto = "SOAP";
		} else if (tmp.get(index).getProtokoll() == gRPCSingle) {
			proto = "gRPC";
		}
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/batchVerarbeitung")
	public String batchVerarbeitung(ArrayList<String> stapel) {
		
		//Ermitteln der ID des empfangenen Auftrags.
		long transformatorBatchErhalten=System.nanoTime();
		long id = Long.parseLong(stapel.get(stapel.size() - 3));

		//Auftrag suchen und lokal zwischenspeichern
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(id);
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode batchVerarbeitung für REST/SOAP"
					+ " des Transformator Service, nicht gefunden werden!");
			return "false";
		}
		logger.info("Für Auftrag " + lokalerAuftrag.getId() 
			+ " mit " + (lokalerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") 
			+ " Stapel zur verarbeitung erhalten");
		//Daten und Stapel hinzufügen
		lokalerAuftrag.getVorSQLDatenHolen().add(Long.parseLong(stapel.get(stapel.size() - 2)));
		lokalerAuftrag.getNachSQLDatenHolen().add(Long.parseLong(stapel.get(stapel.size() - 1)));
		lokalerAuftrag.setStapel(stapel);
		lokalerAuftrag.getTransformatorBatchErhalten().add(transformatorBatchErhalten);
		
		for(int i=0;i<3;i++){
			stapel.remove(stapel.size() -1);
		}

		//Thread zum bearbeiten des Stapels starten
		Thread t = new Thread(new Transformator(id));
		t.start();
		
		return "true";
	}

	/**
	 * Diese Methode Prüft ob der Stapel an den UmdrehenMS Service gesandt wird, oder ob der Transformator Service diesen selbst drehen soll.
	 * 
	 * @return - Gibt einen String mit dem Inhalt "true" zurück wenn alles geklappt hat.
	 */
	public String batchVerarbeitungM() {

		try {
			//Fügt die Zeit der Liste hinzu und initialisiert den StapelGedreht, damit die Liste leer ist.
			String response = "";
			aktuellerAuftrag.getVorTransformator().add(System.nanoTime());
			if (aktuellerAuftrag.getStapelGedreht().size() != 0) {
				ArrayList<String> tmpStapel = new ArrayList<String>();
				aktuellerAuftrag.setStapelGedreht(tmpStapel);
			}
			
			//Je nach Parameter wird der Stapel an den UmdrehenMS weiter Versandt oder vom Transformator Service selbst verarbeitet.
			if (aktuellerAuftrag.isTransformatorMS()) {
				stapelAnUmdrehenMSWeitersenden();
			} else {
				stapelVerarbeitenUndAnPersistenzZuruecksenden();
			}
		} catch (MalformedURLException |  ClassNotFoundException_Exception | SQLException_Exception e) {
			e.printStackTrace();
		}
		return "true";
	}

	/**
	 * Diese Methode dreht alle Strings des aktuell zu verarbeitenden Stapels um und sendet ihn zum Persistenz Service zurück.
	 * 
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 * @throws ClassNotFoundException_Exception - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird
	 * @throws SQLException_Exception - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	private void stapelVerarbeitenUndAnPersistenzZuruecksenden()
			throws MalformedURLException, ClassNotFoundException_Exception, SQLException_Exception {
		
		logger.info("Verarbeite Stapel für Auftrag " + aktuellerAuftrag.getId() + " Protokoll:"
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "ohne UmdrehenMS" : "mit UmdrehenMS"));
		//Variablen Initialisierung
		String response = "";
		String tmpString = "";

		//Wenn mit REST oder SOAP übertragen werden soll wird hier der Stapel verarbeitet
		while (aktuellerAuftrag.getStapel().size() != 0) {
			aktuellerAuftrag.setGerade(aktuellerAuftrag.getGerade() + 1);
			tmpString = "";
			for (int j = aktuellerAuftrag.getStapel().get(0).length() - 1; j >= 0; j--) {
				tmpString += aktuellerAuftrag.getStapel().get(0).charAt(j);
			}
			aktuellerAuftrag.getStapelGedreht().add(tmpString);
			aktuellerAuftrag.getStapel().remove(0);
		}
		aktuellerAuftrag.getStapel().trimToSize();
		aktuellerAuftrag.getStapelGedreht().add(String.valueOf(aktuellerAuftrag.getStartZeit()));
		
		//Je nach Protokoll, wird der gedrehte Stapel an die Persistenz gesendet
		aktuellerAuftrag.getNachTransformator().add(System.nanoTime());
		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
			response = restTarget.path(RESTspeichernBatch).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
					.post(Entity.entity(aktuellerAuftrag.getStapelGedreht(), MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);

		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			URL soapserviceURLPersistenz = new URL(SOAPPersistenzServiceURL);
			QName soapQname = new QName(ServiceBaseURL, SOAPPersistenzServiceName);
			Service soapService = Service.create(soapserviceURLPersistenz, soapQname);

			MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface persistenzPort = soapService
					.getPort(MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface.class);
			response = persistenzPort.setDatenBatch(aktuellerAuftrag.getStapelGedreht());
		}
		if(response.equals("false")){
			logger.severe("FEHLER: False als Response vom Persistenz Service erhalten. "
					+ "Senden deines gedrehten Stapels mit " + (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP")
					+ " ohne UmdrehenMS nicht möglich. Auftrag mit ProzessID " + aktuellerAuftrag.getId());
			//TODO löschen aus Liste
		}
	}

	/**
	 * Diese Methode sendet den Stapel an den UmdrehenMS Service zur Verarbeitung weiter
	 * 
	 * @throws MalformedURLException
	 * @throws MSBenchmark.microServices.SOAPStubs.umdrehenMS.ClassNotFoundException_Exception
	 * @throws MSBenchmark.microServices.SOAPStubs.umdrehenMS.SQLException_Exception
	 */
	private void stapelAnUmdrehenMSWeitersenden() throws MalformedURLException {
		logger.info("Stapel für Auftrag " + aktuellerAuftrag.getId() + " wird an UmdrehenMS mit "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") + " weitergesendet");
		//Variablen Initialisierung
		MSBenchmark.microServices.SOAPStubs.umdrehenMS.Fortschritt castAuftragFuerSOAP=null;
		String response = "";
		
		//Stapel muss für SOAP gecastet werden. REST kann direkt übertragen werden.
		castAuftragFuerSOAP = new MSBenchmark.microServices.SOAPStubs.umdrehenMS.Fortschritt();
		castAuftragFuerSOAP.setStartZeit(aktuellerAuftrag.getStartZeit());
		castAuftragFuerSOAP.setStapel(aktuellerAuftrag.getStapel());
		castAuftragFuerSOAP.setProtokoll(aktuellerAuftrag.getProtokoll());
		
		//Zeiten hinzufügen und den Stapel mit dem entsprechenden Protokoll übertragen.
		aktuellerAuftrag.getStapel().trimToSize();
		aktuellerAuftrag.getNachTransformator().add(System.nanoTime());

		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlUmdrehenMS);
			response = restTarget.path(RESTURLumdrehenMSStapel).request(MediaType.APPLICATION_JSON + "; charset=UTF-8").post(Entity.entity(castAuftragFuerSOAP, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
		
		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			URL soapServiceURLUmdrehenMS = new URL(SOAPUmdrehenMSServiceURL);
			QName soapQname = new QName(ServiceBaseURL, SOAPUmdrehenMSServiceName);
			Service soapSserviceUmdrehenMS = Service.create(soapServiceURLUmdrehenMS, soapQname);
			MSBenchmark.microServices.SOAPStubs.umdrehenMS.UmdrehenMSInterface umdrehenMSPort = soapSserviceUmdrehenMS.getPort(MSBenchmark.microServices.SOAPStubs.umdrehenMS.UmdrehenMSInterface.class);
			response = umdrehenMSPort.setDatenStapelUMS(castAuftragFuerSOAP);
		}
		if(response.equals("false")){
			logger.severe("FEHLER: False als Response vom Umdrehen Service erhalten. "
					+ "Senden deines Stapels mit "+(aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP")+" nicht möglich. "
					+ "Auftrag mit ProzessID "+aktuellerAuftrag.getId());
			//TODO löschen aus Liste
		}
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/batchSpeichernFertig")
	public String batchSpeichernFertig(ArrayList<String> zeitenDesFertigBearbeitetenStapels) {
		//ID des Auftrags ermitteln. Wenn das Argument batch NULL ist wird mit gRPC übertragen.
		long id=Long.parseLong(zeitenDesFertigBearbeitetenStapels.get(zeitenDesFertigBearbeitetenStapels.size()-3));

		//Auftrag suchen und lokal zwischenspeichern
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(id);
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode batchSpeichernFertig für REST/SOAP"
					+ " des Transformator Service, nicht gefunden werden!");
			return "false";
		}
		
		//Zeitstempel hinzufügen
		lokalerAuftrag.getVorSQLDatenSpeichern().add(Long.parseLong(zeitenDesFertigBearbeitetenStapels.get(zeitenDesFertigBearbeitetenStapels.size() - 2)));
		lokalerAuftrag.getNachSQLDatenSpeichern().add(Long.parseLong(zeitenDesFertigBearbeitetenStapels.get(zeitenDesFertigBearbeitetenStapels.size() - 1)));
		
		return "true";
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/batchWeiterreichen")
	public String batchWeiterreichen(ArrayList<String> stapel) {
		//lockUmdrehenMS.unlock();
		//Zeitstempel speichern und ID des Auftrags ermitteln
		long vor = System.nanoTime();
		long id=Long.parseLong(stapel.get(stapel.size()-3));

		//Auftrag in der Liste suchen und lokal zwischenspeichern
		auftragMitIDSuchen(id);
		
		logger.info("Gedrehter Stapel für Auftrag " + aktuellerAuftrag.getId() 
		+ " von UmdrehenMS erhalten. Stapel wird an Persistenz Service mit " 
		+ (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") + " weitergesendet" );
		
		//Zeiten für das Umdrehen hinzufügen
		aktuellerAuftrag.getVorUmdrehenMS().add(Long.parseLong(stapel.get(stapel.size() - 2)));
		aktuellerAuftrag.getNachUmdrehenMS().add(Long.parseLong(stapel.get(stapel.size() - 1)));
		stapel.remove(stapel.size() - 1);
		stapel.remove(stapel.size() - 1);
		

		//Die Zeiten des Weiterleitens hinzufügen
		aktuellerAuftrag.getVorTransformatorMS().add(vor);
		aktuellerAuftrag.getNachTransformatorMS().add(System.nanoTime());

		//Stapel an Persistenz weiterleiten
		String response = "";
		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
			response = restTarget.path(RESTspeichernBatch).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
					.post(Entity.entity(stapel, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			try {
				URL soapServiceURLPersistenz = new URL(SOAPPersistenzServiceURL);
				QName soapQname = new QName(ServiceBaseURL, SOAPPersistenzServiceName);
				Service soapService = Service.create(soapServiceURLPersistenz, soapQname);
				 MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface soapPersistenzPort = soapService.getPort(MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface.class);
				response = soapPersistenzPort.setDatenBatch(stapel);

			} catch (MalformedURLException | ClassNotFoundException_Exception | SQLException_Exception e) {
				e.printStackTrace();
			}
		}

		return "true";
	}


	public void batchWeiterreichen(grpcServices.StapelMsg stapel, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		//Zeit vorTransformatorMS zwischenspeichern
		long vor = System.nanoTime();	
		
		//Auftrag aus liste suchen und lokal zwischenspeichern
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(Long.parseLong(stapel.getMessage(stapel.getMessageList().size()-3)));
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode batchWeiterreichen für gRPC"
					+ " des Transformator Service, nicht gefunden werden!");
			//Wenn Auftrag nicht gefunden wurde false zurückgeben und Methode beenden
			responseObserver.onNext(StringMsg.newBuilder().setMessage("false").build());
			responseObserver.onCompleted();
			return;
		}
		logger.info("Gedrehter Stapel für Auftrag " + lokalerAuftrag.getId()
				+ " von UmdrehenMS erhalten. Stapel wird an Persistenz Service mit gRPC weitergesendet");
		//Neuen Stapel erstellen ohne Zwischengespeicherte Zeitstempel
		StapelMsg grpcStapelMsg = StapelMsg.newBuilder()
				.addAllMessage(stapel.getMessageList().subList(0, stapel.getMessageList().size() - 2)).build();		

		//Übertragene Zeitstempel speichern
		lokalerAuftrag.getVorUmdrehenMS().add(Long.parseLong(stapel.getMessage(stapel.getMessageList().size() - 2)));
		lokalerAuftrag.getNachUmdrehenMS().add(Long.parseLong(stapel.getMessage(stapel.getMessageList().size() - 1)));

		//Die Zeiten des Weiterleitens hinzufügen
		lokalerAuftrag.getVorTransformatorMS().add(vor);
		lokalerAuftrag.getNachTransformatorMS().add(System.nanoTime());

		//Stapel an Persistenz weiterleiten
		ManagedChannel gRPCChannelPersistenzLokal = ManagedChannelBuilder.forAddress(gRPCIPPersistenz, gRPCChannelPortPersistenz).usePlaintext(true).build();
		PersistenzGrpc.PersistenzBlockingStub grpcPersistenzBlockingStub = PersistenzGrpc.newBlockingStub(gRPCChannelPersistenzLokal);
		StringMsg grpcStringMsgResponse = grpcPersistenzBlockingStub.setDatenBatch(grpcStapelMsg);
		gRPCChannelPersistenzLokal.shutdown();
		if(grpcStringMsgResponse.getMessage().equals("false")){
			logger.severe("FEHLER: False als Response vom Persistenz Service erhalten. "
					+ "Beim senden eines gedrehten Stapels zum Speichern für Auftrag " + lokalerAuftrag.getId()
					+ " mit gRPC!");
			//TODO
		}
				
		//Methode batchWeiterreichen() mit NULL als Argument starten und Response an UmdrehenMS Service senden.
		responseObserver.onNext(StringMsg.newBuilder().setMessage("true").build());
		responseObserver.onCompleted();
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/auftragAbschliessen")
	public String auftragAbschliessen(String id) {
		testLaeuft=false;
		//Lösch-Variablen für die Stapel initialisieren
		ArrayList<String> tmp = new ArrayList<String>();
		StapelMsg smTmp = StapelMsg.newBuilder().build();
		
		//Auftrag suchen und lokal zwischenspeichern. Wenn nicht gefunden false zurückgeben
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(Long.parseLong(id));
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode auftragAbschliessen für REST/SOAP"
					+ " des Transformator Service, nicht gefunden werden!");
			return "false";
		}
		logger.info("Auftrag mit der ProzessID" + lokalerAuftrag.getId() + " fertig verarbeitet. Er wird der Lister der abgeschlossenen Aufträge hinzugefügt" );
		//Stapel NULL setzen und Zeitstempel hinzufügen
		lokalerAuftrag.setStapel(tmp);
		lokalerAuftrag.setStapelGedreht(tmp);
		lokalerAuftrag.setStapelGRPC(smTmp);
		lokalerAuftrag.setEndZeit(System.currentTimeMillis());
		
		//Auftrag bei abgeschlossenen hinzufügen und aus der Auftragsliste entfernen
		lockFertigeAuftragsliste.lock();
		AbgeschlosseneAuftraege.add(lokalerAuftrag);
		lockFertigeAuftragsliste.unlock();
		lockAuftragsliste.lock();
		Auftragsliste.remove(lokalerAuftrag);
		lockAuftragsliste.unlock();
		
		String response = "";
		if(lokalerAuftrag.getParameter()==mitTransformator){
			//An den UmdrehenMS Service eine Auftrag abschließen Meldung senden
			logger.info("Sende Auftrag abschließen an Umdrehen Service für Auftrag " + lokalerAuftrag.getId() + " mit " + (lokalerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") );
			if (lokalerAuftrag.getProtokoll() == REST) {
				Client restClient = ClientBuilder.newClient();
				WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
				response = restTarget.path(RESTabschliessen).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
						.post(Entity.entity(id, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
				
			} else if (lokalerAuftrag.getProtokoll() == SOAP) {
				try {
					URL soapServiceURLUmdrehenMS = new URL(SOAPUmdrehenMSServiceURL);
					QName soapQname = new QName(ServiceBaseURL, SOAPUmdrehenMSServiceName);
					Service soapService = Service.create(soapServiceURLUmdrehenMS, soapQname);
					MSBenchmark.microServices.SOAPStubs.umdrehenMS.UmdrehenMSInterface umdrehenMSPort = soapService.getPort(MSBenchmark.microServices.SOAPStubs.umdrehenMS.UmdrehenMSInterface.class);
					response = umdrehenMSPort.auftragAbschliessenUMS(id);
	
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		if(response.equals("false")){
			return "false";//TODO
		}
		
		return "true";
	}
	
	/**
	 * Diese Methode sucht den Auftrag mit der als Argument übergebenen ID raus und kopiert einen Pointer in die 
	 * lokale Variable aktuellerAuftrag der auf diesen Auftrag zeigt.
	 * 
	 * @param id - ID des Auftrags der gesucht werden soll.
	 */
	private FortschrittgRPC auftragMitIDSuchenGRPC(long id) {
		FortschrittgRPC auftrag;
		for (int i = 0; i < Auftragsliste.size(); i++) {
			lockAuftragsliste.lock();
			if (Auftragsliste.get(i).getStartZeit() == id) {
				auftrag = Auftragsliste.get(i);
				lockAuftragsliste.unlock();
				return auftrag;
			}
			lockAuftragsliste.unlock();
		}
		return null;
	}

	public void batchVerarbeitung(grpcServices.StapelMsg grpcStapel, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		System.out.println("grpc verarbeite Stapel"); 

		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(Long.parseLong(grpcStapel.getMessageList().get(grpcStapel.getMessageList().size() - 3)));
		String resp="";
		if(lokalerAuftrag==null){
			resp="false";
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode batchVerarbeitung für REST/SOAP"
					+ " des Transformator Service, nicht gefunden werden!");
		}else{
			resp="true";
		}
		logger.info("Für Auftrag mit der ProzessID " + lokalerAuftrag.getId() 
			+ " mit gRPC Stapel zur verarbeitung erhalten.");
		//Stapel dem Auftrag hinzufügen
		lokalerAuftrag.setStapelGRPC(grpcStapel);

		//Ermitteln der ID des empfangenen Auftrags. Wenn das Argument Batch NULL ist, wird mit gRPC gearbeitet
		lokalerAuftrag.getTransformatorBatchErhalten().add(System.nanoTime());
		lokalerAuftrag.getVorSQLDatenHolen().add(Long.parseLong(grpcStapel.getMessageList().get(grpcStapel.getMessageList().size() - 2)));
		lokalerAuftrag.getNachSQLDatenHolen().add(Long.parseLong(grpcStapel.getMessageList().get(grpcStapel.getMessageList().size() - 1)));
		

		//Die Methode batchVerarbeitung() mit NULL als Argument starten und Response zurücksenden
		responseObserver.onNext(StringMsg.newBuilder().setMessage(resp).build());
		responseObserver.onCompleted();
		
		//Verarbeitung starten
		//Je nach Parameter wird der Stapel an den UmdrehenMS weiter Versandt oder vom Transformator Service selbst verarbeitet.
		lokalerAuftrag.getVorTransformator().add(System.nanoTime());
		if (lokalerAuftrag.isTransformatorMS()) {
			logger.info("Stapel für Auftrag mit der ProzessID " + lokalerAuftrag.getId() + " wird mit gRPC Stapel an Umdrehen Service weitergesendet");
			//Variablen Initialisierung
			FortschrittRequest.Builder castAuftragFuerGRPC=null;
			
			//Stapel muss für SOAP und gRPC gecastet werden. REST kann direkt übertragen werden.
			castAuftragFuerGRPC = FortschrittRequest.newBuilder().setStartZeit(lokalerAuftrag.getStartZeit()).addAllStapel(lokalerAuftrag.getStapelGRPC().getMessageList().subList(0, lokalerAuftrag.getStapelGRPC().getMessageList().size()-3));

			//Zeiten hinzufügen und den Stapel mit dem entsprechenden Protokoll übertragen.
			lokalerAuftrag.getStapel().trimToSize();
			lokalerAuftrag.getNachTransformator().add(System.nanoTime());

			ManagedChannel gRPCChannelUmdrehenMSLokal = ManagedChannelBuilder.forAddress(gRPCIPUmdrehenMS, gRPCChannelPortUmdrehenMS).usePlaintext(true).build();
			UmdrehenMSGrpc.UmdrehenMSBlockingStub grpcUmdrehenMSBlockingStub = UmdrehenMSGrpc.newBlockingStub(gRPCChannelUmdrehenMSLokal);
			StringMsg grpcStringMsgResponse = grpcUmdrehenMSBlockingStub.setDaten(castAuftragFuerGRPC.build());
			gRPCChannelUmdrehenMSLokal.shutdown();
			
		} else {
			//Variablen Initialisierung
			StapelMsg.Builder stapelGRPC= null;
			String tmpString = "";

			//Wenn mit gRPC übertragen werden soll wird hier der Stapel verarbeitet
			stapelGRPC = StapelMsg.newBuilder();
			for(int i=0;i<lokalerAuftrag.getStapelGRPC().getMessageList().size()-3;i++){
				lokalerAuftrag.setGerade(lokalerAuftrag.getGerade() + 1);
				tmpString = "";
				for (int j = lokalerAuftrag.getStapelGRPC().getMessage(i).length() - 1; j >= 0; j--) {
					tmpString += lokalerAuftrag.getStapelGRPC().getMessage(i).charAt(j);
				}
				stapelGRPC.addMessage(tmpString);
			}
			//Leeren Stapel der Auftrag zum Speicher freigeben hinzufügen. Dem Stapel mit den gedrehten Strings die Auftrags-ID hinzufügen und den Zeitstempel speichern
			lokalerAuftrag.setStapelGRPC(StapelMsg.newBuilder().build());
			stapelGRPC.addMessage(String.valueOf(lokalerAuftrag.getStartZeit()));
			lokalerAuftrag.getNachTransformator().add(System.nanoTime());
			logger.info("Stapel für Auftrag mit der ProzessID " + lokalerAuftrag.getId() + " verarbeitet und wird mit gRPC Stapel an Persistenz Service zurückgesendet");
			//Der gedrehte Stapel wird an die Persistenz gesendet
			ManagedChannel gRPCChannelPersistenzLokal = ManagedChannelBuilder.forAddress(gRPCIPPersistenz, gRPCChannelPortPersistenz).usePlaintext(true).build();
			PersistenzGrpc.PersistenzBlockingStub gRPCPersistenzBlockingStub = PersistenzGrpc.newBlockingStub(gRPCChannelPersistenzLokal);
			StringMsg gRPCStringMsgResponse = gRPCPersistenzBlockingStub.setDatenBatch(stapelGRPC.build());
			gRPCChannelPersistenzLokal.shutdown();
		}
	}

	public void auftragAbschliessen(grpcServices.StringMsg auftragsID, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		//Methode auftragAbschliessen() mit der ID als Argument starten, um den Auftrag aus der Auftragsliste zu entfernen und den fertigen Aufträgen hinzufügen.
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(Long.parseLong(auftragsID.getMessage()));
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode auftragAbschliessen für gRPC"
					+ " des Transformator Service, nicht gefunden werden!");
			responseObserver.onNext(StringMsg.newBuilder().setMessage("false").build());
			responseObserver.onCompleted();
			//TODO Exception abfangen
			return;
		}
		logger.info("Auftrag mit der ProzessID " + lokalerAuftrag.getId() + " fertig verarbeitet. Er wird der Lister der abgeschlossenen Aufträge hinzugefügt" );

		auftragAbschliessen(auftragsID.getMessage()); 
		
		//UmdrehenMS Abschluss des Auftrag senden
		StringMsg grpcResponse = StringMsg.newBuilder().build();
		
		if(lokalerAuftrag.getParameter()==mitTransformator){
			logger.info("Auftrag abschließen für Auftrag mit der ProzessID " + lokalerAuftrag.getId() + " wird mit gRPC Stapel an Umdrehen Service gesendet");
			//gRPC Anfrage zum UmdrehenMS Service senden, damit dieser den Auftrag auch aus seiner Liste löscht
			ManagedChannel gRPCChannelUmdrehenMSLokal = ManagedChannelBuilder.forAddress(gRPCIPUmdrehenMS, gRPCChannelPortUmdrehenMS).usePlaintext(true).build();
			UmdrehenMSGrpc.UmdrehenMSBlockingStub umdrehenMSBlockingStub = UmdrehenMSGrpc.newBlockingStub(gRPCChannelUmdrehenMSLokal);
			StringMsg grpcAuftragsIdStringMsg = StringMsg.newBuilder().setMessage(auftragsID.getMessage()).build();
			grpcResponse = umdrehenMSBlockingStub.auftragAbschliessen(grpcAuftragsIdStringMsg);
			gRPCChannelUmdrehenMSLokal.shutdown();
		}
		//Response zurücksenden
		if(grpcResponse.getMessage().equals("false")){
			logger.severe("FEHLER: UmdrehenMS meldet fehler beim Abschließen des Auftags mit der ProzessID " + lokalerAuftrag.getId());
			//TODO fehler behandeln
			responseObserver.onNext(grpcResponse);
		}else{
			responseObserver.onNext(grpcResponse);
		}
		
		responseObserver.onCompleted();
	}

	public void batchSpeichernFertig(grpcServices.StapelMsg grpcStapel, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {

		//ID des Auftrags ermitteln. Auftrag aus Liste raussuchen und lokal zwischenspeichern.
		long id=Long.parseLong(grpcStapel.getMessage(grpcStapel.getMessageList().size()-3));
		FortschrittgRPC lokalerAuftrag = auftragMitIDSuchenGRPC(id);
		
		StringMsg.Builder grpcResponse = StringMsg.newBuilder();
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode batchSpeichernFertig für gRPC"
					+ " des Transformator Service, nicht gefunden werden!");
			//Wenn kein Auftrag gefunden wurde false zurückgeben
			responseObserver.onNext(grpcResponse.setMessage("false").build());
		}else{
			logger.info("Stapel für Auftrag mit der ProzessID " + lokalerAuftrag.getId() + " wurde fertig bearbeitet. Zeitstempel werden hinzugefügt");
			//Zeiten hinzufügen und true zurücksenden.
			lokalerAuftrag.getVorSQLDatenSpeichern().add(Long.parseLong(grpcStapel.getMessage(grpcStapel.getMessageList().size() - 2)));
			lokalerAuftrag.getNachSQLDatenSpeichern().add(Long.parseLong(grpcStapel.getMessage(grpcStapel.getMessageList().size() - 1)));
			
			responseObserver.onNext(grpcResponse.setMessage("true").build());
		}
		//onComplite versendet die Responsen
		responseObserver.onCompleted();
	}

	/**
	 * Diese Methode sendet einen neuen Auftrag an den Persistenz Service
	 * 
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 * @throws ClassNotFoundException_Exception
	 * @throws SQLException_Exception
	 */
	private void datenVonSQLDatenbankHolen()throws MalformedURLException, ClassNotFoundException_Exception, SQLException_Exception {
		
		//Je nach gewähltem Protokoll wird der Auftrag an die Persistenz gesendet 
		String response = "";
		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlPersiszenz);
			response = restTarget.path(RESTURLanfordernBatch).queryParam("Fortschritt", aktuellerAuftrag)
					.request(MediaType.APPLICATION_JSON + "; charset=UTF-8").post(Entity.entity(aktuellerAuftrag, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
		
		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			URL soapServiceURLPersistenz = new URL(SOAPPersistenzServiceURL);
			QName soapQname = new QName(ServiceBaseURL, SOAPPersistenzServiceName);
			Service soapService = Service.create(soapServiceURLPersistenz, soapQname);
			MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface soapPersistenzPort = soapService.getPort(MSBenchmark.microServices.SOAPStubs.persistenz.PersistenzInterface.class);
			MSBenchmark.microServices.SOAPStubs.persistenz.Fortschritt soapCastAuftrag = new MSBenchmark.microServices.SOAPStubs.persistenz.Fortschritt();
			soapCastAuftrag.setFortschritt(aktuellerAuftrag);
			response = soapPersistenzPort.getDatenBatch(soapCastAuftrag);			
		} else if (aktuellerAuftrag.getProtokoll() == gRPCSingle) {
			ManagedChannel gRPCChannelPersistenzLokal = ManagedChannelBuilder.forAddress(gRPCIPPersistenz, gRPCChannelPortPersistenz).usePlaintext(true).build();
			PersistenzGrpc.PersistenzBlockingStub pbs = PersistenzGrpc.newBlockingStub(gRPCChannelPersistenzLokal);
			FortschrittRequest fr = aktuellerAuftrag.getFortschrittBuilder();
			StringMsg sm = pbs.getDatenBatch(fr);
			response=sm.getMessage();
			gRPCChannelPersistenzLokal.shutdown();
		}
		
		if (response.equals("false")) {
			logger.severe("FEHLER: False als Response beim Senden eines neuen Auftrags an die Persistenz erhalten");
			// TODO 
		}
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/autoRun")
	public String autoRun(){
		ArrayList<String> stapelgroesseA = new ArrayList<String>();
		ArrayList<String> bis = new ArrayList<String>();
		stapelgroesseA.add("1");bis.add("30000");
		stapelgroesseA.add("2");bis.add("60000");
		stapelgroesseA.add("5");bis.add("125000");
		stapelgroesseA.add("10");bis.add("250000");
		stapelgroesseA.add("50");bis.add("500000");
		stapelgroesseA.add("100");bis.add("1000000");
		stapelgroesseA.add("1000");bis.add("1000000");
		stapelgroesseA.add("10000");bis.add("1000000");
		stapelgroesseA.add("100000");bis.add("1000000");

		String titel="";
		for(int g=1;g<3;g++){
			for(int j=1;j<2;j++){
				for(int i=0;i<stapelgroesseA.size();i++){
					titel="";
					switch(g){
					case 1: titel+="REST ";break;
					case 2: titel+="SOAP ";break;
					case 3: titel+="gRPC ";break;
					}
					
					switch(j){
					case 1: titel+="ohne UmdrehenMS ";break;
					case 2: titel+="mit UmdrehenMS ";break;
					}
					
					
					titel+="stapelgröße "+stapelgroesseA.get(i);

			
					try {
						testLaeuft=true;
						init(stapelgroesseA.get(i), bis.get(i), titel, String.valueOf(g), String.valueOf(j));
						while(testLaeuft){
							Thread.sleep(1000);
						}
					} catch (MalformedURLException | SQLException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}
		}
		

		return "Test läuft";
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/inCSVSpeichern")
	public String inCSVSpeichern(@QueryParam("dateiname")String Dateiname){
		
		String ergebnisDatenCSV = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		//Danach wird durch die Liste iteriert und für jeden abgeschlossenen Auftrag ein Eintrag in der Tabelle des Ausgabe HTML-Dokuments hinzugefügt
		for (int i = 0; i < AbgeschlosseneAuftraege.size(); i++) {
			lockFertigeAuftragsliste.lock();
			stringProtoPara(i, false);

			ergebnisDatenCSV += AbgeschlosseneAuftraege.get(i).getTitel() + ";" + parameter+ ";" + proto + ";";
			ergebnisDatenCSV += AbgeschlosseneAuftraege.get(i).getVon() + ";" + AbgeschlosseneAuftraege.get(i).getBis() + ";" + (AbgeschlosseneAuftraege.get(i).getBis() - AbgeschlosseneAuftraege.get(i).getVon()) ;
			ergebnisDatenCSV += ";" + dateFormat.format(AbgeschlosseneAuftraege.get(i).getStartZeit());
			ergebnisDatenCSV += ";" + dateFormat.format(AbgeschlosseneAuftraege.get(i).getEndZeit()) + ";" ;
			ergebnisDatenCSV += AbgeschlosseneAuftraege.get(i).zeitUmwandeln((AbgeschlosseneAuftraege.get(i).getEndZeit() - AbgeschlosseneAuftraege.get(i).getStartZeit())*1000000);
			ergebnisDatenCSV += AbgeschlosseneAuftraege.get(i).timeToStringCSV()+ "\n";
			lockFertigeAuftragsliste.unlock();
		}
		
		Writer fw = null;

		try
		{
		  fw = new FileWriter( "C:/Users/Admin/Downloads/Bachelorarbeit/testErgebnisse/"+Dateiname );
		  fw.write( ergebnisDatenCSV );
		  //fw.append( System.getProperty(",") ); // e.g. "\n"
		}
		catch ( IOException e ) {
		  System.err.println( "Konnte Datei nicht erstellen" );
		}
		finally {
		  if ( fw != null )
		    try { fw.close(); } catch ( IOException e ) { e.printStackTrace(); }
		}
		return "Ergebnisse wurden gespeichert";
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/resetAbgeschlosseneAuftraege")
	public String resetMethode(){
		lockFertigeAuftragsliste.lock();
		AbgeschlosseneAuftraege.clear();
		lockFertigeAuftragsliste.unlock();
		return "Liste geleert";
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/resetAuftragsliste")
	public String resetAuftraegeMethode(){
		lockFertigeAuftragsliste.lock();
		Auftragsliste.clear();
		lockFertigeAuftragsliste.unlock();
		return "Liste geleert";
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/loescheAuftrage")
	public String resetAuftrag(@QueryParam("id") String id){
		long idl = Long.parseLong(id);
		if(Auftragsliste.size()==0){
			return "Auftragsliste ist leer. Auftrag konnte nicht gelöscht werden";
		}
		lockAuftragsliste.lock();
		for(int i=0;i<Auftragsliste.size();i++){
			if(Auftragsliste.get(i).getId()==idl){
				Auftragsliste.remove(i);
				return "Auftrag gelöscht";
			}
		}
		lockAuftragsliste.unlock();
		return "Auftrag geloescht";
	}
	
	@Override
	public void run() {
		try{
			if(initGrpc){
				//Initialisieren des gRPC Servers
				initGRPC();
			}else{
				if (aktuellerAuftrag.getVorSQLDatenHolen().size() != 0) {
					//Hier wird reingesprungen wenn der Auftrag in Bearbeitung ist
					batchVerarbeitungM();
				} else {
					//Hier wird reingesprungen wenn ein neuer Auftrag beginnt
					datenVonSQLDatenbankHolen();
				}
			}
		}catch(ClassNotFoundException_Exception | SQLException_Exception | MalformedURLException e){
			//TODO
		}
	}

}
