package chaseGenerator.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import chaseGenerator.ObjectType;

@XmlRootElement
public class ObjectModel extends BaseModel {
	@XmlElement
	ObjectType ot = null;
	String description = null;

	/*
	 * Empty Constructor for XML loading
	 */
	public ObjectModel() {
	}

	public ObjectModel(String name) {
		this.setName(name);
	}

	public void setType(ObjectType t) {

	}

	public ObjectType getType() {
		return ot;
	}

	public void setDescription(String desc) {
		description = desc;
	}

	public String getDescription() {
		return description;
	}
}
