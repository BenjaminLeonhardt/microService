
package MSBenchmark.microServices.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "getDatenBatch", namespace = "http://microServices.MSBenchmark/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDatenBatch", namespace = "http://microServices.MSBenchmark/")
public class GetDatenBatch {

    @XmlElement(name = "arg0", namespace = "")
    private MSBenchmark.microServices.Fortschritt arg0;

    /**
     * 
     * @return
     *     returns Fortschritt
     */
    public MSBenchmark.microServices.Fortschritt getArg0() {
        return this.arg0;
    }

    /**
     * 
     * @param arg0
     *     the value for the arg0 property
     */
    public void setArg0(MSBenchmark.microServices.Fortschritt arg0) {
        this.arg0 = arg0;
    }

}
