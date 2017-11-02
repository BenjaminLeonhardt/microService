
package MSBenchmark.microServices.SOAPStubs.transformator;

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

    private final static QName _BatchSpeichernFertigResponse_QNAME = new QName("http://microServices.MSBenchmark/", "batchSpeichernFertigResponse");
    private final static QName _AuftragAbschliessenResponse_QNAME = new QName("http://microServices.MSBenchmark/", "auftragAbschliessenResponse");
    private final static QName _BatchSpeichernFertig_QNAME = new QName("http://microServices.MSBenchmark/", "batchSpeichernFertig");
    private final static QName _AuftragAbschliessen_QNAME = new QName("http://microServices.MSBenchmark/", "auftragAbschliessen");
    private final static QName _BatchWeiterreichenResponse_QNAME = new QName("http://microServices.MSBenchmark/", "batchWeiterreichenResponse");
    private final static QName _BatchVerarbeitung_QNAME = new QName("http://microServices.MSBenchmark/", "batchVerarbeitung");
    private final static QName _BatchWeiterreichen_QNAME = new QName("http://microServices.MSBenchmark/", "batchWeiterreichen");
    private final static QName _BatchVerarbeitungResponse_QNAME = new QName("http://microServices.MSBenchmark/", "batchVerarbeitungResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: msbenchmark.microservices
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AuftragAbschliessen }
     * 
     */
    public AuftragAbschliessen createAuftragAbschliessen() {
        return new AuftragAbschliessen();
    }

    /**
     * Create an instance of {@link BatchVerarbeitung }
     * 
     */
    public BatchVerarbeitung createBatchVerarbeitung() {
        return new BatchVerarbeitung();
    }

    /**
     * Create an instance of {@link BatchWeiterreichen }
     * 
     */
    public BatchWeiterreichen createBatchWeiterreichen() {
        return new BatchWeiterreichen();
    }

    /**
     * Create an instance of {@link BatchVerarbeitungResponse }
     * 
     */
    public BatchVerarbeitungResponse createBatchVerarbeitungResponse() {
        return new BatchVerarbeitungResponse();
    }

    /**
     * Create an instance of {@link BatchWeiterreichenResponse }
     * 
     */
    public BatchWeiterreichenResponse createBatchWeiterreichenResponse() {
        return new BatchWeiterreichenResponse();
    }

    /**
     * Create an instance of {@link BatchSpeichernFertigResponse }
     * 
     */
    public BatchSpeichernFertigResponse createBatchSpeichernFertigResponse() {
        return new BatchSpeichernFertigResponse();
    }

    /**
     * Create an instance of {@link AuftragAbschliessenResponse }
     * 
     */
    public AuftragAbschliessenResponse createAuftragAbschliessenResponse() {
        return new AuftragAbschliessenResponse();
    }

    /**
     * Create an instance of {@link BatchSpeichernFertig }
     * 
     */
    public BatchSpeichernFertig createBatchSpeichernFertig() {
        return new BatchSpeichernFertig();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchSpeichernFertigResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchSpeichernFertigResponse")
    public JAXBElement<BatchSpeichernFertigResponse> createBatchSpeichernFertigResponse(BatchSpeichernFertigResponse value) {
        return new JAXBElement<BatchSpeichernFertigResponse>(_BatchSpeichernFertigResponse_QNAME, BatchSpeichernFertigResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuftragAbschliessenResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "auftragAbschliessenResponse")
    public JAXBElement<AuftragAbschliessenResponse> createAuftragAbschliessenResponse(AuftragAbschliessenResponse value) {
        return new JAXBElement<AuftragAbschliessenResponse>(_AuftragAbschliessenResponse_QNAME, AuftragAbschliessenResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchSpeichernFertig }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchSpeichernFertig")
    public JAXBElement<BatchSpeichernFertig> createBatchSpeichernFertig(BatchSpeichernFertig value) {
        return new JAXBElement<BatchSpeichernFertig>(_BatchSpeichernFertig_QNAME, BatchSpeichernFertig.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuftragAbschliessen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "auftragAbschliessen")
    public JAXBElement<AuftragAbschliessen> createAuftragAbschliessen(AuftragAbschliessen value) {
        return new JAXBElement<AuftragAbschliessen>(_AuftragAbschliessen_QNAME, AuftragAbschliessen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchWeiterreichenResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchWeiterreichenResponse")
    public JAXBElement<BatchWeiterreichenResponse> createBatchWeiterreichenResponse(BatchWeiterreichenResponse value) {
        return new JAXBElement<BatchWeiterreichenResponse>(_BatchWeiterreichenResponse_QNAME, BatchWeiterreichenResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchVerarbeitung }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchVerarbeitung")
    public JAXBElement<BatchVerarbeitung> createBatchVerarbeitung(BatchVerarbeitung value) {
        return new JAXBElement<BatchVerarbeitung>(_BatchVerarbeitung_QNAME, BatchVerarbeitung.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchWeiterreichen }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchWeiterreichen")
    public JAXBElement<BatchWeiterreichen> createBatchWeiterreichen(BatchWeiterreichen value) {
        return new JAXBElement<BatchWeiterreichen>(_BatchWeiterreichen_QNAME, BatchWeiterreichen.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BatchVerarbeitungResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "batchVerarbeitungResponse")
    public JAXBElement<BatchVerarbeitungResponse> createBatchVerarbeitungResponse(BatchVerarbeitungResponse value) {
        return new JAXBElement<BatchVerarbeitungResponse>(_BatchVerarbeitungResponse_QNAME, BatchVerarbeitungResponse.class, null, value);
    }

}
