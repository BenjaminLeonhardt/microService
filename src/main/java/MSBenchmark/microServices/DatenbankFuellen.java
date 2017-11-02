package MSBenchmark.microServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;


/**
 * @author Benjamin Leonhardt
 *
 * Die Klasse DatenbankFuellen dient zur Erstellung der SQL Datenbank mit Tabellen und Spalten. 
 * Des Weiteren wird die Tabelle Werte mit Zufallswerten gefüllt.
 * 
 * Sie muss nur ein mal zum Initialisieren ausgeführt werden. In der Readme.md ist dies 
 * unter Punkt 2.3.1 Datenbank erstellen beschrieben.
 * 
 */
@Path("/datenbankFuellen")
public class DatenbankFuellen {
	public static final String ipDatenbank 		= "127.0.0.1";
//	public static final String ipDatenbank 		= "10.15.252.205";
	public static final String portDatenbank 	= "5432";
	
	public static int counter=0;
	
	public DatenbankFuellen(){
		
	}

	@GET
	@Path("/fuellen")
	public String fuellen(@QueryParam("dropTable") String dropTable) {
		String ret="Die SQL Datenbank ist nun mit 1.000.000 zufaelligen Strings gefuellt";
		try {
			//Verbindung zur Datenbank herstellen
			Class.forName("org.postgresql.Driver");
			Connection con = DriverManager.getConnection("jdbc:postgresql://"+ipDatenbank+":"+portDatenbank+"/",Persistenz.benutzerSQL,Persistenz.passwortSQL);
			if(con!=null){
				System.out.println("Datenbankverbindung hergestellt!");
			}
			
			//Statementobjekt generieren
			Statement st = con.createStatement();
			String s = "";
			
			if(dropTable.equals("1")){
				st.execute("DROP DATABASE \"PersistenzBA\"");
			}

			//Datenbank erstellen und öffnen
			st.execute("CREATE DATABASE \"PersistenzBA\" WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;");
			con = DriverManager.getConnection("jdbc:postgresql://"+ipDatenbank+":"+portDatenbank+"/PersistenzBA",Persistenz.benutzerSQL,Persistenz.passwortSQL);
			st = con.createStatement();
			
			//Tabellen erstellen
			st.execute("CREATE TABLE werte (id integer NOT NULL, wert character varying(32) NOT NULL, CONSTRAINT werte_pkey PRIMARY KEY (id) )WITH ( OIDS = FALSE );");
			st.execute("ALTER TABLE werte OWNER to postgres;");
			
			st.execute("CREATE TABLE umgedreht (id integer NOT NULL, id_werte integer NOT NULL, werteumgedreht character varying(32) NOT NULL, "
					+ "CONSTRAINT umgedreht_pkey PRIMARY KEY (id), CONSTRAINT fkey_werte FOREIGN KEY (id_werte) REFERENCES public.werte (id) MATCH SIMPLE "
					+ "ON UPDATE NO ACTION ON DELETE NO ACTION) WITH ( OIDS = FALSE);");
			st.execute("ALTER TABLE umgedreht OWNER to postgres;");
			//st.execute("TRUNCATE table werte CASCADE");
			
			//Tabelle Werte mit zufälligen 32 Zeichen langen Strings füllen
			Random gen = new Random();
			int f =0;
			for(int i=0;i<1000000;i++){
				counter=i;
				for(int j =0;j<32;j++){
					f=((gen.nextInt((int)System.currentTimeMillis()&0x7FFFFFFF)%60)+48);
					if(f>57){
						f += 8;
					}
					if(f>90){
						f += 7; 
					}
					char c = (char) f;
					s = s + c;
				}
				st.execute("insert into Werte values (" + i + ", '" + s + "');");
				
				System.out.println("insert into Werte values (" + i + ", " + s + ")");
				s = "";
				
			}
			
		} catch (ClassNotFoundException | SQLException e) {
			if(e.getClass()==SQLException.class){
				e.getMessage();
			}
			e.printStackTrace();
			ret=e.getMessage();
		}
		return ret;
	}
	
	/**
	 * Statusabfrage zum Fortschritt des Füllens der Datenbank
	 * 
	 * @return Counter, wieviele Strings schon in die Datenbank geschrieben wurden.
	 */
	@GET
	@Path("/counter")
	public String counter() {
		return String.valueOf(counter);
	}
	
}
