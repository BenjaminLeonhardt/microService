
package MSBenchmark.microServices.SOAPStubs.umdrehenMS;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the msbenchmark.microservices package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Fortschritt_QNAME = new QName("http://microServices.MSBenchmark/", "fortschritt");
    private final static QName _AuftragAbschliessenUMS_QNAME = new QName("http://microServices.MSBenchmark/", "auftragAbschliessenUMS");
    private final static QName _AuftragAbschliessenUMSResponse_QNAME = new QName("http://microServices.MSBenchmark/", "auftragAbschliessenUMSResponse");
    private final static QName _SetDatenStapelUMSResponse_QNAME = new QName("http://microServices.MSBenchmark/", "setDatenStapelUMSResponse");
    private final static QName _SetDatenStapelUMS_QNAME = new QName("http://microServices.MSBenchmark/", "setDatenStapelUMS");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: msbenchmark.microservices
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Fortschritt }
     * 
     */
    public Fortschritt createFortschritt() {
        return new Fortschritt();
    }

    /**
     * Create an instance of {@link SetDatenStapelUMSResponse }
     * 
     */
    public SetDatenStapelUMSResponse createSetDatenStapelUMSResponse() {
        return new SetDatenStapelUMSResponse();
    }

    /**
     * Create an instance of {@link SetDatenStapelUMS }
     * 
     */
    public SetDatenStapelUMS createSetDatenStapelUMS() {
        return new SetDatenStapelUMS();
    }

    /**
     * Create an instance of {@link AuftragAbschliessenUMS }
     * 
     */
    public AuftragAbschliessenUMS createAuftragAbschliessenUMS() {
        return new AuftragAbschliessenUMS();
    }

    /**
     * Create an instance of {@link AuftragAbschliessenUMSResponse }
     * 
     */
    public AuftragAbschliessenUMSResponse createAuftragAbschliessenUMSResponse() {
        return new AuftragAbschliessenUMSResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Fortschritt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "fortschritt")
    public JAXBElement<Fortschritt> createFortschritt(Fortschritt value) {
        return new JAXBElement<Fortschritt>(_Fortschritt_QNAME, Fortschritt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuftragAbschliessenUMS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "auftragAbschliessenUMS")
    public JAXBElement<AuftragAbschliessenUMS> createAuftragAbschliessenUMS(AuftragAbschliessenUMS value) {
        return new JAXBElement<AuftragAbschliessenUMS>(_AuftragAbschliessenUMS_QNAME, AuftragAbschliessenUMS.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuftragAbschliessenUMSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "auftragAbschliessenUMSResponse")
    public JAXBElement<AuftragAbschliessenUMSResponse> createAuftragAbschliessenUMSResponse(AuftragAbschliessenUMSResponse value) {
        return new JAXBElement<AuftragAbschliessenUMSResponse>(_AuftragAbschliessenUMSResponse_QNAME, AuftragAbschliessenUMSResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDatenStapelUMSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "setDatenStapelUMSResponse")
    public JAXBElement<SetDatenStapelUMSResponse> createSetDatenStapelUMSResponse(SetDatenStapelUMSResponse value) {
        return new JAXBElement<SetDatenStapelUMSResponse>(_SetDatenStapelUMSResponse_QNAME, SetDatenStapelUMSResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDatenStapelUMS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "setDatenStapelUMS")
    public JAXBElement<SetDatenStapelUMS> createSetDatenStapelUMS(SetDatenStapelUMS value) {
        return new JAXBElement<SetDatenStapelUMS>(_SetDatenStapelUMS_QNAME, SetDatenStapelUMS.class, null, value);
    }

}
