
package MSBenchmark.microServices.jaxws;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "batchVerarbeitung", namespace = "http://microServices.MSBenchmark/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "batchVerarbeitung", namespace = "http://microServices.MSBenchmark/")
public class BatchVerarbeitung {

    @XmlElement(name = "arg0", namespace = "")
    private ArrayList<String> arg0;

    /**
     * 
     * @return
     *     returns ArrayList<String>
     */
    public ArrayList<String> getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(ArrayList<String> arg0) {
        this.arg0 = arg0;
    }

}
