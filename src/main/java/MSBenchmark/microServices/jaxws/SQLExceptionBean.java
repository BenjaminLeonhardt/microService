
package MSBenchmark.microServices.jaxws;

import java.sql.SQLException;
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
@XmlRootElement(name = "SQLException", namespace = "http://microServices.MSBenchmark/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SQLException", namespace = "http://microServices.MSBenchmark/", propOrder = {
    "SQLState",
    "errorCode",
    "message",
    "nextException"
})
public class SQLExceptionBean {

    private String SQLState;
    private int errorCode;
    private String message;
    private SQLException nextException;

    /**
     * 
     * @return
     *     returns String
     */
    public String getSQLState() {
        return this.SQLState;
    }

    /**
     * 
     * @param SQLState
     *     the value for the SQLState property
     */
    public void setSQLState(String SQLState) {
        this.SQLState = SQLState;
    }

    /**
     * 
     * @return
     *     returns int
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * 
     * @param errorCode
     *     the value for the errorCode property
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
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

    /**
     * 
     * @return
     *     returns SQLException
     */
    public SQLException getNextException() {
        return this.nextException;
    }

    /**
     * 
     * @param nextException
     *     the value for the nextException property
     */
    public void setNextException(SQLException nextException) {
        this.nextException = nextException;
    }

}
