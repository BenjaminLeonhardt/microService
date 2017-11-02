package MSBenchmark.microServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import grpcServices.FortschrittRequest;

@XmlRootElement
public class Fortschritt implements Comparable<Fortschritt> {
	private int parameter;
	private int protokoll;
	private ArrayList<String> stapel;
	private ArrayList<String> stapelGedreht;
	private boolean alsStapel;
	private boolean transformatorMS;
	private int von;
	private int bis;
	private int gerade;
	private int sqlGerade;
	private int sqlGeradeSpeichern;
	private int msUmdrehenGerade;
	private long StartZeit;
	private ArrayList<Long> vorSQLDatenHolen;
	private ArrayList<Long> nachSQLDatenHolen;
	private ArrayList<Long> vorTransformator;
	private ArrayList<Long> nachTransformator;
	private ArrayList<Long> vorUmdrehenMS;
	private ArrayList<Long> nachUmdrehenMS;
	private ArrayList<Long> vorTransformatorMS;
	private ArrayList<Long> nachTransformatorMS;
	private ArrayList<Long> vorSQLDatenSpeichern;
	private ArrayList<Long> nachSQLDatenSpeichern;
	private ArrayList<Long> transformatorBatchErhalten;
	private long EndZeit;
	private int id;
	private String titel;
	private double durchschnittszeitEinzeln;
	private int stapelgroesse;
	private boolean batchInBearbeitung;
	private boolean batchAmSpeichern;

	public Fortschritt() {
		if (stapel == null) {
			stapel = new ArrayList<>();
		}
		if (stapelGedreht == null) {
			stapelGedreht = new ArrayList<>();
		}
		if (vorSQLDatenHolen == null) {
			vorSQLDatenHolen = new ArrayList<>();
		}
		if (nachSQLDatenHolen == null) {
			nachSQLDatenHolen = new ArrayList<>();
		}
		if (vorTransformator == null) {
			vorTransformator = new ArrayList<>();
		}
		if (nachTransformator == null) {
			nachTransformator = new ArrayList<>();
		}
		if (vorUmdrehenMS == null) {
			vorUmdrehenMS = new ArrayList<>();
		}
		if (nachUmdrehenMS == null) {
			nachUmdrehenMS = new ArrayList<>();
		}
		if (vorSQLDatenSpeichern == null) {
			vorSQLDatenSpeichern = new ArrayList<>();
		}
		if (nachSQLDatenSpeichern == null) {
			nachSQLDatenSpeichern = new ArrayList<>();
		}
		if (stapelGedreht == null) {
			stapelGedreht = new ArrayList<>();
		}
		if (vorTransformatorMS == null) {
			vorTransformatorMS = new ArrayList<>();
		}
		if (nachTransformatorMS == null) {
			nachTransformatorMS = new ArrayList<>();
		}
		if (transformatorBatchErhalten == null) {
			transformatorBatchErhalten = new ArrayList<>();
		}
	}

	public Fortschritt(Fortschritt f) {
		this.protokoll = f.protokoll;
		this.parameter = f.parameter;
		this.von = f.von;
		this.bis = f.bis;
		this.StartZeit = f.StartZeit;
		this.titel = f.titel;
		this.alsStapel = f.isAlsStapel();
		if (parameter == 2) {
			this.transformatorMS = true;
		} else {
			this.transformatorMS = false;
		}
		this.stapelgroesse = f.stapelgroesse;
		
		
		if (f.stapel == null) {
			stapel = new ArrayList<>();
		} else {
			this.stapel = f.stapel;
		}
		if (f.stapelGedreht == null) {
			stapelGedreht = new ArrayList<>();
		} else {
			this.stapelGedreht = f.stapelGedreht;
		}
		if (f.vorSQLDatenHolen == null) {
			vorSQLDatenHolen = new ArrayList<>();
		} else {
			this.vorSQLDatenHolen = f.vorSQLDatenHolen;
		}
		if (f.nachSQLDatenHolen == null) {
			nachSQLDatenHolen = new ArrayList<>();
		} else {
			this.nachSQLDatenHolen = f.nachSQLDatenHolen;
		}
		if (f.vorTransformator == null) {
			vorTransformator = new ArrayList<>();
		} else {
			this.vorTransformator = f.vorTransformator;
		}
		if (f.nachTransformator == null) {
			nachTransformator = new ArrayList<>();
		} else {
			this.nachTransformator = f.nachTransformator;
		}
		if (f.vorUmdrehenMS == null) {
			vorUmdrehenMS = new ArrayList<>();
		} else {
			this.vorUmdrehenMS = f.vorUmdrehenMS;
		}
		if (f.nachUmdrehenMS == null) {
			nachUmdrehenMS = new ArrayList<>();
		} else {
			this.nachUmdrehenMS = f.nachUmdrehenMS;
		}
		if (f.vorSQLDatenSpeichern == null) {
			vorSQLDatenSpeichern = new ArrayList<>();
		} else {
			this.vorSQLDatenSpeichern = f.vorSQLDatenSpeichern;
		}
		if (f.nachSQLDatenSpeichern == null) {
			nachSQLDatenSpeichern = new ArrayList<>();
		} else {
			this.nachSQLDatenSpeichern = f.nachSQLDatenSpeichern;
		}
		if (f.stapelGedreht == null) {
			stapelGedreht = new ArrayList<>();
		} else {
			this.stapelGedreht = f.stapelGedreht;
		}
		if (f.vorTransformatorMS == null) {
			vorTransformatorMS = new ArrayList<>();
		} else {
			this.vorTransformatorMS = f.vorTransformatorMS;
		}
		if (f.nachTransformatorMS == null) {
			nachTransformatorMS = new ArrayList<>();
		} else {
			this.nachTransformatorMS = f.nachTransformatorMS;
		}
		if (f.transformatorBatchErhalten == null) {
			transformatorBatchErhalten = new ArrayList<>();
		} else {
			this.transformatorBatchErhalten = f.transformatorBatchErhalten;
		}
	}

	public Fortschritt(int parameter, int von, int bis, long startZeit, String titel, int proto, int stapelgroesse) {
		this.protokoll = proto;
		this.parameter = parameter;
		this.von = von;
		this.bis = bis;
		this.StartZeit = startZeit;
		this.titel = titel;
		this.alsStapel = true;
		if (parameter == 2) {
			this.transformatorMS = true;
		} else {
			this.transformatorMS = false;
		}
		this.stapelgroesse = stapelgroesse;
		vorSQLDatenHolen = new ArrayList<>();
		nachSQLDatenHolen = new ArrayList<>();
		vorTransformator = new ArrayList<>();
		nachTransformator = new ArrayList<>();
		vorTransformatorMS = new ArrayList<>();
		nachTransformatorMS = new ArrayList<>();
		vorUmdrehenMS = new ArrayList<>();
		nachUmdrehenMS = new ArrayList<>();
		vorSQLDatenSpeichern = new ArrayList<>();
		nachSQLDatenSpeichern = new ArrayList<>();
		stapelGedreht = new ArrayList<>();
		stapel = new ArrayList<>();
		transformatorBatchErhalten = new ArrayList<>();
		EndZeit = 0;
		batchInBearbeitung = false;
	}

	public String zeitUmwandeln(long zeit) {
		String nanosekunden = "";
		for(int i=5;i>0;i--){
			if(zeit%1000000 < Math.pow(10, i)){
				nanosekunden += "0";
			}
		}
		nanosekunden += String.valueOf(zeit % 1000000);
		
		String milisekunden = "";
		if (((zeit / (long) 1000000) % 1000) < 100) {
			milisekunden = "0";
			if (((zeit / (long) 1000000) % 1000) < 10) {
				milisekunden += "0";
			}
		}
		milisekunden += String.valueOf((zeit / (long) 1000000) % 1000);
		
		String sekunden="";
		if (((zeit / ((long) 1000 * (long) 1000000))) % 60 < 10) {
			sekunden = "0";
		}
		sekunden += String.valueOf(((zeit / ((long) 1000 * (long) 1000000))) % 60);
		
		String minuten="";
		if (((zeit / ((long) 1000 * (long) 1000000 * (long) 60)) % 60) < 10) {
			minuten += "0";
		}
		minuten += String.valueOf(((zeit / ((long) 1000 * (long) 1000000 * (long) 60)) % 60));
		
		String stunden="";
		if ((((zeit / ((long) 1000 * (long) 1000000 * (long) 60 * (long) 60))) % 24) < 10) {
			stunden += "0";
		}
		stunden = String.valueOf((((zeit / ((long) 1000 * (long) 1000000 * (long) 60 * (long) 60))) % 24));
		
		String tage = String.valueOf((((zeit / ((long) 1000 * (long) 1000000 * (long) 60 * (long) 60 * (long) 24))) % 30));
		String zeitString = tage + ":" + stunden + ":" + minuten + ":" + sekunden + ":" + milisekunden + ":"+ nanosekunden;

		return zeitString;
	}
	


	public String timeToString() {
		long sqlLadenDurchschnitt = 0;
		long verarbeitenDurchschnitt = 0;
		long umdrehenMSDurchschnitt = 0;
		long verarbeitenDurchschnittNachMS = 0;
		long sqlSpeichernDurchschnitt = 0;
		long sqlLadenGesamt = 0;
		long verarbeitenGesamt = 0;
		long umdrehenMSGesamt = 0;
		long verarbeitenGesamtNachMS = 0;
		long sqlSpeichernGesamt = 0;
		double durchschnittTausend = 0;
		String s = "<table><tbody>";

		if (nachSQLDatenHolen.size() != 0) {
			for (int i = 0; i < nachSQLDatenHolen.size(); i++) {
				sqlLadenGesamt += nachSQLDatenHolen.get(i) - vorSQLDatenHolen.get(i);
			}
			sqlLadenDurchschnitt = sqlLadenGesamt / nachSQLDatenHolen.size();
		}
		if (nachTransformator.size() != 0) {
			for (int i = 0; i < nachTransformator.size(); i++) {
				verarbeitenGesamt += nachTransformator.get(i) - vorTransformator.get(i);
			}
			verarbeitenDurchschnitt = verarbeitenGesamt / nachTransformator.size();
		}
		if (nachUmdrehenMS.size() != 0) {
			for (int i = 0; i < nachUmdrehenMS.size(); i++) {
				umdrehenMSGesamt += nachUmdrehenMS.get(i) - vorUmdrehenMS.get(i);
			}
			umdrehenMSDurchschnitt = umdrehenMSGesamt / nachUmdrehenMS.size();
		}
		if (nachTransformatorMS.size() != 0) {
			for (int i = 0; i < nachTransformatorMS.size(); i++) {
				verarbeitenGesamtNachMS += nachTransformatorMS.get(i) - vorTransformatorMS.get(i);
			}
			verarbeitenDurchschnittNachMS = verarbeitenGesamtNachMS / nachTransformatorMS.size();
		}
		if (nachSQLDatenSpeichern.size() != 0) {
			for (int i = 0; i < nachSQLDatenSpeichern.size(); i++) {
				sqlSpeichernGesamt += nachSQLDatenSpeichern.get(i) - vorSQLDatenSpeichern.get(i);
			}
			if(msUmdrehenGerade!=0){
				durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / ((double) msUmdrehenGerade / 1000.0);
			}else{
				if (gerade != 0) {
					durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / ((double) gerade / 1000.0);
				} else {
					durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / (((double) bis - (double) von) / 1000.0);
				}
			}
			sqlSpeichernDurchschnitt = sqlSpeichernGesamt / nachSQLDatenSpeichern.size();
		}
		long uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator = 0;
		long uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS = 0;
		long uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator = 0;
		long uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern = 0;

		if (transformatorBatchErhalten.size() != 0) {
			for (int i = 0; i < transformatorBatchErhalten.size(); i++) {
				if(nachSQLDatenHolen.get(i)==null){
					break;
				}
				uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator += transformatorBatchErhalten.get(i)
						- nachSQLDatenHolen.get(i);
			}
			uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator = uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator
					/ transformatorBatchErhalten.size();
		}
		if (vorUmdrehenMS.size() != 0) {
			for (int i = 0; i < vorUmdrehenMS.size(); i++) {
				uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS += vorUmdrehenMS.get(i)
						- nachTransformator.get(i);
			}
			uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS = uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS
					/ vorUmdrehenMS.size();
		}
		if (vorTransformatorMS.size() != 0) {
			for (int i = 0; i < vorTransformatorMS.size(); i++) {
				if(nachUmdrehenMS.get(i)==null){
					break;
				}
				uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator += vorTransformatorMS.get(i) - nachUmdrehenMS.get(i);
			}
			uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator = uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator
					/ vorTransformatorMS.size();
		}
		if (vorSQLDatenSpeichern.size() != 0) {
			for (int i = 0; i < vorSQLDatenSpeichern.size(); i++) {
				if (transformatorMS) {
					uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern += vorSQLDatenSpeichern
							.get(i) - nachTransformatorMS.get(i);
				} else {
					uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern += vorSQLDatenSpeichern
							.get(i) - nachTransformator.get(i);
				}
			}
			uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern = uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern
					/ vorSQLDatenSpeichern.size();
		}

		if (sqlLadenGesamt != 0) {
			s += "<tr><td>Durchschnittszeit SQL Datenanfrage: </td><td> " + zeitUmwandeln(sqlLadenDurchschnitt)
					+ " (t:h:m:s:ms:ns)";
			s += "</td></tr><tr><td>Gesamtzeit SQL Datenanfrage: </td><td>"
					+ zeitUmwandeln(sqlLadenDurchschnitt * nachSQLDatenHolen.size()) + " <br></td></tr>\n";
		} else {
			s += "<tr><td>Durchschnittszeit SQL Datenanfrage: </td><td>  Lade Datensätze...<br></td></tr>\n";
		}
		if (!transformatorMS) {
			if (verarbeitenGesamt != 0) {
				s += "<tr><td>Durchschnittszeit Transformator: </td><td> " + zeitUmwandeln(verarbeitenDurchschnitt);
				s += "</td></tr><tr><td>Gesamtzeit Transformator: </td><td>"
						+ zeitUmwandeln(verarbeitenDurchschnitt * nachTransformator.size()) + "<br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit UmdrehenMS: </td><td> ----------";
				s += "</td></tr><tr><td>Gesamtzeit UmdrehenMS: </td><td> ---------- <br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit Trans.weiterreichen: </td><td> ----------";
				s += "</td></tr><tr><td>Gesamtzeit Trans.weiterreichen: </td><td> ---------- <br></td></tr>\n";
			} else {
				s += "<tr><td>Durchschnittszeit Transformator: </td><td>  Verarbeite Datensätze...<br></td></tr>\n";
			}
		} else {
			if (umdrehenMSGesamt != 0) {
				s += "<tr><td>Durchschnittszeit Transformator: </td><td> " + zeitUmwandeln(verarbeitenDurchschnitt);
				s += "</td></tr><tr><td>Gesamtzeit Transformator: </td><td>"
						+ zeitUmwandeln(verarbeitenDurchschnitt * nachTransformator.size()) + "<br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit UmdrehenMS: </td><td> " + zeitUmwandeln(umdrehenMSDurchschnitt);
				s += "</td></tr><tr><td>Gesamtzeit UmdrehenMS: </td><td>"
						+ zeitUmwandeln(umdrehenMSDurchschnitt * nachUmdrehenMS.size()) + "<br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit Trans.weiterreichen: </td><td> "
						+ zeitUmwandeln(verarbeitenDurchschnittNachMS);
				s += "</td></tr><tr><td>Gesamtzeit Trans.weiterreichen: </td><td>"
						+ zeitUmwandeln(verarbeitenDurchschnittNachMS * nachTransformatorMS.size())
						+ "<br></td></tr>\n";
			} else {
				s += "<tr><td>Durchschnittszeit UmdrehenMS: </td><td>  Verarbeite Datensätze...<br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit Trans.weiterreichen: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
		}
		if (sqlSpeichernGesamt != 0) {
			s += "<tr><td>Durchschnittszeit SQL speichern: </td><td> " + zeitUmwandeln(sqlSpeichernDurchschnitt);
			s += "</td></tr><tr><td>Gesamtzeit SQL speichern: </td><td>"
					+ zeitUmwandeln(sqlSpeichernDurchschnitt * nachSQLDatenSpeichern.size()) + "<br></td></tr>\n";
		} else {
			s += "<tr><td>Durchschnittszeit SQL speichern: </td><td>  Speichere Datensätze...<br></td></tr>\n";
		}

		if (durchschnittTausend != 0) {
			s += "<tr><td><br>Durchschnittzeit für 1000 Datensätze: </td><td><br>"
					+ zeitUmwandeln((long) durchschnittTausend) + "</td></tr>";
		} else {
			s += "<tr><td><br>Durchschnittzeit für 1000 Datensätze:</td><td> wird ermittelt...</td></tr>";
		}

		if (uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator != 0) {
			s += "<tr><td>Durchschnittszeit senden SQL -> Transformator : </td><td> "
					+ zeitUmwandeln(uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator);
			s += "</td></tr><tr><td>Gesamtzeit senden SQL -> Transformator: </td><td>" + zeitUmwandeln(
					uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator * vorTransformator.size())
					+ "<br></td></tr>\n";
		} else {
			s += "<tr><td>Durchschnittszeit senden SQL -> Transformator: </td><td>  Lade Datensätze...<br></td></tr>\n";
		}
		if (!transformatorMS) {
			s += "<tr><td>Durchschnittszeit senden Transformator -> UmdrehenMS : </td><td> ----------";
			s += "</td></tr><tr><td>Gesamtzeit senden Transformator -> UmdrehenMS: </td><td> ---------- <br></td></tr>\n";
			s += "<tr><td>Durchschnittszeit senden Transformator -> UmdrehenMS: </td><td>  ----------<br></td></tr>\n";
		} else {
			if (uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS != 0) {
				s += "<tr><td>Durchschnittszeit senden Transformator -> UmdrehenMS : </td><td> "
						+ zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS);
				s += "</td></tr><tr><td>Gesamtzeit senden Transformator -> UmdrehenMS: </td><td>"
						+ zeitUmwandeln(
								uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS * vorUmdrehenMS.size())
						+ "<br></td></tr>\n";
			} else {
				s += "<tr><td>Durchschnittszeit senden Transformator -> UmdrehenMS: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
			if (uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator != 0) {
				s += "<tr><td>Durchschnittszeit senden UmdrehenMS -> Transformator : </td><td> "
						+ zeitUmwandeln(uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator);
				s += "</td></tr><tr><td>Gesamtzeit senden UmdrehenMS -> Transformator: </td><td>"
						+ zeitUmwandeln(
								uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator * vorTransformatorMS.size())
						+ "<br></td></tr>\n";
			} else {
				s += "<tr><td>Durchschnittszeit senden UmdrehenMS -> Transformator: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
		}
		if (uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern != 0) {
			s += "<tr><td>Durchschnittszeit senden Transformator -> SQL : </td><td> "
					+ zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern);
			s += "</td></tr><tr><td>Gesamtzeit senden Transformator -> SQL: </td><td>"
					+ zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern
							* vorSQLDatenSpeichern.size())
					+ "<br></td></tr>\n";
		} else {
			s += "<tr><td>Durchschnittszeit senden Transformator -> SQL: </td><td>  Lade Datensätze...<br></td></tr>\n";
		}

		s += "</table></tbody><br>****************************************************************************<br><br>\n\n";
		return s;
	}
	
	public String timeToStringCSV() {
		long sqlLadenDurchschnitt = 0;
		long verarbeitenDurchschnitt = 0;
		long umdrehenMSDurchschnitt = 0;
		long verarbeitenDurchschnittNachMS = 0;
		long sqlSpeichernDurchschnitt = 0;
		long sqlLadenGesamt = 0;
		long verarbeitenGesamt = 0;
		long umdrehenMSGesamt = 0;
		long verarbeitenGesamtNachMS = 0;
		long sqlSpeichernGesamt = 0;
		double durchschnittTausend = 0;
		String s = "";

		if (nachSQLDatenHolen.size() != 0) {
			for (int i = 0; i < nachSQLDatenHolen.size(); i++) {
				sqlLadenGesamt += nachSQLDatenHolen.get(i) - vorSQLDatenHolen.get(i);
			}
			sqlLadenDurchschnitt = sqlLadenGesamt / nachSQLDatenHolen.size();
		}
		if (nachTransformator.size() != 0) {
			for (int i = 0; i < nachTransformator.size(); i++) {
				verarbeitenGesamt += nachTransformator.get(i) - vorTransformator.get(i);
			}
			verarbeitenDurchschnitt = verarbeitenGesamt / nachTransformator.size();
		}
		if (nachUmdrehenMS.size() != 0) {
			for (int i = 0; i < nachUmdrehenMS.size(); i++) {
				umdrehenMSGesamt += nachUmdrehenMS.get(i) - vorUmdrehenMS.get(i);
			}
			umdrehenMSDurchschnitt = umdrehenMSGesamt / nachUmdrehenMS.size();
		}
		if (nachTransformatorMS.size() != 0) {
			for (int i = 0; i < nachTransformatorMS.size(); i++) {
				verarbeitenGesamtNachMS += nachTransformatorMS.get(i) - vorTransformatorMS.get(i);
			}
			verarbeitenDurchschnittNachMS = verarbeitenGesamtNachMS / nachTransformatorMS.size();
		}
		if (nachSQLDatenSpeichern.size() != 0) {
			for (int i = 0; i < nachSQLDatenSpeichern.size(); i++) {
				sqlSpeichernGesamt += nachSQLDatenSpeichern.get(i) - vorSQLDatenSpeichern.get(i);
			}
			if(msUmdrehenGerade!=0){
				durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / ((double) msUmdrehenGerade / 1000.0);
			}else{
				if (gerade != 0) {
					durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / ((double) gerade / 1000.0);
				} else {
					durchschnittTausend = (((double) nachSQLDatenSpeichern.get(nachSQLDatenSpeichern.size() - 1)) - (double) vorSQLDatenHolen.get(0)) / (((double) bis - (double) von) / 1000.0);
				}
			}
			sqlSpeichernDurchschnitt = sqlSpeichernGesamt / nachSQLDatenSpeichern.size();
		}
		long uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator = 0;
		long uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS = 0;
		long uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator = 0;
		long uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern = 0;

		if (transformatorBatchErhalten.size() != 0) {
			for (int i = 0; i < transformatorBatchErhalten.size(); i++) {
				if(nachSQLDatenHolen.get(i)==null){
					break;
				}
				uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator += transformatorBatchErhalten.get(i)
						- nachSQLDatenHolen.get(i);
			}
			uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator = uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator
					/ transformatorBatchErhalten.size();
		}
		if (vorUmdrehenMS.size() != 0) {
			for (int i = 0; i < vorUmdrehenMS.size(); i++) {
				uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS += vorUmdrehenMS.get(i)
						- nachTransformator.get(i);
			}
			uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS = uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS
					/ vorUmdrehenMS.size();
		}
		if (vorTransformatorMS.size() != 0) {
			for (int i = 0; i < vorTransformatorMS.size(); i++) {
				if(nachUmdrehenMS.get(i)==null){
					break;
				}
				uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator += vorTransformatorMS.get(i) - nachUmdrehenMS.get(i);
			}
			uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator = uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator
					/ vorTransformatorMS.size();
		}
		if (vorSQLDatenSpeichern.size() != 0) {
			for (int i = 0; i < vorSQLDatenSpeichern.size(); i++) {
				if (transformatorMS) {
					uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern += vorSQLDatenSpeichern
							.get(i) - nachTransformatorMS.get(i);
				} else {
					uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern += vorSQLDatenSpeichern
							.get(i) - nachTransformator.get(i);
				}
			}
			uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern = uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern
					/ vorSQLDatenSpeichern.size();
		}

		if (sqlLadenGesamt != 0) {
			s += ";" + zeitUmwandeln(sqlLadenDurchschnitt);
			s += ";" + zeitUmwandeln(sqlLadenDurchschnitt * nachSQLDatenHolen.size()) ;
		} else {
			s += ";";
		}
		if (!transformatorMS) {
			if (verarbeitenGesamt != 0) {
				s += ";" + zeitUmwandeln(verarbeitenDurchschnitt);
				s += ";" + zeitUmwandeln(verarbeitenDurchschnitt * nachTransformator.size());
				s += "0;";
				s += "0;";
				s += "0;";
			} else {
				s += "<tr><td>Durchschnittszeit Transformator: </td><td>  Verarbeite Datensätze...<br></td></tr>\n";
			}
		} else {
			if (umdrehenMSGesamt != 0) {
				s += ";" + zeitUmwandeln(verarbeitenDurchschnitt);
				s += ";" + zeitUmwandeln(verarbeitenDurchschnitt * nachTransformator.size());
				s += ";" + zeitUmwandeln(umdrehenMSDurchschnitt);
				s += ";" + zeitUmwandeln(umdrehenMSDurchschnitt * nachUmdrehenMS.size());
				s += ";" + zeitUmwandeln(verarbeitenDurchschnittNachMS);
				s += ";" + zeitUmwandeln(verarbeitenDurchschnittNachMS * nachTransformatorMS.size());
						
			} else {
				s += "<tr><td>Durchschnittszeit UmdrehenMS: </td><td>  Verarbeite Datensätze...<br></td></tr>\n";
				s += "<tr><td>Durchschnittszeit Trans.weiterreichen: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
		}
		if (sqlSpeichernGesamt != 0) {
			s += ";" + zeitUmwandeln(sqlSpeichernDurchschnitt);
			s += ";" + zeitUmwandeln(sqlSpeichernDurchschnitt * nachSQLDatenSpeichern.size());
		} else {
			s += "<tr><td>Durchschnittszeit SQL speichern: </td><td>  Speichere Datensätze...<br></td></tr>\n";
		}

		if (durchschnittTausend != 0) {
			s += ";" + zeitUmwandeln((long) durchschnittTausend);
		} else {
			s += "<tr><td><br>Durchschnittzeit für 1000 Datensätze:</td><td> wird ermittelt...</td></tr>";
		}

		if (uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator != 0) {
			s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator);
			s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonPersistenzDatenHolenZuTransformator * vorTransformator.size());
		} else {
			s += "<tr><td>Durchschnittszeit senden SQL -> Transformator: </td><td>  Lade Datensätze...<br></td></tr>\n";
		}
		if (!transformatorMS) {
			s += "0;";
			s += "0;";
			s += "0;";
		} else {
			if (uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS != 0) {
				s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS);
				s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuUmdrehenMS * vorUmdrehenMS.size());
			} else {
				s += "<tr><td>Durchschnittszeit senden Transformator -> UmdrehenMS: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
			if (uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator != 0) {
				s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator);
				s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonUmdrehenMSZuTransformator * vorTransformatorMS.size());
			} else {
				s += "<tr><td>Durchschnittszeit senden UmdrehenMS -> Transformator: </td><td>  Lade Datensätze...<br></td></tr>\n";
			}
		}
		if (uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern != 0) {
			s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern);
			s += ";" + zeitUmwandeln(uebertragungszeitDurchschnittVonTransformatorZuPersistenzDatenSpeichern* vorSQLDatenSpeichern.size());
		} else {
			s += "<tr><td>Durchschnittszeit senden Transformator -> SQL: </td><td>  Lade Datensätze...<br></td></tr>\n";
		}
		return s;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public ArrayList<String> getStapel() {
		return stapel;
	}

	public void setStapel(ArrayList<String> stapel) {
		this.stapel = stapel;
	}

	public int getProtokoll() {
		return protokoll;
	}

	public void setProtokoll(int protokoll) {
		this.protokoll = protokoll;
	}

	public double getDurchschnittszeitEinzeln() {
		return durchschnittszeitEinzeln;
	}

	public void setDurchschnittszeitEinzeln(double durchschnittszeitEinzeln) {
		this.durchschnittszeitEinzeln = durchschnittszeitEinzeln;
	}

	public int getParameter() {
		return parameter;
	}

	public void setParameter(int parameter) {
		this.parameter = parameter;
	}

	public int getGerade() {
		return gerade;
	}

	public void setGerade(int gerade) {
		this.gerade = gerade;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVon() {
		return von;
	}

	public void setVon(int von) {
		this.von = von;
	}

	public int getBis() {
		return bis;
	}

	public void setBis(int bis) {
		this.bis = bis;
	}

	public long getStartZeit() {
		return StartZeit;
	}

	public void setStartZeit(long startZeit) {
		StartZeit = startZeit;
	}

	public boolean isAlsStapel() {
		return alsStapel;
	}

	public void setAlsStapel(boolean alsStapel) {
		this.alsStapel = alsStapel;
	}

	public int getSqlGerade() {
		return sqlGerade;
	}

	public void setSqlGerade(int sqlGerade) {
		this.sqlGerade = sqlGerade;
	}

	public boolean isTransformatorMS() {
		return transformatorMS;
	}

	public void setTransformatorMS(boolean transformatorMS) {
		this.transformatorMS = transformatorMS;
	}

	public int getMsUmdrehenGerade() {
		return msUmdrehenGerade;
	}

	public void setMsUmdrehenGerade(int msUmdrehenGerade) {
		this.msUmdrehenGerade = msUmdrehenGerade;
	}

	public long getEndZeit() {
		return EndZeit;
	}

	public void setEndZeit(long endZeit) {
		EndZeit = endZeit;
	}

	public int getStapelgroesse() {
		return stapelgroesse;
	}

	public void setStapelgroesse(int stapelgroesse) {
		this.stapelgroesse = stapelgroesse;
	}

	public boolean isBatchInBearbeitung() {
		return batchInBearbeitung;
	}

	public void setBatchInBearbeitung(boolean batchInBearbeitung) {
		this.batchInBearbeitung = batchInBearbeitung;
	}

	public int getSqlGeradeSpeichern() {
		return sqlGeradeSpeichern;
	}

	public void setSqlGeradeSpeichern(int sqlGeradeSpeichern) {
		this.sqlGeradeSpeichern = sqlGeradeSpeichern;
	}

	@Override
	public int compareTo(Fortschritt arg0) {
		if (this.id < arg0.id) {
			return 1;
		}
		return 0;
	}

	public void setVorSQLDatenHolen(ArrayList<Long> vorSQLDatenHolen) {
		this.vorSQLDatenHolen = vorSQLDatenHolen;
	}

	public void setNachSQLDatenHolen(ArrayList<Long> nachSQLDatenHolen) {
		this.nachSQLDatenHolen = nachSQLDatenHolen;
	}

	public void setVorTransformator(ArrayList<Long> vorTransformator) {
		this.vorTransformator = vorTransformator;
	}

	public void setNachTransformator(ArrayList<Long> nachTransformator) {
		this.nachTransformator = nachTransformator;
	}

	public void setVorUmdrehenMS(ArrayList<Long> vorUmdrehenMS) {
		this.vorUmdrehenMS = vorUmdrehenMS;
	}

	public void setNachUmdrehenMS(ArrayList<Long> nachUmdrehenMS) {
		this.nachUmdrehenMS = nachUmdrehenMS;
	}

	public void setVorSQLDatenSpeichern(ArrayList<Long> vorSQLDatenSpeichern) {
		this.vorSQLDatenSpeichern = vorSQLDatenSpeichern;
	}

	public void setNachSQLDatenSpeichern(ArrayList<Long> nachSQLDatenSpeichern) {
		this.nachSQLDatenSpeichern = nachSQLDatenSpeichern;
	}

	public ArrayList<Long> getVorSQLDatenHolen() {
		return vorSQLDatenHolen;
	}

	public ArrayList<Long> getNachSQLDatenHolen() {
		return nachSQLDatenHolen;
	}

	public ArrayList<Long> getVorTransformator() {
		return vorTransformator;
	}

	public ArrayList<Long> getNachTransformator() {
		return nachTransformator;
	}

	public ArrayList<Long> getVorUmdrehenMS() {
		return vorUmdrehenMS;
	}

	public ArrayList<Long> getNachUmdrehenMS() {
		return nachUmdrehenMS;
	}

	public ArrayList<Long> getVorSQLDatenSpeichern() {
		return vorSQLDatenSpeichern;
	}

	public ArrayList<Long> getNachSQLDatenSpeichern() {
		return nachSQLDatenSpeichern;
	}

	public ArrayList<Long> getVorTransformatorMS() {
		return vorTransformatorMS;
	}

	public void setVorTransformatorMS(ArrayList<Long> vorTransformatorMS) {
		this.vorTransformatorMS = vorTransformatorMS;
	}

	public ArrayList<Long> getNachTransformatorMS() {
		return nachTransformatorMS;
	}

	public void setNachTransformatorMS(ArrayList<Long> nachTransformatorMS) {
		this.nachTransformatorMS = nachTransformatorMS;
	}

	public ArrayList<String> getStapelGedreht() {
		return stapelGedreht;
	}

	public void setStapelGedreht(ArrayList<String> stapelGedreht) {
		this.stapelGedreht = stapelGedreht;
	}

	public boolean isBatchAmSpeichern() {
		return batchAmSpeichern;
	}

	public void setBatchAmSpeichern(boolean batchAmSpeichern) {
		this.batchAmSpeichern = batchAmSpeichern;
	}

	public ArrayList<Long> getTransformatorBatchErhalten() {
		return transformatorBatchErhalten;
	}

	public void setTransformatorBatchErhalten(ArrayList<Long> transformatorBatchErhalten) {
		this.transformatorBatchErhalten = transformatorBatchErhalten;
	}

	public void setFortschritt(MSBenchmark.microServices.Fortschritt f) {
		this.alsStapel = f.isAlsStapel();
		this.bis = f.getBis();
		this.durchschnittszeitEinzeln = f.getDurchschnittszeitEinzeln();
		this.EndZeit = f.getEndZeit();
		this.gerade = f.getGerade();
		this.id = f.getId();
		this.msUmdrehenGerade = f.getMsUmdrehenGerade();
		this.parameter = f.getParameter();
		this.protokoll = f.getProtokoll();
		this.sqlGerade = f.getSqlGerade();
		this.stapel = f.getStapel();
		this.stapelGedreht = f.getStapelGedreht();
		this.StartZeit = f.getStartZeit();
		this.titel = f.getTitel();
		this.transformatorMS = f.isTransformatorMS();
		this.von = f.getVon();
		this.batchInBearbeitung = f.isBatchInBearbeitung();
		this.batchAmSpeichern = f.isBatchAmSpeichern();
		this.stapelgroesse = f.getStapelgroesse();

		vorSQLDatenHolen = f.getVorSQLDatenHolen();
		nachSQLDatenHolen = f.getNachSQLDatenHolen();
		vorTransformator = f.getVorTransformator();
		nachTransformator = f.getNachTransformator();
		vorUmdrehenMS = f.getVorUmdrehenMS();
		nachUmdrehenMS = f.getNachUmdrehenMS();
		vorSQLDatenSpeichern = f.getVorSQLDatenSpeichern();
		nachSQLDatenSpeichern = f.getNachSQLDatenSpeichern();
	}

	public void setFortschritt(FortschrittRequest f) {
		this.alsStapel = f.getAlsStapel();
		this.bis = f.getBis();
		this.durchschnittszeitEinzeln = f.getDurchschnittszeitEinzeln();
		this.EndZeit = f.getEndZeit();
		this.gerade = f.getGerade();
		this.id = f.getId();
		this.msUmdrehenGerade = f.getMsUmdrehenGerade();
		this.parameter = f.getParameter();
		this.protokoll = f.getProtokoll();
		this.sqlGerade = f.getSqlGerade();
		if (f.getStapelList() == null) {
			this.stapel = new ArrayList<>();
		} else {
			this.stapel = new ArrayList<>();
			for (String s : f.getStapelList()) {
				this.stapel.add(s);
			}
		}

		if (f.getStapelGedrehtList() == null) {
			this.stapelGedreht = new ArrayList<>();
		} else {
			this.stapelGedreht = new ArrayList<>();
			for (String s : f.getStapelGedrehtList()) {
				this.stapelGedreht.add(s);
			}
		}

		this.StartZeit = f.getStartZeit();
		this.titel = f.getTitel();
		this.transformatorMS = f.getTransformatorMS();
		this.von = f.getVon();
		this.batchInBearbeitung = f.getBatchInBearbeitung();
		this.batchAmSpeichern = f.getBatchAmSpeichern();
		this.stapelgroesse = f.getStapelgroesse();

		vorSQLDatenHolen = new ArrayList<>();
		nachSQLDatenHolen = new ArrayList<>();
		vorTransformator = new ArrayList<>();
		nachTransformator = new ArrayList<>();
		vorUmdrehenMS = new ArrayList<>();
		nachUmdrehenMS = new ArrayList<>();
		vorSQLDatenSpeichern = new ArrayList<>();
		nachSQLDatenSpeichern = new ArrayList<>();

		for (long s : f.getVorSQLDatenSpeichernList()) {
			this.vorSQLDatenHolen.add(s);
		}
		for (long s : f.getNachSQLDatenSpeichernList()) {
			this.nachSQLDatenHolen.add(s);
		}
		for (long s : f.getVorTransformatorMSList()) {
			this.vorTransformator.add(s);
		}
		for (long s : f.getNachTransformatorMSList()) {
			this.nachTransformator.add(s);
		}
		for (long s : f.getVorUmdrehenMSList()) {
			this.vorUmdrehenMS.add(s);
		}
		for (long s : f.getNachUmdrehenMSList()) {
			this.nachUmdrehenMS.add(s);
		}
		for (long s : f.getVorSQLDatenSpeichernList()) {
			this.vorSQLDatenSpeichern.add(s);
		}
		for (long s : f.getNachSQLDatenSpeichernList()) {
			this.nachSQLDatenSpeichern.add(s);
		}

	}

	FortschrittRequest getFortschrittBuilder() {
		return FortschrittRequest.newBuilder().setAlsStapel(this.alsStapel).setBis(this.getBis())
				.setDurchschnittszeitEinzeln(this.getDurchschnittszeitEinzeln()).setGerade(this.getGerade())
				.setId(this.getId()).setMsUmdrehenGerade(this.getMsUmdrehenGerade()).setParameter(this.getParameter())
				.setProtokoll(this.getProtokoll()).setSqlGerade(this.getSqlGerade()).addAllStapel(this.getStapel())
				.addAllStapelGedreht(this.getStapelGedreht()).setStartZeit(this.getStartZeit())
				.setTitel(this.getTitel()).setTransformatorMS(this.isTransformatorMS()).setVon(this.getVon())
				.setBatchInBearbeitung(this.isBatchInBearbeitung()).setBatchAmSpeichern(this.isBatchAmSpeichern())
				.setStapelgroesse(this.getStapelgroesse()).addAllNachSQLDatenHolen(this.getVorSQLDatenHolen())
				.addAllNachSQLDatenHolen(this.getNachSQLDatenHolen())
				.addAllNachTransformator(this.getVorTransformator())
				.addAllNachTransformator(this.getNachTransformator()).addAllNachUmdrehenMS(this.getVorUmdrehenMS())
				.addAllNachUmdrehenMS(this.getNachUmdrehenMS())
				.addAllNachSQLDatenSpeichern(this.getVorSQLDatenSpeichern())
				.addAllNachSQLDatenSpeichern(this.getNachSQLDatenSpeichern()).build();
	}

}
