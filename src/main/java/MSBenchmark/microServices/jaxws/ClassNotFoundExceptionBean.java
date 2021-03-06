
package MSBenchmark.microServices.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2.9
 * 
 */
@XmlRootElement(name = "ClassNotFoundException", namespace = "http://microServices.MSBenchmark/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassNotFoundException", namespace = "http://microServices.MSBenchmark/", propOrder = {
    "exception",
    "message"
})
public class ClassNotFoundExceptionBean {

    private Throwable exception;
    private String message;

    /**
     * 
     * @return
     *     returns Throwable
     */
    public Throwable getException() {
        return this.exception;
    }

    /**
     * 
     * @param exception
     *     the value for the exception property
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * 
     * @param message
     *     the value for the message property
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
