
package MSBenchmark.microServices.SOAPStubs.transformator;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r fortschritt complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="fortschritt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="alsStapel" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="batchAmSpeichern" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="batchInBearbeitung" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="bis" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="durchschnittszeitEinzeln" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="endZeit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="gerade" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="msUmdrehenGerade" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nachSQLDatenHolen" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nachSQLDatenSpeichern" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nachTransformator" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nachUmdrehenMS" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="protokoll" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sqlGerade" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sqlGeradeSpeichern" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stapel" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stapelGedreht" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stapelgroesse" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="startZeit" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="titel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="transformatorMS" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="von" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vorSQLDatenHolen" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vorSQLDatenSpeichern" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vorTransformator" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vorUmdrehenMS" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fortschritt", propOrder = {
    "alsStapel",
    "batchAmSpeichern",
    "batchInBearbeitung",
    "bis",
    "durchschnittszeitEinzeln",
    "endZeit",
    "gerade",
    "id",
    "msUmdrehenGerade",
    "nachSQLDatenHolen",
    "nachSQLDatenSpeichern",
    "nachTransformator",
    "nachUmdrehenMS",
    "parameter",
    "protokoll",
    "sqlGerade",
    "sqlGeradeSpeichern",
    "stapel",
    "stapelGedreht",
    "stapelgroesse",
    "startZeit",
    "titel",
    "transformatorMS",
    "von",
    "vorSQLDatenHolen",
    "vorSQLDatenSpeichern",
    "vorTransformator",
    "vorUmdrehenMS"
})
public class Fortschritt {

    protected boolean alsStapel;
    protected boolean batchAmSpeichern;
    protected boolean batchInBearbeitung;
    protected int bis;
    protected double durchschnittszeitEinzeln;
    protected long endZeit;
    protected int gerade;
    protected int id;
    protected int msUmdrehenGerade;
    @XmlElement(nillable = true)
    protected List<Long> nachSQLDatenHolen;
    @XmlElement(nillable = true)
    protected List<Long> nachSQLDatenSpeichern;
    @XmlElement(nillable = true)
    protected List<Long> nachTransformator;
    @XmlElement(nillable = true)
    protected List<Long> nachUmdrehenMS;
    protected int parameter;
    protected int protokoll;
    protected int sqlGerade;
    protected int sqlGeradeSpeichern;
    @XmlElement(nillable = true)
    protected List<String> stapel;
    @XmlElement(nillable = true)
    protected List<String> stapelGedreht;
    protected int stapelgroesse;
    protected long startZeit;
    protected String titel;
    protected boolean transformatorMS;
    protected int von;
    @XmlElement(nillable = true)
    protected List<Long> vorSQLDatenHolen;
    @XmlElement(nillable = true)
    protected List<Long> vorSQLDatenSpeichern;
    @XmlElement(nillable = true)
    protected List<Long> vorTransformator;
    @XmlElement(nillable = true)
    protected List<Long> vorUmdrehenMS;

    /**
     * Ruft den Wert der alsStapel-Eigenschaft ab.
     * 
     */
    public boolean isAlsStapel() {
        return alsStapel;
    }

    /**
     * Legt den Wert der alsStapel-Eigenschaft fest.
     * 
     */
    public void setAlsStapel(boolean value) {
        this.alsStapel = value;
    }

    /**
     * Ruft den Wert der batchAmSpeichern-Eigenschaft ab.
     * 
     */
    public boolean isBatchAmSpeichern() {
        return batchAmSpeichern;
    }

    /**
     * Legt den Wert der batchAmSpeichern-Eigenschaft fest.
     * 
     */
    public void setBatchAmSpeichern(boolean value) {
        this.batchAmSpeichern = value;
    }

    /**
     * Ruft den Wert der batchInBearbeitung-Eigenschaft ab.
     * 
     */
    public boolean isBatchInBearbeitung() {
        return batchInBearbeitung;
    }

    /**
     * Legt den Wert der batchInBearbeitung-Eigenschaft fest.
     * 
     */
    public void setBatchInBearbeitung(boolean value) {
        this.batchInBearbeitung = value;
    }

    /**
     * Ruft den Wert der bis-Eigenschaft ab.
     * 
     */
    public int getBis() {
        return bis;
    }

    /**
     * Legt den Wert der bis-Eigenschaft fest.
     * 
     */
    public void setBis(int value) {
        this.bis = value;
    }

    /**
     * Ruft den Wert der durchschnittszeitEinzeln-Eigenschaft ab.
     * 
     */
    public double getDurchschnittszeitEinzeln() {
        return durchschnittszeitEinzeln;
    }

    /**
     * Legt den Wert der durchschnittszeitEinzeln-Eigenschaft fest.
     * 
     */
    public void setDurchschnittszeitEinzeln(double value) {
        this.durchschnittszeitEinzeln = value;
    }

    /**
     * Ruft den Wert der endZeit-Eigenschaft ab.
     * 
     */
    public long getEndZeit() {
        return endZeit;
    }

    /**
     * Legt den Wert der endZeit-Eigenschaft fest.
     * 
     */
    public void setEndZeit(long value) {
        this.endZeit = value;
    }

    /**
     * Ruft den Wert der gerade-Eigenschaft ab.
     * 
     */
    public int getGerade() {
        return gerade;
    }

    /**
     * Legt den Wert der gerade-Eigenschaft fest.
     * 
     */
    public void setGerade(int value) {
        this.gerade = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der msUmdrehenGerade-Eigenschaft ab.
     * 
     */
    public int getMsUmdrehenGerade() {
        return msUmdrehenGerade;
    }

    /**
     * Legt den Wert der msUmdrehenGerade-Eigenschaft fest.
     * 
     */
    public void setMsUmdrehenGerade(int value) {
        this.msUmdrehenGerade = value;
    }

    /**
     * Gets the value of the nachSQLDatenHolen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nachSQLDatenHolen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNachSQLDatenHolen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getNachSQLDatenHolen() {
        if (nachSQLDatenHolen == null) {
            nachSQLDatenHolen = new ArrayList<Long>();
        }
        return this.nachSQLDatenHolen;
    }

    /**
     * Gets the value of the nachSQLDatenSpeichern property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nachSQLDatenSpeichern property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNachSQLDatenSpeichern().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getNachSQLDatenSpeichern() {
        if (nachSQLDatenSpeichern == null) {
            nachSQLDatenSpeichern = new ArrayList<Long>();
        }
        return this.nachSQLDatenSpeichern;
    }

    /**
     * Gets the value of the nachTransformator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nachTransformator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNachTransformator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getNachTransformator() {
        if (nachTransformator == null) {
            nachTransformator = new ArrayList<Long>();
        }
        return this.nachTransformator;
    }

    /**
     * Gets the value of the nachUmdrehenMS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nachUmdrehenMS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNachUmdrehenMS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getNachUmdrehenMS() {
        if (nachUmdrehenMS == null) {
            nachUmdrehenMS = new ArrayList<Long>();
        }
        return this.nachUmdrehenMS;
    }

    /**
     * Ruft den Wert der parameter-Eigenschaft ab.
     * 
     */
    public int getParameter() {
        return parameter;
    }

    /**
     * Legt den Wert der parameter-Eigenschaft fest.
     * 
     */
    public void setParameter(int value) {
        this.parameter = value;
    }

    /**
     * Ruft den Wert der protokoll-Eigenschaft ab.
     * 
     */
    public int getProtokoll() {
        return protokoll;
    }

    /**
     * Legt den Wert der protokoll-Eigenschaft fest.
     * 
     */
    public void setProtokoll(int value) {
        this.protokoll = value;
    }

    /**
     * Ruft den Wert der sqlGerade-Eigenschaft ab.
     * 
     */
    public int getSqlGerade() {
        return sqlGerade;
    }

    /**
     * Legt den Wert der sqlGerade-Eigenschaft fest.
     * 
     */
    public void setSqlGerade(int value) {
        this.sqlGerade = value;
    }

    /**
     * Ruft den Wert der sqlGeradeSpeichern-Eigenschaft ab.
     * 
     */
    public int getSqlGeradeSpeichern() {
        return sqlGeradeSpeichern;
    }

    /**
     * Legt den Wert der sqlGeradeSpeichern-Eigenschaft fest.
     * 
     */
    public void setSqlGeradeSpeichern(int value) {
        this.sqlGeradeSpeichern = value;
    }

    /**
     * Gets the value of the stapel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stapel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStapel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getStapel() {
        if (stapel == null) {
            stapel = new ArrayList<String>();
        }
        return this.stapel;
    }
    
    public void setStapel(ArrayList<String> stapel){
    	this.stapel=stapel;
    }
    
    public void setStapelGedreht(ArrayList<String> stapelGedreht){
    	this.stapelGedreht=stapelGedreht;
    }

    /**
     * Gets the value of the stapelGedreht property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stapelGedreht property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStapelGedreht().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getStapelGedreht() {
        if (stapelGedreht == null) {
            stapelGedreht = new ArrayList<String>();
        }
        return this.stapelGedreht;
    }

    /**
     * Ruft den Wert der stapelgroesse-Eigenschaft ab.
     * 
     */
    public int getStapelgroesse() {
        return stapelgroesse;
    }

    /**
     * Legt den Wert der stapelgroesse-Eigenschaft fest.
     * 
     */
    public void setStapelgroesse(int value) {
        this.stapelgroesse = value;
    }

    /**
     * Ruft den Wert der startZeit-Eigenschaft ab.
     * 
     */
    public long getStartZeit() {
        return startZeit;
    }

    /**
     * Legt den Wert der startZeit-Eigenschaft fest.
     * 
     */
    public void setStartZeit(long value) {
        this.startZeit = value;
    }

    /**
     * Ruft den Wert der titel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Legt den Wert der titel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitel(String value) {
        this.titel = value;
    }

    /**
     * Ruft den Wert der transformatorMS-Eigenschaft ab.
     * 
     */
    public boolean isTransformatorMS() {
        return transformatorMS;
    }

    /**
     * Legt den Wert der transformatorMS-Eigenschaft fest.
     * 
     */
    public void setTransformatorMS(boolean value) {
        this.transformatorMS = value;
    }

    /**
     * Ruft den Wert der von-Eigenschaft ab.
     * 
     */
    public int getVon() {
        return von;
    }

    /**
     * Legt den Wert der von-Eigenschaft fest.
     * 
     */
    public void setVon(int value) {
        this.von = value;
    }

    /**
     * Gets the value of the vorSQLDatenHolen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vorSQLDatenHolen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVorSQLDatenHolen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getVorSQLDatenHolen() {
        if (vorSQLDatenHolen == null) {
            vorSQLDatenHolen = new ArrayList<Long>();
        }
        return this.vorSQLDatenHolen;
    }

    /**
     * Gets the value of the vorSQLDatenSpeichern property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vorSQLDatenSpeichern property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVorSQLDatenSpeichern().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getVorSQLDatenSpeichern() {
        if (vorSQLDatenSpeichern == null) {
            vorSQLDatenSpeichern = new ArrayList<Long>();
        }
        return this.vorSQLDatenSpeichern;
    }

    /**
     * Gets the value of the vorTransformator property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vorTransformator property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVorTransformator().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getVorTransformator() {
        if (vorTransformator == null) {
            vorTransformator = new ArrayList<Long>();
        }
        return this.vorTransformator;
    }

    /**
     * Gets the value of the vorUmdrehenMS property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vorUmdrehenMS property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVorUmdrehenMS().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Long }
     * 
     * 
     */
    public List<Long> getVorUmdrehenMS() {
        if (vorUmdrehenMS == null) {
            vorUmdrehenMS = new ArrayList<Long>();
        }
        return this.vorUmdrehenMS;
    }

    
	public void setFortschritt(MSBenchmark.microServices.Fortschritt f){
    	this.alsStapel=f.isAlsStapel();
    	this.bis=f.getBis();
    	this.durchschnittszeitEinzeln=f.getDurchschnittszeitEinzeln();
    	this.endZeit=f.getEndZeit();
    	this.gerade=f.getGerade();
    	this.id=f.getId();
    	this.msUmdrehenGerade=f.getMsUmdrehenGerade();
    	this.parameter=f.getParameter();
    	this.protokoll=f.getProtokoll();
    	this.sqlGerade=f.getSqlGerade();
    	this.stapel=f.getStapel();
    	this.stapelGedreht=f.getStapelGedreht();
    	this.startZeit=f.getStartZeit();
    	this.titel=f.getTitel();
    	this.transformatorMS=f.isTransformatorMS();
    	this.von=f.getVon();
        this.batchInBearbeitung=f.isBatchInBearbeitung();
        this.batchAmSpeichern=f.isBatchAmSpeichern();
        this.stapelgroesse=f.getStapelgroesse();
        
        vorSQLDatenHolen=f.getVorSQLDatenHolen();
		nachSQLDatenHolen=f.getNachSQLDatenHolen();
		vorTransformator=f.getVorTransformator();
		nachTransformator=f.getNachTransformator();
		vorUmdrehenMS=f.getVorUmdrehenMS();
		nachUmdrehenMS=f.getNachUmdrehenMS();
		vorSQLDatenSpeichern=f.getVorSQLDatenSpeichern();
		nachSQLDatenSpeichern=f.getNachSQLDatenSpeichern();

      
    }
    
}
