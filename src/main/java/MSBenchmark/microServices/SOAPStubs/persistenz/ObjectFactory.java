
package MSBenchmark.microServices.SOAPStubs.persistenz;
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

    private final static QName _GetDatenBatchResponse_QNAME = new QName("http://microServices.MSBenchmark/", "getDatenBatchResponse");
    private final static QName _SetDatenBatchResponse_QNAME = new QName("http://microServices.MSBenchmark/", "setDatenBatchResponse");
    private final static QName _SetDatenBatch_QNAME = new QName("http://microServices.MSBenchmark/", "setDatenBatch");
    private final static QName _GetStatus_QNAME = new QName("http://microServices.MSBenchmark/", "getStatus");
    private final static QName _ClassNotFoundException_QNAME = new QName("http://microServices.MSBenchmark/", "ClassNotFoundException");
    private final static QName _Fortschritt_QNAME = new QName("http://microServices.MSBenchmark/", "fortschritt");
    private final static QName _GetStatusResponse_QNAME = new QName("http://microServices.MSBenchmark/", "getStatusResponse");
    private final static QName _GetDatenBatch_QNAME = new QName("http://microServices.MSBenchmark/", "getDatenBatch");
    private final static QName _SQLException_QNAME = new QName("http://microServices.MSBenchmark/", "SQLException");

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
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link GetDatenBatch }
     * 
     */
    public GetDatenBatch createGetDatenBatch() {
        return new GetDatenBatch();
    }

    /**
     * Create an instance of {@link SQLException }
     * 
     */
    public SQLException createSQLException() {
        return new SQLException();
    }

    /**
     * Create an instance of {@link GetDatenBatchResponse }
     * 
     */
    public GetDatenBatchResponse createGetDatenBatchResponse() {
        return new GetDatenBatchResponse();
    }

    /**
     * Create an instance of {@link SetDatenBatchResponse }
     * 
     */
    public MSBenchmark.microServices.SOAPStubs.persistenz.SetDatenBatchResponse createSetDatenBatchResponse() {
        return new MSBenchmark.microServices.SOAPStubs.persistenz.SetDatenBatchResponse();
    }

    /**
     * Create an instance of {@link SetDatenBatch }
     * 
     */
    public SetDatenBatch createSetDatenBatch() {
        return new SetDatenBatch();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link ClassNotFoundException }
     * 
     */
    public ClassNotFoundException createClassNotFoundException() {
        return new ClassNotFoundException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDatenBatchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "getDatenBatchResponse")
    public JAXBElement<GetDatenBatchResponse> createGetDatenBatchResponse(GetDatenBatchResponse value) {
        return new JAXBElement<GetDatenBatchResponse>(_GetDatenBatchResponse_QNAME, GetDatenBatchResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDatenBatchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "setDatenBatchResponse")
    public JAXBElement<SetDatenBatchResponse> createSetDatenBatchResponse(SetDatenBatchResponse value) {
        return new JAXBElement<SetDatenBatchResponse>(_SetDatenBatchResponse_QNAME, SetDatenBatchResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDatenBatch }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "setDatenBatch")
    public JAXBElement<SetDatenBatch> createSetDatenBatch(SetDatenBatch value) {
        return new JAXBElement<SetDatenBatch>(_SetDatenBatch_QNAME, SetDatenBatch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "getStatus")
    public JAXBElement<GetStatus> createGetStatus(GetStatus value) {
        return new JAXBElement<GetStatus>(_GetStatus_QNAME, GetStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClassNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "ClassNotFoundException")
    public JAXBElement<ClassNotFoundException> createClassNotFoundException(ClassNotFoundException value) {
        return new JAXBElement<ClassNotFoundException>(_ClassNotFoundException_QNAME, ClassNotFoundException.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "getStatusResponse")
    public JAXBElement<GetStatusResponse> createGetStatusResponse(GetStatusResponse value) {
        return new JAXBElement<GetStatusResponse>(_GetStatusResponse_QNAME, GetStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDatenBatch }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "getDatenBatch")
    public JAXBElement<GetDatenBatch> createGetDatenBatch(GetDatenBatch value) {
        return new JAXBElement<GetDatenBatch>(_GetDatenBatch_QNAME, GetDatenBatch.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SQLException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://microServices.MSBenchmark/", name = "SQLException")
    public JAXBElement<SQLException> createSQLException(SQLException value) {
        return new JAXBElement<SQLException>(_SQLException_QNAME, SQLException.class, null, value);
    }

}
