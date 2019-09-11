package com.utilities;


public class Prescription {
	private Drug drug;
	private Dosage dosage;
	private String note;
	private String imagePath;
	
	public Drug getDrug() {
		return drug;
	}
	public void setDrug(Drug drug) {
		this.drug = drug;
	}
	public Dosage getDosage() {
		return dosage;
	}
	public void setDosage(Dosage dosage) {
		this.dosage = dosage;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	public String getImagePath()
	{
	  return imagePath;
	}
	
	public void setImagePath(String path)
	{
	  imagePath = path;
	}
	
	@Override
  public boolean equals(Object other)
  {
    if (this == other) return true;
    if (!(other instanceof Prescription)) return false;
    Prescription otherPrescription = (Prescription)other;
    if(drug == null || otherPrescription.drug == null) return false;
    return drug.getSetid().equals(otherPrescription.drug.getSetid());
  }
}