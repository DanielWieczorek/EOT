package de.wieczorek.eot.ui;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.wieczorek.eot.domain.machine.MachineState;

@XmlRootElement
public class MachineInfo {

    private MachineState state;

    private List<IndividualInfo> population;

    @XmlElement(name = "state")
    public MachineState getState() {
	return state;
    }

    public void setState(MachineState state) {
	this.state = state;
    }

    @XmlElementWrapper(name = "population")
    @XmlElement(name = "individual")
    public List<IndividualInfo> getPopulation() {
	return population;
    }

    public void setPopulation(List<IndividualInfo> population) {
	this.population = population;
    }

}
