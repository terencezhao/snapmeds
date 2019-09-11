package com.utilities;

import org.codehaus.jackson.annotate.JsonIgnore;

//temporary data container
public class Drug {
	
	@JsonIgnore
	public boolean isPopulated(){
		return getName()!=null && !getName().isEmpty() && !
				getName().equalsIgnoreCase("null") && !getName().equalsIgnoreCase("false");
	}
	
	public String getAdversereactions() {
		return adversereactions;
	}
	public void setAdversereactions(String adversereactions) {
		this.adversereactions = adversereactions;
	}
	public String getConflictingconditions() {
		return conflictingconditions;
	}
	public void setConflictingconditions(String conflictingconditions) {
		this.conflictingconditions = conflictingconditions;
	}
	public String getGenericnames() {
		return genericnames;
	}
	public void setGenericnames(String genericnames) {
		this.genericnames = genericnames;
	}
	public String getPrecautions() {
		return precautions;
	}
	public void setPrecautions(String precautions) {
		this.precautions = precautions;
	}
	public String getBoxwarnings() {
		return boxwarnings;
	}
	public void setBoxwarnings(String boxwarnings) {
		this.boxwarnings = boxwarnings;
	}
	public String getUses() {
		return uses;
	}
	public void setUses(String uses) {
		this.uses = uses;
	}
	public String getWarnings() {
		return warnings;
	}
	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMedicationguide() {
		return medicationguide;
	}
	public void setMedicationguide(String medicationguide) {
		this.medicationguide = medicationguide;
	}
	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}
	
	private String adversereactions;
	private String conflictingconditions;
	private String genericnames;
	private String precautions;
	private String boxwarnings;
	private String uses;
	private String warnings;
	private String name;
	private String medicationguide;
	private String setid; 
}
