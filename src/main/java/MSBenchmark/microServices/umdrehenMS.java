package MSBenchmark.microServices;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import grpcServices.UmdrehenMSGrpc.UmdrehenMSImplBase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;


//http://localhost:8080/microServices/umdrehenMSService?wsdl
/**
 * @author Benjamin Leonhardt
 *
 * UmdrehenMS dreht die Stapel mit Strings die ihm zugesandt werden um und sendet diese wieder zurück
 *
 */
@WebService(endpointInterface = "MSBenchmark.microServices.umdrehenMSInterface", serviceName = "umdrehenMSService")
@Path("umdrehenMS")
public class umdrehenMS extends UmdrehenMSImplBase implements umdrehenMSInterface, Runnable {

//	private static final String ipTransformator					= 	"10.15.240.215";
	private static final String ipTransformator					= 	"127.0.0.1";
	
	//REST Adressen Transformator
	private static final String RESTIPTransformator				=	ipTransformator;											//REST IP Adresse Transformator
	private static final String RESTPortTransformator			=	"8080";													//REST Port Transformator
	private static final String RESTbaseUrlTransformator		=	"http://"+RESTIPTransformator+":"+RESTPortTransformator;	//REST IP-Adresse und Port an der der Transformator-Service zu finden ist 
	
	private static final String RESTspeichernBatch = "/microServices/webapi/Transformator/batchWeiterreichen";					//REST Adresse für die Methode batchWeiterreichen


	//SOAP Adressen Transformator
	private static final String SOAPIPTransformator				=	ipTransformator;											//SOAP IP Adresse zum Transformator Service
	private static final String SOAPPortTransformator			=	"8080";													//SOAP Port zum Transformator Service
	private static final String SOAPbaseUrl 					=	"http://"+SOAPIPTransformator+":"+SOAPPortTransformator;	//SOAP IP-Adresse und Port an der der Transformator-Service zu finden ist 
	
	private static final String SOAPTransformatorServiceURL  	=	SOAPbaseUrl + "/microServices/TransformatorService?wsdl";	//SOAP Adresse für den Transformator Service
//	private static final String SOAPTransformatorServiceURL  	=	"file:/opt/jboss/wildfly/standalone/TransformatorService.xml";//Angepasste WSDL Datei für Container
	private static final String SOAPTransformatorServiceName 	=	"TransformatorService";										//SOAP Name für Transformator Service
	private static final String SOAPServiceBaseURL 				=	"http://microServices.MSBenchmark/";						//SOAP ServiceBaseURL. Entspricht dem Package Namen in umgekehrter Reihenfolge

	//gRPC Server UmdrehenMS
	private static Server gRPCServer;																							//gRPCServer damit von der UmdrehenMS Service auf den Transformator Service über gRPC zugegriffen werden kann
	private static final int gRPCServerPortUmdrehenMS 			= 30051;														//Port für den gRPC Transformator Service
	private boolean initGrpc 									= false;														//lokale Variable die für das initialisieren des gRPC Servers verwendet wird

	//gRPC Adressen Transformator
	private static final String gRPCAddressTransformator 		= "127.0.0.1";													//Adresse für den gRPC Transformator Service
//	private static final String gRPCAddressTransformator 		= "10.15.245.76";
	private static final int gRPCChannelPortTransformator		= 20051;														//Port für den gRPC Transformator Service

	//Globale Listen und lokale Variablen und Locks
	private static ArrayList<FortschrittgRPC> Auftragsliste;																	//Liste der Aufträge die bearbeitet werden
	private static ArrayList<grpcServices.FortschrittRequest> hilfsAuftragsListeGRPC;											//Hilfsliste für gRPC Aufträge
	
	private FortschrittgRPC aktuellerAuftrag;																					//Lokale Variable zum Auftrag zwischenspeichern
	private grpcServices.FortschrittRequest aktuellerAuftragGRPC;																//Lokale Variable für gRPC Aufträge
	
	private static Lock lockAuftragslisteUmdrehen = new ReentrantLock();														//Lock zum bearbeiten der Auftragsliste fortschrittMS
	private static Lock lockAuftragslisteUmdrehenGRPC = new ReentrantLock();													//Lock zum bearbeiten der Auftragsliste fortschrittMSGRPC

	//Java Logging API
	private static final Logger logger 						= Logger.getLogger(Transformator.class.getName());

	
	private static final int REST = 1, SOAP = 2, gRPCSingle = 3, gRPCStream = 4;												//Hilfsvariablen für das Protokoll. Zur besseren Lesbarkeit des Quellcodes

	/**
	 * Standard Konstruktor
	 */
	public umdrehenMS() {
		logger.setLevel(Level.ALL);
		if (Auftragsliste == null) {
			Auftragsliste = new ArrayList<>();
		}if (hilfsAuftragsListeGRPC == null) {
			hilfsAuftragsListeGRPC = new ArrayList<>();
		}
	}

	/**
	 * Dieser Konstruktor wird verwendet wenn ein neuer Thread mit einem neuen Auftrag 
	 * empfangen wurde.
	 * 
	 * @param auftrag - Objekt vom Typ Fortschritt. Entspricht einem abzuarbeitenden Auftrag.
	 * 			Wird in ein Objekt vom Typ FortschrittgRPC gecastet. Dies besitzt ein
	 * 			paar zusätzliche Felder und entspricht dem Typ der Globalen Auftragsliste 
	 * 			fortschrittMS.
	 */
	public umdrehenMS(Fortschritt auftrag) {
		if (Auftragsliste == null) {
			Auftragsliste = new ArrayList<>();
		}
		if (hilfsAuftragsListeGRPC == null) {
			hilfsAuftragsListeGRPC = new ArrayList<>();
		}
		aktuellerAuftrag = new FortschrittgRPC(auftrag);
	}

	/**
	 * Dieser Konstruktor wird aufgerufen, wenn ein Auftrag schon in der Liste
	 * vorhanden ist. Er empfängt nur noch die ID des Auftrags und hängt den 
	 * Zeiger des entsprechenden Objektes auf die Lokale Variable batch und bei
	 * verwendetem gRPC als Protokoll auch auf die Variable batchGRPC um.
	 * 
	 * 
	 * @param id - Die ID eines Auftrages. Dient zur Identifikation, welcher Stapel
	 * 			zu welchem Auftrag gehört.
	 */
	public umdrehenMS(long id) {
		auftragMitIDSuchen(id);
	}

	private void auftragMitIDSuchen(long id) {
		for (int i = 0; i < Auftragsliste.size(); i++) {
			lockAuftragslisteUmdrehen.lock();
			if (Auftragsliste.get(i).getStartZeit() == id) {
				aktuellerAuftrag = Auftragsliste.get(i);
				lockAuftragslisteUmdrehen.unlock();
				break;
			}
			lockAuftragslisteUmdrehen.unlock();
		}
		if(aktuellerAuftrag.getProtokoll()==gRPCSingle){
			for (int i = 0; i < hilfsAuftragsListeGRPC.size(); i++) {
				lockAuftragslisteUmdrehenGRPC.lock();
				if (hilfsAuftragsListeGRPC.get(i).getStartZeit() == id) {
					aktuellerAuftragGRPC = hilfsAuftragsListeGRPC.get(i);
					lockAuftragslisteUmdrehenGRPC.unlock();
					break;
				}
				lockAuftragslisteUmdrehenGRPC.unlock();
			}
		}
	}
	
	/**
	 * Diese Methode sucht den Auftrag mit der als Argument übergebenen ID raus und kopiert einen Pointer in die 
	 * lokale Variable aktuellerAuftrag der auf diesen Auftrag zeigt.
	 * 
	 * @param id - ID des Auftrags der gesucht werden soll.
	 */
	private FortschrittgRPC aufrtagMitIDSuchenGRPC(long id) {
		FortschrittgRPC auftrag;
		for (int i = 0; i < Auftragsliste.size(); i++) {
			lockAuftragslisteUmdrehen.lock();
			if (Auftragsliste.get(i).getStartZeit() == id) {
				auftrag = Auftragsliste.get(i);
				lockAuftragslisteUmdrehen.unlock();
				return auftrag;
			}
			lockAuftragslisteUmdrehen.unlock();
		}
		return null;
	}
	
	/**
	 * Dieser Konstruktor wird aufgerufen, wenn der gRPC Server initialisiert werden soll.
	 * Er weist der boolean initGrpc true zu, und läuft nach dem Start eines neuen Threads,
	 * in der run() Methode in die Methode zum starten des gRPC Servers.
	 * 
	 * @param initGrpc - boolean zur Identifizierung ob der gRPC Server initialisiert werden soll
	 */
	public umdrehenMS(boolean initGrpc){
		this.initGrpc = initGrpc;
	}

	/**
	 * REST Methode, welche einen neuen Thread startet, die den gRPC Server startet.
	 * 
	 * @return - String mit true sobald der Server gestartet wurde, damit der Transformator 
	 * 			Service eine Bestätigung bekommt, das alles wie erwartet geklappt hat.
	 */
	@GET
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/initGrpc")
	public String initGrpc() {
		Thread threadZumGrpcServerStarten = new Thread(new umdrehenMS(true));
		threadZumGrpcServerStarten.start();
		long vorWarten = System.currentTimeMillis();
		while (gRPCServer == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				logger.severe("FEHLER: Interrupt für den Sleep beim warten auf den gRPC Server");
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
			if (System.currentTimeMillis() - vorWarten > 5000) {
				logger.severe("FEHLER: Starten des gRPC Server dauert länger als erwartet. Sende False zurück an Transformator Service");
				return "false";
			}
		}
		return "true";
	}
	
	/**
	 * Die Methode starteGRPCServer() startet einen gRPC Server, damit der UmdrehenMS Service
	 * via gRPC Protokoll auf den Transformator Service zugreifen kann.
	 * 
	 */
	private void starteGRPCServer() {
		if (umdrehenMS.gRPCServer == null) {
			try {
				umdrehenMS.gRPCServer = ServerBuilder.forPort(gRPCServerPortUmdrehenMS).addService(new umdrehenMS()).build().start();
				logger.info("UmdrehenMS Server gestartet.");
				initGrpc = false;
				umdrehenMS.gRPCServer.awaitTermination();
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/stapel")
	public String setDatenStapelUMS(Fortschritt auftragMitStapel){

		logger.info("Auftrag mit Stapel erhalten. ProzessID " + auftragMitStapel.getId() + " erhalten."
				+ " Protokoll: " + (auftragMitStapel.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (auftragMitStapel.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));

		long id = auftragMitStapel.getStartZeit();
		//Suchen des Auftrages in der Liste
		lockAuftragslisteUmdrehen.lock();//TODO
		boolean gefunden = false;
		for (int i = 0; i < Auftragsliste.size(); i++) {
			if (Auftragsliste.get(i).getStartZeit() == id) {
				Auftragsliste.get(i).getVorUmdrehenMS().add(System.nanoTime());
				if(auftragMitStapel!=null){
					Auftragsliste.get(i).setStapel(auftragMitStapel.getStapel());
				}
				gefunden = true;
				break;
			}
		}
		
		//Wenn in der Liste kein Auftrag mit der ID gefunden wurde, wird er zur Liste hinzugefügt
		if (gefunden == false) {
			if(auftragMitStapel.getVorUmdrehenMS()==null){
				auftragMitStapel.setVorUmdrehenMS(new ArrayList<Long>());
			}
			auftragMitStapel.getVorUmdrehenMS().add(System.nanoTime());
			Auftragsliste.add(new FortschrittgRPC(auftragMitStapel));
		}
		if (auftragMitStapel.getProtokoll() != gRPCSingle) {
			//Starten eines neuen Threads der den Stapel bearbeitet
			Thread threadZumAuftragBearbeiten = new Thread(new umdrehenMS(id));
			threadZumAuftragBearbeiten.start();
		}
		lockAuftragslisteUmdrehen.unlock();

		return "true";
	}

	@Override
	public void setDaten(grpcServices.FortschrittRequest grpcAuftragMitStapel, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		long vor = System.nanoTime();
		
		//Einsprungpunkt von gRPC. Empfangener Auftrag wird erst lokal gespeichert und danach, wenn nicht schon vorhanden, in die gRPC Auftragsliste hinzugefügt.
		grpcServices.FortschrittRequest lokalerAuftragGrpc  = grpcAuftragMitStapel;
		long id = lokalerAuftragGrpc.getStartZeit();
		Fortschritt auftragFuerListe = new Fortschritt();
		auftragFuerListe.setStartZeit(id);
		auftragFuerListe.setProtokoll(3);
		logger.info("Auftrag mit Stapel erhalten. ProzessID " + auftragFuerListe.getId() + " erhalten."
				+ " Protokoll: gRPC "
				+ (auftragFuerListe.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		setDatenStapelUMS(auftragFuerListe);
		
		lockAuftragslisteUmdrehenGRPC.lock();
		boolean gefunden = false;
		for (int i = 0; i < hilfsAuftragsListeGRPC.size(); i++) {
			if (hilfsAuftragsListeGRPC.get(i).getStartZeit() == id) {
				hilfsAuftragsListeGRPC.set(i, lokalerAuftragGrpc);
				gefunden = true;
				break;
			}
		}
		if (gefunden == false) {
			hilfsAuftragsListeGRPC.add(lokalerAuftragGrpc);
		}
		lockAuftragslisteUmdrehenGRPC.unlock();
		//Die Methode setDatenStapelUMS wird mit einem NULL Pointer als Argument aufgerufen. Damit weiß die Methode, dass ein gRPC Auftrag Empfangen wurde


		//Ein String als Response mit dem Inhalt true wird an den Transformator Service versandt
		StringMsg grpcResponse = StringMsg.newBuilder().setMessage("true").build();
		responseObserver.onNext(grpcResponse);
		responseObserver.onCompleted();
		FortschrittgRPC lokalerAuftrag = aufrtagMitIDSuchenGRPC(id);   
		
		//Rückgabe-Variable für gRPC und String Hilfsvariable initialisieren
		StapelMsg.Builder grpcStapel=null;
		String tmp = "";
		
		//Wenn der Auftrag mit gRPC versandt wird. Es wird zuerst ein Builder erstellt und alle Strings umgedreht hinzufügt
		grpcStapel = StapelMsg.newBuilder();
		for(int i=0; i<lokalerAuftragGrpc.getStapelList().size();i++){
			lokalerAuftrag.setMsUmdrehenGerade(lokalerAuftrag.getMsUmdrehenGerade() + 1);
			tmp = "";
			for (int j = lokalerAuftragGrpc.getStapelList().get(i).length() - 1; j >= 0; j--) {
				tmp += lokalerAuftragGrpc.getStapelList().get(i).charAt(j);
			}
			grpcStapel.addMessage(tmp);
		}
		//ID und Zeiten noch am ende hinzufügen
		//aktuellerAuftrag.getNachUmdrehenMS().add(System.nanoTime());//TODO evtl kann das weg
		grpcStapel.addMessage(String.valueOf(lokalerAuftrag.getStartZeit()));
		grpcStapel.addMessage(String.valueOf(vor));
		grpcStapel.addMessage(String.valueOf(System.nanoTime()));

		logger.info("Stapel gedreht und wird wieder an den Transformator Service zurückgesendet."
				+ " ProzessID " + auftragFuerListe.getId() + " erhalten."
				+ " Protokoll: gRPC " + (auftragFuerListe.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		//Umgedrehter Stapel wird mit dem ausgewählten Protokoll versandt
		ManagedChannel gRPCChannelLokal = ManagedChannelBuilder.forAddress(gRPCAddressTransformator, gRPCChannelPortTransformator).usePlaintext(true).build();
		TransformatorGrpc.TransformatorBlockingStub grpcTransformatorBlockingStub = TransformatorGrpc.newBlockingStub(gRPCChannelLokal);
		grpcResponse = grpcTransformatorBlockingStub.batchWeiterreichen(grpcStapel.build());
		gRPCChannelLokal.shutdown();
		if(grpcResponse.getMessage().equals("false")){
			logger.severe("FEHLER im UmdrehenMS: Beim Senden des gRPC Auftrags " + lokalerAuftrag.getStartZeit() 
						+ " an die Transformator Service Methode batchWeiterreichen()!");
			//TODO Auftrag löschen
		}
	}

	/**
	 * Zum Abfragen des Status eines Auftrages.
	 * 
	 * @param id - Id des Abzufragenden Auftrages
	 * @return - Die Zähler Variable msUmdrehenGerade. Entspricht der Anzahl an verarbeiteter Strings für diesen Auftrag.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/msUmdrehenStatus")
	public String getStatusUMS(@QueryParam("id") String id) {
		//Initialisieren des Response Strings und bei leerer Liste wird 0 zurückgegeben. Wenn ein Auftrag noch nicht hier angekommen ist sind noch keine Daten verarbeitet worden.
		String msUmdrehenGerade = "0";
		lockAuftragslisteUmdrehen.lock();
		if (Auftragsliste.size() == 0) {
			lockAuftragslisteUmdrehen.unlock();
			return msUmdrehenGerade;
		}
		//Ansonsten wird die Variable in einen String umgewandelt und zurückgegeben. Wenn in der Liste nicht vorhanden wird auch die 0 zurückgegeben
		for (int i = 0; i < Auftragsliste.size(); i++) {
			if (Auftragsliste.get(i).getStartZeit() == Long.parseLong(id)) {
				msUmdrehenGerade = String.valueOf(Auftragsliste.get(i).getMsUmdrehenGerade());
				break;
			}
		}
		lockAuftragslisteUmdrehen.unlock();
		return msUmdrehenGerade;
	}

	/**
	 * Die Methode umdrehenM(), dreht den String um und fügt am ende die ID, sowie die gemessenen Zeiten hinzu.
	 * 
	 * @throws MalformedURLException - Wird geworfen, wenn die URL keine korrekte Syntax aufweist.
	 */
	private void umdrehenM(long id) throws MalformedURLException {
		String tmp = "";
		FortschrittgRPC lokalerFortschritt = aufrtagMitIDSuchenGRPC(id);

		//Wenn mit SOAP oder REST versandt wird, wird zuerst der Stapel in der Lokale variable neu initialisiert, damit er leer ist
		if (lokalerFortschritt.getStapelGedreht().size() != 0) {
			ArrayList<String> tmp1 = new ArrayList<String>();
			lokalerFortschritt.setStapelGedreht(tmp1);
		}
		//daraufhin werden alle Strings umgedreht darin hinzugefügt
		while (lokalerFortschritt.getStapel().size() != 0) {
			lokalerFortschritt.setMsUmdrehenGerade(lokalerFortschritt.getMsUmdrehenGerade() + 1);
			tmp = "";
			for (int j = lokalerFortschritt.getStapel().get(0).length() - 1; j >= 0; j--) {
				tmp += lokalerFortschritt.getStapel().get(0).charAt(j);
			}
			// System.out.println(batch.getStapel().get(0)+" " +tmp);
			lokalerFortschritt.getStapelGedreht().add(tmp);
			lokalerFortschritt.getStapel().remove(0);
		}
		//ID und Zeiten noch am ende hinzufügen
		//aktuellerAuftrag.getNachUmdrehenMS().add(System.nanoTime());//TODO kann auch weg wie oben
		lokalerFortschritt.getStapelGedreht().add(String.valueOf(lokalerFortschritt.getStartZeit()));
		lokalerFortschritt.getStapelGedreht().add(String.valueOf(lokalerFortschritt.getVorUmdrehenMS().get(lokalerFortschritt.getVorUmdrehenMS().size() - 1)));
		lokalerFortschritt.getStapelGedreht().add(String.valueOf(System.nanoTime()));
		
		logger.info("Stapel gedreht und wird wieder an den Transformator Service zurückgesendet."
				+ " ProzessID " + lokalerFortschritt.getId() + " erhalten."
				+ " Protokoll: " + (lokalerFortschritt.getProtokoll() == 1 ? "REST" : "SOAP") + " "
				+ (lokalerFortschritt.getProtokoll() == 1 ? "mit UmdrehenMS" : "ohne UmdrehenMS"));
		
		//Speicher freigeben
		ArrayList<String> gedrehterStapel = lokalerFortschritt.getStapelGedreht();
		lokalerFortschritt.setStapelGedreht(new ArrayList<String>());
		
		//Umgedrehter Stapel wird mit dem ausgewählten Protokoll versandt
		String response = "";
		if (lokalerFortschritt.getProtokoll() == REST) {
			Client restClient = ClientBuilder.newClient();
			WebTarget restTarget = restClient.target(RESTbaseUrlTransformator);
			response = restTarget.path(RESTspeichernBatch).request(MediaType.APPLICATION_JSON + "; charset=UTF-8").post(Entity.entity(gedrehterStapel, MediaType.APPLICATION_JSON + "; charset=UTF-8"), String.class);
		} else if (lokalerFortschritt.getProtokoll() == SOAP) {
			URL soapServiceURLTransformator = new URL(SOAPTransformatorServiceURL);
			QName soqpQname = new QName(SOAPServiceBaseURL, SOAPTransformatorServiceName); 
			Service soapTransformatorService = Service.create(soapServiceURLTransformator, soqpQname);
			MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface soapTransformatorPort = soapTransformatorService.getPort(MSBenchmark.microServices.SOAPStubs.transformator.TransformatorInterface.class);
			response = soapTransformatorPort.batchWeiterreichen(gedrehterStapel);
		} 
		
		if(response.equals("false")){
			logger.severe("FEHLER im UmdrehenMS: Beim Senden des Auftrags " + lokalerFortschritt.getStartZeit() + " an die Transformator Service Methode batchWeiterreichen()!");
			//TODO auftrag löschen
		}
	}
	
	@Override
	@POST
	@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	@Path("/auftragAbschliessen")
	public String auftragAbschliessenUMS(String id) {
		long ids = Long.parseLong(id);
		//Auftrag in der Auftragsliste suchen und wenn gefunden löschen. Ansonsten eine Fehlermeldung ausgeben und false zurückgeben
		FortschrittgRPC lokalerAuftrag = aufrtagMitIDSuchenGRPC(ids);
		if(lokalerAuftrag==null){
			logger.info("FEHLER im UmdrehenMS: Das Löschen des Auftrags " + id + " aus der globalen Liste schlug fehl! Auftrag nicht gefunden!");
			return "false";
		}
		Auftragsliste.remove(lokalerAuftrag);
		
		//Wenn Auftrag ein gRPC-Auftrag war diesen aus der Hilfsliste löschen
		if(lokalerAuftrag.getProtokoll()==gRPCSingle){
			boolean gefunden=false;
			for (int i = 0; i < hilfsAuftragsListeGRPC.size(); i++) {
				lockAuftragslisteUmdrehenGRPC.lock();
				if (hilfsAuftragsListeGRPC.get(i).getStartZeit() == ids) {
					gefunden = true;
					hilfsAuftragsListeGRPC.remove(i);
					lockAuftragslisteUmdrehenGRPC.unlock();
					break;
				}
				lockAuftragslisteUmdrehenGRPC.unlock();
			}
			//Wenn nicht gefunden Fehler ausgeben und false zurückgeben
			if(!gefunden){
				logger.severe("FEHLER im UmdrehenMS: Das Löschen des gRPC Auftrags " + id + " aus der globalen Liste schlug fehl! gRPC Auftrag nicht gefunden!");
				return "false";
			}
		}
		
		return "true";
	}
	
	public void auftragAbschliessen(grpcServices.StringMsg grpcAuftragsID, io.grpc.stub.StreamObserver<grpcServices.StringMsg> responseObserver) {
		//Methode auftragAbschliessen() mit der ID als Argument starten
		String response = auftragAbschliessenUMS(grpcAuftragsID.getMessage());
		if(response.equals("false")){
			//TODO 
			logger.severe("FEHLER im UmdrehenMS: Das Abschließen des gRPC Auftrags " + grpcAuftragsID.getMessage() + " schlug fehl!");
		}
		//Response zurücksenden
		StringMsg respStringMsg = StringMsg.newBuilder().setMessage(response).build();
		responseObserver.onNext(respStringMsg);
		responseObserver.onCompleted();
	}

	@Override
	public void run() {
		//Hier wird reingesprungen, wenn ein neuer Thread gestartet wird
		//Je nach verwendeten Konstruktor werden die booleschen Variablen gesetzt und die entsprechenden Methoden aufgerufen
		try {
			if(initGrpc){//Hier wird reingesprungen, wenn der gRPC Server initialisiert werden soll
				starteGRPCServer();
			}else{//Hier wird reingesprungen, wenn ein Stapel verarbeitet werden soll
				umdrehenM(this.aktuellerAuftrag.getStartZeit());
			}
		} catch (MalformedURLException | ThreadDeath e) {
			if(e instanceof ClassNotFoundException){
				logger.severe("FEHLER: ClassNotFoundException !");//TODO schauen kp was zu tun. evtl auftrag löschen
			}else if(e instanceof ThreadDeath){
				logger.severe("FEHLER: ThreadDeath !");//TODO alles TOT keine ahnung google hilft evtl
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
