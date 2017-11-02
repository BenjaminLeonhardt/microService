package MSBenchmark.microServices;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

import grpcServices.StapelMsg;
import grpcServices.TransformatorGrpc;
import grpcServices.StringMsg;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import grpcServices.PersistenzGrpc;

//http://localhost:8080/microServices/PersistenzService?wsdl
/**
 * @author Benjamin Leonhardt
 *
 * Die Klasse Persistenz dient zur Kommunikation mit der SQL Datenbank.
 *
 */
@WebService(endpointInterface = "MSBenchmark.microServices.PersistenzInterface",
          serviceName = "PersistenzService")
@Path("persistenzBA")
public class Persistenz extends PersistenzGrpc.PersistenzImplBase implements PersistenzInterface, Runnable{
	
	private static final String ipTransformator					= 	"127.0.0.1";
//	private static final String ipTransformator					= 	"10.15.252.205";
	
	//SQL Variablen
	public static final String passwortSQL 						=	"bla123"; 													//Passwort für die SQL Datenbank
	public static final String benutzerSQL 						=	"postgres";													//Benutzername für die SQL Datenbank
	public static final String SQLIP							=	DatenbankFuellen.ipDatenbank;								//IP Adresse der SQL Datenbank
	public static final String SQLPort							=	DatenbankFuellen.portDatenbank;								//Port Adresse der SQL Datenbank
	public static final String SQLDriverURL						= 	"jdbc:postgresql://"+SQLIP+":"+SQLPort+"/PersistenzBA";		//URL des SQL JDBC Driver zur postgeSQL Datenbank
	
	private static Connection sqlConnection						=	null;														//Globale SQL Connection variable
	private static int indexDB									=	0;															//Index-Zähler für die Datenbank. Entspricht dem Index des letzten Eintrages in der Tabelle umgedreht 
	private static Lock lockSQL 								= 	new ReentrantLock();										//Lock für Zugriffe auf SQL Datenbank	
	
	//REST Adressen Transformator
	private static final String RESTIPTransformator				=	ipTransformator;											//REST IP Adresse Transformator
	private static final String RESTPortTransformator			=	"8080";													//REST Port Transformator
	private static final String RESTbaseUrlTransformator		=	"http://"+RESTIPTransformator+":"+RESTPortTransformator;	//REST IP-Adresse und Port an der der Transformator-Service zu finden ist 
	
	private static final String RESTbatchVerarbeitung 			=	"/microServices/webapi/Transformator/batchVerarbeitung";	//REST Adresse für die Methode batchVerarbeitung des Transformator Service 
	private static final String RESTbatchSpeichernFertig 		=	"/microServices/webapi/Transformator/batchSpeichernFertig";	//REST Adresse für die Methode batchSpeichernFertig des Transformator Service
	private static final String RESTabschliessen 				=	"/microServices/webapi/Transformator/auftragAbschliessen";	//REST Adresse für die Methode auftragAbschliessen des Transformator Service
	
	//SOAP Adressen Transformator
	private static final String SOAPIPTransformator				=	ipTransformator;											//SOAP IP Adresse zum Transformator Service
	private static final String SOAPPortTransformator			=	"8080";													//SOAP Port zum Transformator Service
	private static final String SOAPbaseUrl 					=	"http://"+SOAPIPTransformator+":"+SOAPPortTransformator;	//SOAP IP-Adresse und Port an der der Transformator-Service zu finden ist 
	
	private static final String SOAPTransformatorServiceURL  	=	SOAPbaseUrl + "/microServices/TransformatorService?wsdl";	//SOAP Service URL
//	private static final String SOAPTransformatorServiceURL  	=	"file:/opt/jboss/wildfly/standalone/TransformatorService.xml";//Angepasste WSDL Datei für Container
	private static final String SOAPTransformatorServiceName 	=	"TransformatorService";										//SOAP Service Name
	private static final String SOAPServiceBaseURL  			=	"http://microServices.MSBenchmark/";						//SOAP Base URL
	
	//gRPC Server für Persistenz Service
	private static Server gRPCServerPersistenz;																					//gRPCServer damit von der Persistenz auf den Transformator Service über gRPC zugegriffen werden kann
	private static final int gRPCServerPortPersistenz 			= 	10051;														//Port für den gRPC Transformator Service
	private boolean initGrpc 									= 	false;														//lokale Variable die für das initialisieren des gRPC Servers verwendet wird
	
	//gRPC Adressen zum Transformator Service
	private static final String gRPCAddressTransformator 		= 	"127.0.0.1";												//Adresse für den gRPC Transformator Service
//	private static final String gRPCAddressTransformator 		= 	"10.15.245.76";
	private static final int gRPCChannelPortTransformator		= 	20051;														//Port für den gRPC Transformator Service

	//Globale Listen und lokale Variablen und Locks
	private static ArrayList<MSBenchmark.microServices.FortschrittgRPC> Auftragsliste;											//Liste aller Aufträge die zu verarbeiten sind
	private static Lock lockAuftragsliste 						= 	new ReentrantLock();										//Lock für Zugriffe auf gemeinsam benützte fortschrittSQL Liste
	
	private boolean save;																										//ist true, wenn ein Thread zum speichern eines Stapels gestartet wird
	private boolean complete;																									//ist true, wenn ein Thread gestartet wird nachdem ein Auftrag abgearbeitet wurde
	
	private MSBenchmark.microServices.FortschrittgRPC aktuellerAuftrag;															//Lokale Variable, wird verwendet um mit dem aktuellen Objekt aus der Auftragsliste fortschrittSQL zwischen zu speichern
	
	private static final int REST = 1, SOAP = 2, gRPCSingle = 3, gRPCStream = 4;												//Hilfsvariablen für das Protokoll. Zur besseren Lesbarkeit des Quellcodes
	
	//Java Logging API
	private static final Logger logger 						= Logger.getLogger(Transformator.class.getName());

	
	/**
	 * Standard Konstruktor. 
	 * 
	 * Initialisiert die Globalen Auftragslisten und das lokale Objekt für den aktuellen Auftrag.
	 */
	public Persistenz() {
		logger.setLevel(Level.ALL);
		if (Auftragsliste == null) {
			Auftragsliste = new ArrayList<MSBenchmark.microServices.FortschrittgRPC>();
		}
		aktuellerAuftrag = new MSBenchmark.microServices.FortschrittgRPC();
	}
	
	/**
	 * Dieser Konstruktor wird als Controller verwendet, damit zwischen den Aufgaben die zu bearbeiten sind gewechselt werden kann.
	 * In der run()-Methode wird entsprechend den Boole Werten, die hier übergeben werden, anders verarbeitet. 
	 * 
	 * @param auftrag - Lokales Objekt vom Typ FortschrittgRPC. Beinhaltet für den laufenden Thread den ab zu arbeiteten Auftrag. In ihm sind alle Fortschritts relevanten Daten gespeichert.
	 * @param speichern - Dieser boolesche Wert teilt dem Thread mit, ob er in die Datenbank schreiben oder von der Datenbank lesen soll.
	 * @param abchliessen - Dieser Wert teilt dem Thread mit ob er den Auftrag abgearbeitet hat und beenden soll.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird.
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	public Persistenz(MSBenchmark.microServices.FortschrittgRPC auftrag, boolean speichern, boolean abchliessen)
			throws ClassNotFoundException, SQLException {
		this.aktuellerAuftrag = auftrag;
		save = speichern;
		complete = abchliessen;
	}
	
	/**
	 * Konstruktor für den Thread zum initialisieren des gRPC-Servers.
	 * 
	 * @param initGrpc - Wenn true wird in der run()-Methode in die starteGRPCServer()-Methode gesprungen.
	 */
	public Persistenz(boolean initGrpc) {
		this.initGrpc = initGrpc;
	}
	
	/**
	 * Dieser Konstruktor wird bei laufenden Aufträgen verwendet.
	 * 
	 * Sucht den aktuellen Auftrag und setzt die Booleans. Im Anschluss wird in die run() Methode gesprungen und dort in die
	 * entsprechende Methode
	 * 
	 * @param id - Die ID des zu bearbeitenden Auftrags. Ist gleichzeitig auch die Startzeit des Auftrags in Millisekunden.
	 * @param speichern - Dieser boolesche Wert teilt dem Thread mit, ob er in die Datenbank schreiben oder von der Datenbank lesen soll.
	 * @param abschliessen - Dieser Wert teilt dem Thread mit ob er den Auftrag abgearbeitet hat und beenden soll.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird.
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	public Persistenz(long id, boolean speichern, boolean abschliessen) throws ClassNotFoundException, SQLException {
		aufrtagMitIDSuchen(id);
		save = speichern;
		complete = abschliessen;
	}

	/**
	 * Diese Methode sucht den Auftrag mit der als Argument übergebenen ID raus und kopiert einen Pointer in die 
	 * lokale Variable aktuellerAuftrag der auf diesen Auftrag zeigt.
	 * 
	 * @param id - ID des Auftrags der gesucht werden soll.
	 */
	private void aufrtagMitIDSuchen(long id) {
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
	 * Diese Methode sucht den Auftrag mit der als Argument übergebenen ID raus und kopiert einen Pointer in die 
	 * lokale Variable aktuellerAuftrag der auf diesen Auftrag zeigt.
	 * 
	 * @param id - ID des Auftrags der gesucht werden soll.
 	 * @return Gesuchter Auftrag oder wenn nicht gefunden ein NULL Objekt vom Typ FortschrittgRPC.
	 */
	private FortschrittgRPC aufrtagMitIDSuchenGRPC(long id) {
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

	/**
	 * Diese Methode dient zum starten des gRPC Servers. Sie startet einen Thread und wartet bis dieser den Server gestartet hat.
	 * 
	 * @return Sendet einen String "true" zurück sobald der Server gestartet wurde.
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/initGrpc")
	public String initGrpc() {
		Thread t = new Thread(new Persistenz(true));
		t.start();
		long vorWarten = System.currentTimeMillis();
		while (gRPCServerPersistenz == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				logger.severe("FEHLER: Interrupt für den Sleep beim warten auf den gRPC Server");
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
			if(System.currentTimeMillis()-vorWarten>5000){
				logger.severe("FEHLER: Starten des gRPC Server dauert länger als erwartet. Sende False zurück an Transformator Service");
				return "false";
			}
		}
		return "true";
	}

	/**
	 * Diese Methode startet den gRPC-Server zum Transformator Service.
	 */
	private void starteGRPCServer() {
		if (Persistenz.gRPCServerPersistenz == null) {
			try {
				Persistenz.gRPCServerPersistenz = ServerBuilder.forPort(gRPCServerPortPersistenz).addService(new Persistenz()).build().start();
				logger.info("gRPC Persistenz Server gestartet");
				Persistenz.gRPCServerPersistenz.awaitTermination();
				initGrpc = false;
			} catch (InterruptedException | IOException e) {
				logger.severe("FEHLER: Beim starten des gRPC Servers");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	@POST
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/anfordernBatch")
	public String getDatenBatch(Fortschritt auftrag) throws ClassNotFoundException, SQLException {
		logger.info("Auftrag mit ProzessID " + aktuellerAuftrag.getId() + " erhalten."
				+ " Protokoll: " + (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		// Auftrag in lokale Variable kopieren
		this.aktuellerAuftrag = new MSBenchmark.microServices.FortschrittgRPC();
		this.aktuellerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
		this.aktuellerAuftrag.setFortschritt(auftrag);

		// Berechnung der Anzahl an Stapel die der Auftrag enthält
		this.aktuellerAuftrag.setBatchAnzahl((int) ((double) (this.aktuellerAuftrag.getBis() - this.aktuellerAuftrag.getVon()) / (double) this.aktuellerAuftrag.getStapelgroesse()));
		// Wenn die Berechnung einen Rest hat, muss ein Stapel mehr bearbeitet
		// werden
		if ((this.aktuellerAuftrag.getBis() - this.aktuellerAuftrag.getVon()) % this.aktuellerAuftrag.getStapelgroesse() != 0) {
			this.aktuellerAuftrag.setBatchAnzahl(this.aktuellerAuftrag.getBatchAnzahl() + 1);
		}
		// Hinzufügen des Auftrages in die Auftragsliste.
		Auftragsliste.add(this.aktuellerAuftrag);
		// Starten eines neuen Threads der den Auftrag bearbeitet
		Thread t = new Thread(new Persistenz(this.aktuellerAuftrag.getStartZeit(), false, false));
		t.start();
		// Bestätigung an den Transformator-Service zurücksenden
		return "true";
	}
	
	
	/**
	 * Die Methode getAnforderBatch liest die angeforderten Daten aus der Datenbank und sendet sie an 
	 * den Transformator zur Verarbeitung.
	 * 
	 * @return - Einen String mit dem Inhalt "true", wenn alles funktioniert hat.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird.
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	public String getAnfordernBatch() throws ClassNotFoundException, SQLException, MalformedURLException{
		String response="";
		//Initialisierung der Datenbank und des Statement sowie ResultSet zum bearbeiten der Datenbankabfragen.
		initJDBCDriver();
		Statement sqlStatement = sqlConnection.createStatement();
		ResultSet sqlResultSet = null;

		//Builder ist für gRPC dieser muss vor der Schleifer erstellt werden. In ihn werden bei jeder Iteration die Ergebnisse mit addMessage() hinzugefügt.
		StapelMsg.Builder grpcStapel=null;

		//Diese Schleife wird aufgerufen solange noch weitere Stapel verarbeitet werden müssen
		if (aktuellerAuftrag.getAktuellerBatch() < aktuellerAuftrag.getBatchAnzahl()) {
			
			//Berechnung der Index Ränder die für den aktuellen Stapel gelten
			int von = aktuellerAuftrag.getStapelgroesse() * aktuellerAuftrag.getAktuellerBatch();
			int bis = aktuellerAuftrag.getStapelgroesse() * (aktuellerAuftrag.getAktuellerBatch() + 1);
			if (bis > aktuellerAuftrag.getBis()) { //Wenn der letzte Stapel kleiner ist wird hier berücksichtigt
				bis = aktuellerAuftrag.getBis();
			}
			logger.info("Beim Auftrag mit ProzessID " + aktuellerAuftrag.getId() + 
					" werden die Strings mit den Index von " + von + " bis " + bis +" aus der Datenbank geladen");
			//Zeit speichern und Liste neu initialisieren
			aktuellerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
			if (aktuellerAuftrag.getStapel().size() != 0) {
				ArrayList<String> tmp = new ArrayList<>();
				aktuellerAuftrag.setStapel(tmp);
			}
			
			//SQL Abfrage
			lockSQL.lock();
			sqlResultSet = sqlStatement.executeQuery("SELECT ID,Wert FROM werte WHERE id BETWEEN '" + von + "' AND '" + (bis - 1) + "' ;");
			lockSQL.unlock();

			//Bei SOAP und REST wird hier rein gesprungen und solange weitere Strings im ResultSet befinden, werden diese dem Stapel des Auftragsobjekts hinzugefügt
			while (sqlResultSet.next()) {
				aktuellerAuftrag.getStapel().add(sqlResultSet.getString(2));
				aktuellerAuftrag.setSqlGerade(aktuellerAuftrag.getSqlGerade() + 1);
			}
			
			//Hier werden dem Stapel die ID des Auftrags sowie die Zeiten hinzugefügt.
			aktuellerAuftrag.getStapel().add(String.valueOf(aktuellerAuftrag.getStartZeit()));
			aktuellerAuftrag.getStapel().add(String.valueOf(aktuellerAuftrag.getVorSQLDatenHolen().get(aktuellerAuftrag.getVorSQLDatenHolen().size() - 1)));
			aktuellerAuftrag.getStapel().add(String.valueOf(System.nanoTime()));
			aktuellerAuftrag.setAktuellerBatch(aktuellerAuftrag.getAktuellerBatch() + 1);
			
			//Versandt des Stapel mit den jeweiligen Protokollen
			if (aktuellerAuftrag.getProtokoll() == REST) {
				Client restClient = ClientBuilder.newClient();
				WebTarget restTarget = restClient.target(RESTbaseUrlTransformator);
				response = restTarget.path(RESTbatchVerarbeitung).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
						.post(Entity.entity(aktuellerAuftrag.getStapel(), MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
				
			} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
				URL soapServiceURLTransformator = new URL(SOAPTransformatorServiceURL);
				QName soapQname = new QName(SOAPServiceBaseURL, SOAPTransformatorServiceName);
				Service soapTransformatorService = Service.create(soapServiceURLTransformator, soapQname);
				MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface soapTransformatorPort = soapTransformatorService
						.getPort(MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface.class);
				response = soapTransformatorPort.batchVerarbeitung(aktuellerAuftrag.getStapel());
				
			} 
			if(response.equals("false")){
				logger.severe("FEHLER: Beim senden eines neuen Stapels an den Transformator Service false als Response erhalten!");
				//TODO auftrag löschen oder so
			}
			sqlResultSet.close();
		}else{
			//Wenn der letzte Stapel verarbeitet wurde wird hier reingesprungen und der Auftrag wird entfernt
			sqlStatement.close();
			Auftragsliste.remove(aktuellerAuftrag);
			
			//Ein neuer Thread wird gestartet, damit allen anderen Microservices eine Nachricht bekommen, dass der Auftrag abgeschlossen wurde.
			Thread threadAuftragAbschliessen = new Thread(new Persistenz(aktuellerAuftrag, true, true));
			threadAuftragAbschliessen.start();
		}
		return "true";
	}

	/**
	 * Wie der Name der Methode schon sagt wird hier der JDBC Treiber der SQL Datenbank geladen und die Tabelle umgedreht wird geleert.
	 * 
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	private void initJDBCDriver() throws ClassNotFoundException, SQLException {
		if (sqlConnection == null) {
			Class.forName("org.postgresql.Driver");
			sqlConnection = DriverManager.getConnection(SQLDriverURL, benutzerSQL, passwortSQL);
			if (sqlConnection != null) {
				logger.info("Datenbankverbindung hergestellt!");
			}
			Statement sqlStatement = sqlConnection.createStatement();
			sqlStatement.execute("TRUNCATE table umgedreht CASCADE");
		}
	}
	
	
	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/speichernBatch")
	public String setDatenBatch(ArrayList<String> stapel) throws ClassNotFoundException, SQLException {
		//Die ID des Stapels wird ermittelt. 
		long id = Long.parseLong(stapel.get(stapel.size() - 1));
		
		//Auftrag aus der globalen Liste suchen. Wenn ein NULL Objekt zurückgegeben wird, wurde der Auftrag nicht gefunden, dann wird false zurückgegeben.
		FortschrittgRPC auftragLokal = aufrtagMitIDSuchenGRPC(id);
		if(auftragLokal==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode setDatenBatch für REST/SOAP"
					+ " des Persistenz Service, nicht gefunden werden!");
			return "false";
		}
		logger.info("Speicher Stapel für Auftrag " + auftragLokal.getId() + " Protokoll: "
				+ (auftragLokal.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (auftragLokal.getProtokoll() == 1 ? "ohne UmdrehenMS" : "mit UmdrehenMS"));
		//Die Auftrags-ID wird vom Stapel entfernt
		stapel.remove(stapel.size() - 1);
		//Stapel dem Auftrag hinzugefügt und die Zeit wird hinzugefügt
		auftragLokal.setStapelGedreht(stapel);
		auftragLokal.getVorSQLDatenSpeichern().add(System.nanoTime());
		
		//Thread zum Speichern wird gestartet.
		Thread threadZumSpeichern = new Thread(new Persistenz(id, true, false));
		threadZumSpeichern.start();
		
		//Antwort an Transformator wird zurückgesendet
		return "true"; 
	}
	
	
	/**
	 * Diese Method wird aufgerufen nachdem ein neuer Thread aus der Methode setDatenBatch() zum speichern gestartet wurde.
	 * Sie speichert den erhaltenen Stapel in der SQL Datenbank in der Tabelle umgedreht und sendet die gemessenen Zeiten 
	 * an den Transformator-Service zurück.
	 * 
	 * @return - Gibt einen String mit dem Inhalt "true" zurück wenn alles geklappt hat.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	public String speichernBatch() throws ClassNotFoundException, SQLException, MalformedURLException{
		//SQL Connection wird initialisiert
		initJDBCDriver();
		//Stapel wird in die Datenbank geschrieben
		sqlSchreiben();
		
		//Stapel mit den gemessenen Zeitstempel werden zum Transformator Service gesendet
		String response = "";
		ArrayList<String> datenZumFertigenStapel = new ArrayList<>();
		datenZumFertigenStapel.add(String.valueOf(aktuellerAuftrag.getStartZeit()));
		datenZumFertigenStapel.add(String.valueOf(aktuellerAuftrag.getVorSQLDatenSpeichern().get(aktuellerAuftrag.getVorSQLDatenSpeichern().size() - 1)));
		datenZumFertigenStapel.add(String.valueOf(System.nanoTime()));
		
		logger.info("Sende Stapel für Auftrag " + aktuellerAuftrag.getId() + " Protokoll: "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS") 
				+ "an Transformator Service.");
		
		//Nächster Stapel wird aus der SQL Datenbank gelesen.
		getAnfordernBatch();
		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlTransformator);
			response = restTarget.path(RESTbatchSpeichernFertig).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
					.post(Entity.entity(datenZumFertigenStapel, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);

		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			URL soapServiceURLTransformator = new URL(SOAPTransformatorServiceURL);
			QName soapQname = new QName(SOAPServiceBaseURL, SOAPTransformatorServiceName);
			Service soapTransformatorService = Service.create(soapServiceURLTransformator, soapQname);
			MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface soapTransformatorPort = soapTransformatorService
					.getPort(MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface.class);
			response = soapTransformatorPort.batchSpeichernFertig(datenZumFertigenStapel);
		} 
		if(response.equals("false")){
			logger.severe("FEHLER: false als Response vom Transformator Service erhalten beim Senden, dass der Batch fertig gespeichert wurde!");
			//TODO Auftrag löschen oder so
		}
		return "true";
	}

	/**
	 * Diese Methode schreibt die Daten in die SQL Datenbank
	 * 
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 */
	private void sqlSchreiben() throws SQLException {
		//Das Statement wird initialisiert
		Statement sqlStatement = sqlConnection.createStatement(); 
		lockSQL.lock();
		//Der Stapel wird in die Datenbank geschrieben
		for (int i = 0; i < aktuellerAuftrag.getStapelGedreht().size(); i++) {
			sqlStatement.addBatch("insert into umgedreht values ("+ indexDB++ + "," + ((aktuellerAuftrag.getStapelgroesse()*(aktuellerAuftrag.getAktuellerBatch()-1))+i) + ",'" + aktuellerAuftrag.getStapelGedreht().get(i) + "');");
			aktuellerAuftrag.setSqlGeradeSpeichern(aktuellerAuftrag.getSqlGeradeSpeichern() + 1);
		}
		lockSQL.unlock();
		lockSQL.lock();
		sqlStatement.executeBatch();
		lockSQL.unlock();
	}
	
	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/sqlStatus")
	public String getStatus(@QueryParam("id") String id) {
		//Variable wird mit 0 initialisiert. Wenn die Liste leer ist, ist evtl der Auftrag noch nicht angekommen und es wird 0 zurückgegeben
		String sqlGerade = "0";
		lockAuftragsliste.lock();
		if (Auftragsliste.size() == 0) {
			lockAuftragsliste.unlock();
			return sqlGerade;
		}
		
		//Wenn der Auftrag gefunden wird, wird der Wert SqlGeradeSpeichern zurückgegeben, wenn nicht wird 0 zurückgegeben.
		for (int i = 0; i < Auftragsliste.size(); i++) {
			if (Auftragsliste.get(i).getStartZeit() == Long.parseLong(id)) {
				sqlGerade = String.valueOf(Auftragsliste.get(i).getSqlGerade() + Auftragsliste.get(i).getSqlGeradeSpeichern());
				break;
			}
		}
		lockAuftragsliste.unlock();
		return sqlGerade;

	}
	

	@Override
	public void getDatenBatch(grpcServices.FortschrittRequest grpcAuftrag, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		//Hier sendet gRPC seine Anfrage für einen Auftrag

		//Die empfangenen Daten werden auf lokale Variablen kopiert
		FortschrittgRPC lokalerAuftrag = new MSBenchmark.microServices.FortschrittgRPC();
		lokalerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
		lokalerAuftrag.setFortschritt(grpcAuftrag);
		logger.info("Auftrag mit ProzessID " + lokalerAuftrag.getId() + " erhalten."
				+ " Protokoll: gRPC " + (lokalerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		//Berechnung der Stapel-Anzahl
		lokalerAuftrag.setBatchAnzahl((int) ((double) (lokalerAuftrag.getBis() - lokalerAuftrag.getVon()) / (double) lokalerAuftrag.getStapelgroesse()));
		if ((lokalerAuftrag.getBis() - lokalerAuftrag.getVon()) % lokalerAuftrag.getStapelgroesse() != 0) {
			//Wenn ein Rest übrigbleibt muss ein Stapel mehr berechnet werden
			lokalerAuftrag.setBatchAnzahl(lokalerAuftrag.getBatchAnzahl() + 1);
		}
		//Hinzufügen des Auftrags in der Liste
		Auftragsliste.add(lokalerAuftrag);
		//Response an Transformator-Service zurücksenden.
		StringMsg grpcResponse = StringMsg.newBuilder().setMessage("true").build();
		responseObserver.onNext(grpcResponse);
		responseObserver.onCompleted();
		
		try {
			getAnfordernBatchGRPC(lokalerAuftrag.getStartZeit());
		} catch (ClassNotFoundException  | SQLException e) {
			if(e instanceof ClassNotFoundException){
				logger.severe("FEHLER: ClassNotFoundException in getDatenBatch Methode");
			}else if(e instanceof SQLException){
				logger.severe("FEHLER SQLException: in getDatenBatch Methode");//TODO reset neue DB Verbindung oder so mit counter, so das nur 2 mal 
			}
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Die Methode getAnforderBatch liest die angeforderten Daten aus der Datenbank und sendet sie an 
	 * den Transformator zur Verarbeitung.
	 * 
	 * @return - Einen String mit dem Inhalt "true", wenn alles funktioniert hat.
	 * @throws ClassNotFoundException - Wird geworfen, wenn eine Klasse über einen String gesucht und nicht gefunden wird.
	 * @throws SQLException - Wird geworfen, wenn eine SQL-Abfrage nicht durchgeführt werden kann.
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	public String getAnfordernBatchGRPC(long id) throws ClassNotFoundException, SQLException{
		FortschrittgRPC lokalerAuftrag = aufrtagMitIDSuchenGRPC(id);   
		//Initialisierung der Datenbank und des Statement sowie ResultSet zum bearbeiten der Datenbankabfragen.
		initJDBCDriver();
		Statement sqlStatement = sqlConnection.createStatement();
		ResultSet sqlResultSet = null;

		//Builder ist für gRPC dieser muss vor der Schleifer erstellt werden. In ihn werden bei jeder Iteration die Ergebnisse mit addMessage() hinzugefügt.
		StapelMsg.Builder grpcStapel = null;

		//Diese Schleife wird aufgerufen solange noch weitere Stapel verarbeitet werden müssen
		if (lokalerAuftrag.getAktuellerBatch() < lokalerAuftrag.getBatchAnzahl()) {
			
			//Berechnung der Index Ränder die für den aktuellen Stapel gelten
			int von = lokalerAuftrag.getStapelgroesse() * lokalerAuftrag.getAktuellerBatch();
			int bis = lokalerAuftrag.getStapelgroesse() * (lokalerAuftrag.getAktuellerBatch() + 1);
			if (bis > lokalerAuftrag.getBis()) { //Wenn der letzte Stapel kleiner ist wird hier berücksichtigt
				bis = lokalerAuftrag.getBis();
			}
			logger.info("Beim Auftrag mit ProzessID " + lokalerAuftrag.getId() + 
					" werden die Strings mit den Index von " + von + " bis " + bis +" aus der Datenbank geladen");
			//Zeit speichern und Liste neu initialisieren
			lokalerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
			if (lokalerAuftrag.getStapel().size() != 0) {
				ArrayList<String> tmp = new ArrayList<>();
				lokalerAuftrag.setStapel(tmp);
			}
			
			//SQL Abfrage
			lockSQL.lock();
			sqlResultSet = sqlStatement.executeQuery("SELECT ID, Wert FROM werte WHERE id BETWEEN '" + von + "' AND '" + (bis - 1) + "' ;");
			lockSQL.unlock();
			
			//Ein neuer Builder erstellt und solange weitere Strings in der ResultSet befinden, wird der String in das StapelMsg-Objekt hinzugefügt.
			grpcStapel = StapelMsg.newBuilder();
			while (sqlResultSet.next()) {
				grpcStapel.addMessage(sqlResultSet.getString(2));
				lokalerAuftrag.setSqlGerade(lokalerAuftrag.getSqlGerade() + 1);
			}
	
			//Hier werden dem Stapel die ID des Auftrags sowie die Zeiten hinzugefügt.
			grpcStapel.addMessage(String.valueOf(lokalerAuftrag.getStartZeit()));
			grpcStapel.addMessage(String.valueOf(lokalerAuftrag.getVorSQLDatenHolen().get(lokalerAuftrag.getVorSQLDatenHolen().size() - 1)));
			grpcStapel.addMessage(String.valueOf(System.nanoTime()));
			lokalerAuftrag.setStapelGRPC(grpcStapel.build());
			
			//Counter-Variable wird erhöht nach dem der Batch erfolgreich geladen und versandt wurde
			lokalerAuftrag.setAktuellerBatch(lokalerAuftrag.getAktuellerBatch() + 1);
			
			//Stapel wird an den Transformator Service versandt.
			ManagedChannel gRPCChannelLokal = ManagedChannelBuilder.forAddress(gRPCAddressTransformator, gRPCChannelPortTransformator).usePlaintext(true).build();
			TransformatorGrpc.TransformatorBlockingStub grpcTransformatorBlockingStub = TransformatorGrpc.newBlockingStub(gRPCChannelLokal);
			StringMsg grpcResponse = grpcTransformatorBlockingStub.batchVerarbeitung(lokalerAuftrag.getStapelGRPC());
			gRPCChannelLokal.shutdown();
			if(grpcResponse.getMessage().equals("false")){
				logger.severe("FEHLER: false als Response vom Transformator Service erhalten, beim Senden des Stapels!");
				//TODO löschen des Auftrags
			}
			
			sqlResultSet.close();
		}else{
			//Wenn der letzte Stapel verarbeitet wurde wird hier reingesprungen und der Auftrag wird entfernt
			sqlStatement.close();
			Auftragsliste.remove(lokalerAuftrag);
			
			//Ein neuer Thread wird gestartet, damit allen anderen Microservices eine Nachricht bekommen, dass der Auftrag abgeschlossen wurde.
			Thread threadAuftragAbschliessen = new Thread(new Persistenz(lokalerAuftrag, true, true));
			threadAuftragAbschliessen.start();//TODO entfernen
		}
		return "true";
	}
	

	@Override
	public void setDatenBatch(grpcServices.StapelMsg grpcStapel, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		//Hier sendet gRPC seine umgedrehten Strings hin um diese in der SQL Datenbank zu speichern
		long id = Long.parseLong(grpcStapel.getMessageList().get(grpcStapel.getMessageList().size() - 1));
		FortschrittgRPC lokalerAuftrag = aufrtagMitIDSuchenGRPC(id);
		if(lokalerAuftrag==null){
			logger.severe("FEHLER: Auftrag konnte in der globalen Auftragsliste, in der Methode setDatenBatch für gRPC"
					+ " des Persistenz Service, nicht gefunden werden!");
			//TODO Auftrag löschen
			return;
		}
		logger.info("Stapel für Auftrag mit ProzessID " + lokalerAuftrag.getId() + " erhalten."
				+ " Protokoll: gRPC " + (lokalerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		
		lokalerAuftrag.setStapelGRPCgedreht(grpcStapel);
		lokalerAuftrag.getVorSQLDatenSpeichern().add(System.nanoTime());
			
		
		//Response an Transformator-Service zurücksenden.
		StringMsg grpcResponse = StringMsg.newBuilder().setMessage("true").build();
		responseObserver.onNext(grpcResponse);
		responseObserver.onCompleted();
		
		try {
			//SQL Connection wird geladen
			initJDBCDriver();
			//Das Statement wird initialisiert
			Statement sqlStatement = sqlConnection.createStatement(); 
			lockSQL.lock();
			//Der Stapel in das Statement geschrieben und Statement wird ausgeführt
			for (int i = 0; i < lokalerAuftrag.getStapelGRPCgedreht().getMessageList().size() - 1; i++) {
				sqlStatement.addBatch("insert into umgedreht values ("+ indexDB++ + "," + ((lokalerAuftrag.getStapelgroesse()*(lokalerAuftrag.getAktuellerBatch()-1))+i) + ",'" + lokalerAuftrag.getStapelGRPCgedreht().getMessage(i) + "');");
				lokalerAuftrag.setSqlGeradeSpeichern(lokalerAuftrag.getSqlGeradeSpeichern()+1);
			}
			sqlStatement.executeBatch();
			lockSQL.unlock();
			String response = "";

			StapelMsg.Builder grpcDatenZumFertigenStapel = StapelMsg.newBuilder();
	
			grpcDatenZumFertigenStapel.addMessage(String.valueOf(lokalerAuftrag.getStartZeit()))
						.addMessage(String.valueOf(lokalerAuftrag.getVorSQLDatenSpeichern().get(lokalerAuftrag.getVorSQLDatenSpeichern().size() - 1)))
						.addMessage(String.valueOf(System.nanoTime()));
			
			//Der nächste Stapel wird geladen
			getAnfordernBatchGRPC(id);
			
			logger.info("Sende Stapel für Auftrag " + lokalerAuftrag.getId() + " Protokoll: gRPC "
					+ (lokalerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS") 
					+ "an Transformator Service.");
			//Daten des gespeicherten Stapels werden an den Transformator Service gesendet
			ManagedChannel gRPCChannelLokal = ManagedChannelBuilder.forAddress(gRPCAddressTransformator, gRPCChannelPortTransformator).usePlaintext(true).build();
			TransformatorGrpc.TransformatorBlockingStub transformatorBlockingStub = TransformatorGrpc.newBlockingStub(gRPCChannelLokal);
			grpcResponse = transformatorBlockingStub.batchSpeichernFertig(grpcDatenZumFertigenStapel.build());
			gRPCChannelLokal.shutdown();
			if(grpcResponse.getMessage().equals("false")){
				logger.severe("FEHLER: false als Response vom Transformator Service erhalten, beim Senden des Stapels mit gRPC!");
				//TODO auftrag löschen
			}
			
		} catch (ClassNotFoundException | SQLException  e) {
			if (e instanceof ClassNotFoundException){
				logger.severe("FEHLER: ClassNotFoundException !");//TODO schauen kp was zu tun. evtl auftrag löschen
			}else if(e instanceof SQLException){
				logger.severe("FEHLER: SQLException beim Zugriff auf die Datenbank!");//TODO alles reseten neue DB verbindung und mit counter noch mal versuchen
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


	@Override
	public void getDatenBatchStream(grpcServices.FortschrittRequest grpcAuftragStream, io.grpc.stub.StreamObserver<grpcServices.StapelMsg> responseObserver) {
		
		
		//Diese Methode funktioniert noch nicht!!!!!!!!!!!!!
		
		
		try {
			this.aktuellerAuftrag = new MSBenchmark.microServices.FortschrittgRPC();
			this.aktuellerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
			this.aktuellerAuftrag.setFortschritt(grpcAuftragStream);
			Auftragsliste.add(this.aktuellerAuftrag);
			
			String response="";
			//System.out.println("Methode für Batch Anfrage gestartet. ID: " + f.getId());		
			initJDBCDriver();
			Statement sqlstatement = sqlConnection.createStatement();
			ResultSet sqlResultSet = null;

			StapelMsg.Builder grpcStapel = null;
			int i = aktuellerAuftrag.getVon();
			int batchAnzahl = (int) ((double) (aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) / (double) aktuellerAuftrag.getStapelgroesse());
			if ((aktuellerAuftrag.getBis() - aktuellerAuftrag.getVon()) % aktuellerAuftrag.getStapelgroesse() != 0) {
				batchAnzahl++;
			}
			for (int j = 0; j < batchAnzahl; j++) {
				int von = aktuellerAuftrag.getStapelgroesse() * j;
				int bis = aktuellerAuftrag.getStapelgroesse() * (j + 1);
				if (bis > aktuellerAuftrag.getBis()) {
					bis = aktuellerAuftrag.getBis();
				}
				aktuellerAuftrag.getVorSQLDatenHolen().add(System.nanoTime());
				if (aktuellerAuftrag.getStapel().size() != 0) {
					ArrayList<String> tmp = new ArrayList<>();
					aktuellerAuftrag.setStapel(tmp);
				}

				lockSQL.lock();
				sqlResultSet = sqlstatement.executeQuery(
						"SELECT ID,Wert FROM werte WHERE id BETWEEN '" + von + "' AND '" + (bis - 1) + "' ;");
				lockSQL.unlock();
				if (aktuellerAuftrag.getProtokoll() == gRPCStream) {
					grpcStapel = StapelMsg.newBuilder();
					while (sqlResultSet.next()) {
						grpcStapel.addMessage(sqlResultSet.getString(2));
						aktuellerAuftrag.setSqlGerade(aktuellerAuftrag.getSqlGerade() + 1);
					}
				} 

				if (aktuellerAuftrag.getProtokoll() == gRPCStream) {
					grpcStapel.addMessage(String.valueOf(aktuellerAuftrag.getStartZeit()));
					grpcStapel.addMessage(String.valueOf(aktuellerAuftrag.getVorSQLDatenHolen().get(aktuellerAuftrag.getVorSQLDatenHolen().size() - 1)));
					grpcStapel.addMessage(String.valueOf(System.nanoTime()));
					aktuellerAuftrag.setStapelGRPC(grpcStapel.build());
				} else {

				}
				aktuellerAuftrag.setBatchInBearbeitung(true);
				if (aktuellerAuftrag.getProtokoll() == REST) {
	
				} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
	
				} else if (aktuellerAuftrag.getProtokoll() == gRPCStream) {
//					TransformatorGrpc.TransformatorStub tbs = TransformatorGrpc.newStub();
//					StreamObserver<StringMsg> so = new StreamObserver<StringMsg>(){
//	
//						@Override
//						public void onNext(StringMsg value) {
//							
//						}
//	
//						@Override
//						public void onError(Throwable t) {
//							// TODO Auto-generated method stub
//						}
//	
//						@Override
//						public void onCompleted() {
//							// TODO Auto-generated method stub
//						}};
//					tbs.batchVerarbeitung(aktuellerAuftrag.getStapelGRPC(), so);
				}
	
				while (aktuellerAuftrag.isBatchInBearbeitung()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// System.out.println("Batch "+(j*f.getStapelgroesse())+
				// "von"+((j+1)*f.getStapelgroesse())+" aus SQL geladen. Starte
				// Thread zum bearbeiten an Transformator. ID: " + f.getId());
			}
			sqlstatement.close();
			sqlResultSet.close();
			Auftragsliste.remove(aktuellerAuftrag);
	
	//		int g=0;
	//		for (int j = 0; j < 10; j++) {
	//			System.out.println(request.getStartZeit());
	//			Builder smsg = StapelMsg.newBuilder();
	//			for (int i = 0; i < 10; i++) {
	//				smsg.addMessage(String.valueOf(g++));
	//			}
	//			StapelMsg msg = smsg.build();
	//			responseObserver.onNext(msg);
	//		}
	//		responseObserver.onCompleted();
			
			// System.out.println("Batch Anfrage erhalten. Füge Anfrage hinzu,
			// starte Thread und sende Response ID: " + f.getId());
	
	//		
	//		try {
	//			Thread t;
	//			t = new Thread(new Persistenz(this.f.getStartZeit(), false, false));
	//			t.start();
	//		} catch (ClassNotFoundException | SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//		StringMsg resp = StringMsg.newBuilder().setMessage("true").build();
	//		responseObserver.onNext(resp);
	//		responseObserver.onCompleted();
	}catch(Exception e){
			
		}
//	}
	
//	public String getItStream() {
//		
//		Thread t = new Thread(new MyResource(true));
//		t.start();
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051).usePlaintext(true).build();
//		PersistenzGrpc.PersistenzStub pbs = PersistenzGrpc.newStub(channel);
//		StreamObserver so = new StreamObserver<StapelMsg>(){
//
//			@Override
//			public void onNext(StapelMsg value) {
//				al.add(value);
//				
//			}
//
//			@Override
//			public void onError(Throwable t) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onCompleted() {
//				String test="";
//				for(StapelMsg msg : al){
//					for(int i=0; i<msg.getMessageCount();i++){
//						test += msg.getMessage(i) + "\n";
//					}
//					
//				}
//				System.out.println(test);
//			}
//		};
//		FortschrittRequest fr = FortschrittRequest.newBuilder().setStartZeit(System.nanoTime()).build();
//		pbs.getDatenBatchStream(fr,so);
//		String test="";
//		for(StapelMsg msg : al){
//			for(int i=0; i<msg.getMessageCount();i++){
//				test += msg.getMessage(i) + "\n";
//			}
//			
//		}
//		
//		return test;

	}

	@Override
	public io.grpc.stub.StreamObserver<grpcServices.FortschrittRequest> setDatenBatchStream(io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		
		
		//Diese Methode funktioniert noch nicht!!!!!!!!!!!!!
		
		
		return null;
	}
	

	@Override
	public void run() {
		//Hier wird reingesprungen, wenn ein neuer Thread gestartet wird
		//Je nach verwendeten Konstruktor werden die booleschen Variablen gesetzt und die entsprechenden Methoden aufgerufen
		try {
			if(initGrpc){
				starteGRPCServer();
			}else{
				if(complete){
					auftragAbschließen();
				}else{
					if(save){
						speichernBatch();
					}else{
						getAnfordernBatch();
					}
				}
			}
		}catch (ClassNotFoundException | SQLException | MalformedURLException e) {
			if(e instanceof ClassNotFoundException){
				logger.severe("FEHLER: ClassNotFoundException !");//TODO schauen kp was zu tun. evtl auftrag löschen
			}else if(e instanceof SQLException){
				logger.severe("FEHLER: SQLException beim Zugriff auf die Datenbank!");//TODO alles reseten neue DB verbindung und mit counter noch mal versuchen
			}else if(e instanceof MalformedURLException){
				logger.severe("FEHLER: MalformedURLException beim Senden!");//TODO ip passt wohl nicht. Auftrag löschen
			}
				
			e.printStackTrace();
		}
	}

	/**
	 * Wird aufgerufen sobald der letzte Stapel abgearbeitet wurde
	 * 
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	private void auftragAbschließen() throws MalformedURLException {
		logger.info("Sende Auftrag abschließen für Auftrag " + aktuellerAuftrag.getId() + " Protokoll: gRPC "
				+ (aktuellerAuftrag.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS") 
				+ "an Transformator Service.");
		String response = "";
		if (aktuellerAuftrag.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlTransformator);
			response = restTarget.path(RESTabschliessen).request(MediaType.APPLICATION_JSON + "; charset=UTF-8")
					.post(Entity.entity(String.valueOf(aktuellerAuftrag.getStartZeit()), MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
			
		} else if (aktuellerAuftrag.getProtokoll() == SOAP) {
			URL soapServiceURLTransformator = new URL(SOAPTransformatorServiceURL);
			QName soapQname = new QName(SOAPServiceBaseURL, SOAPTransformatorServiceName);
			Service soapTransformatorService = Service.create(soapServiceURLTransformator, soapQname);
			MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface soapTransformatorPort = soapTransformatorService
					.getPort(MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface.class);
			response = soapTransformatorPort.auftragAbschliessen(String.valueOf(aktuellerAuftrag.getStartZeit()));
			
		} else if (aktuellerAuftrag.getProtokoll() == gRPCSingle) {
			//Stapel wird an den Transformator Service versandt.
			ManagedChannel gRPCChannelLokal = ManagedChannelBuilder.forAddress(gRPCAddressTransformator, gRPCChannelPortTransformator).usePlaintext(true).build();
			TransformatorGrpc.TransformatorBlockingStub grpcTransformatorBlockingStub = TransformatorGrpc.newBlockingStub(gRPCChannelLokal);
			StringMsg auftragID = StringMsg.newBuilder().setMessage(String.valueOf(aktuellerAuftrag.getStartZeit())).build();
			StringMsg grpcResponse = grpcTransformatorBlockingStub.auftragAbschliessen(auftragID);
			response = grpcResponse.getMessage();
			gRPCChannelLokal.shutdown();
		}
		if(response.equals("false")){
			logger.severe("FEHLER: false als Response vom Transformator Service erhalten, beim Senden Auftrag abschließen mit gRPC!");
			//TODO nochmal senden mit counter und irgendwann löschen
		}
	}

	
	/**
	 * Testet die ausgeführten Aufträge, ob die SQL Einträge stimmen.
	 * 
	 * @return
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.TEXT_HTML)
	@Path("/tester")
	public String testerMethode(){
		String ergebnis = "Anzahl falscher Strings:";
		try {
			initJDBCDriver();
			Statement sqlStatement = sqlConnection.createStatement();
			ResultSet sqlResultSet = null;
			ArrayList<String> tabelleWerte= new ArrayList<>();
			ArrayList<String> tabelleWerteumgedreht= new ArrayList<>();
			ArrayList<Integer> tabelleIDsVonWerteZuDenStringsInWerteumgedreht= new ArrayList<>();
			//Tabelle Werte in ArrayListe speichern
			lockSQL.lock();
			sqlResultSet = sqlStatement.executeQuery("SELECT ID, wert FROM werte WHERE id BETWEEN '" + 0 + "' AND '" + 999999 + "' ;");
			lockSQL.unlock();
			
			while(sqlResultSet.next()){
				tabelleWerte.add(sqlResultSet.getString(2));
			}
			
			lockSQL.lock();
			sqlResultSet = sqlStatement.executeQuery("SELECT count(werteumgedreht) FROM umgedreht;");
			lockSQL.unlock();
			sqlResultSet.next();
			int bisWerteUmgedreht=Integer.parseInt(sqlResultSet.getString(1));
			boolean stop=false;
			int von = 0;
			int counterFalscheStrings=0;
			int stapelgroesse=10000;
			int rest=0;
			if(stapelgroesse>bisWerteUmgedreht){
				stapelgroesse=bisWerteUmgedreht;
			}
			if(bisWerteUmgedreht%stapelgroesse!=0){
				rest=bisWerteUmgedreht%stapelgroesse;
			}
			boolean fertig=false;
			while(!stop){
				//Tabelle Werte in ArrayListe speichern
				lockSQL.lock();
				sqlResultSet = sqlStatement.executeQuery("SELECT ID_werte, werteumgedreht FROM umgedreht WHERE id BETWEEN '" + von + "' AND '" + (von+stapelgroesse) + "' ;");
				lockSQL.unlock();
				
				while (sqlResultSet.next()) {
					tabelleIDsVonWerteZuDenStringsInWerteumgedreht.add(sqlResultSet.getInt(1));
					tabelleWerteumgedreht.add(sqlResultSet.getString(2));
				}

				String string = "";
				String umgedrehterString = "";
				for (int i = 0; i < tabelleWerteumgedreht.size(); i++) {
					string = tabelleWerte.get(tabelleIDsVonWerteZuDenStringsInWerteumgedreht.get(i));
					umgedrehterString = tabelleWerteumgedreht.get(i);
					for (int j = 0; j < umgedrehterString.length(); j++) {
						if (string.charAt(j) != umgedrehterString.charAt(umgedrehterString.length() - j - 1)) {
							counterFalscheStrings++;
						}
					}
				}
				
				tabelleWerteumgedreht = new ArrayList<>();
				tabelleIDsVonWerteZuDenStringsInWerteumgedreht = new ArrayList<>();
				if (von + rest == bisWerteUmgedreht) {
					von += rest;
					stapelgroesse=rest;
					fertig=true;
				} else {
					von += stapelgroesse;
				}
				if(von>bisWerteUmgedreht||fertig){
					return bisWerteUmgedreht + " Stings getestet. "  + ergebnis + " " + counterFalscheStrings;
				}
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			if(e instanceof ClassNotFoundException){
				logger.severe("FEHLER: ClassNotFoundException !");//TODO schauen kp was zu tun. evtl auftrag löschen
			}else if(e instanceof SQLException){
				logger.severe("FEHLER: SQLException beim Zugriff auf die Datenbank!");//TODO alles reseten neue DB verbindung und mit counter noch mal versuchen
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return ergebnis;
	}
}
