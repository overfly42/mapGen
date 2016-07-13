package chaseGenerator.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import chaseGenerator.ObjectType;

@XmlRootElement
public class ObjectModel extends BaseModel {
	@XmlElement
	public ObjectType ot = null;
	String description = null;
	public Map<String, Boolean> allowedTerrains;

	/*
	 * Empty Constructor for XML loading
	 */
	public ObjectModel() {
		allowedTerrains = new HashMap<>();
	}

	public ObjectModel(String name) {
		this();
		this.setName(name);
	}

	public void setType(ObjectType t) {
		ot = t;
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

	public boolean isAllowedTo(String areaType) {
		if(allowedTerrains.containsKey(areaType))
			return allowedTerrains.get(areaType);
		return true;
	}
}
