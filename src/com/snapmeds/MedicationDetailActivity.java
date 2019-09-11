package com.snapmeds;

import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.storage.SimpleStorage;
import com.utilities.Drug;
import com.utilities.Prescription;

public class MedicationDetailActivity extends DetailBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medication_detail);
		Intent intent = getIntent();

		// Set up Drug Object
		drug = new Drug();

		drug.setSetid(intent.getStringExtra(Constants.DRUG_SET_ID));
		drug.setName(intent.getStringExtra(Constants.DRUG_NAME));
		drug.setGenericnames(intent.getStringExtra(Constants.DRUG_GENERIC_NAME));
		drug.setUses(intent.getStringExtra(Constants.DRUG_USES));
		drug.setWarnings(intent.getStringExtra(Constants.DRUG_WARNINGS));
		drug.setPrecautions(intent.getStringExtra(Constants.DRUG_PRECAUTIONS));
		drug.setAdversereactions(intent.getStringExtra(Constants.DRUG_ADVERSE_REACTIONS));
		drug.setBoxwarnings(intent.getStringExtra(Constants.DRUG_BOX_WARNINGS));
		drug.setConflictingconditions(intent
				.getStringExtra(Constants.DRUG_CONFLICTING_CONDITIONS));
		drug.setMedicationguide(intent.getStringExtra(Constants.DRUG_MEDICATION_GUIDE));
		
		initializeInformation();
	}

	private boolean inCabinet(String id) {
		try {
			Prescription foundPrescription = SimpleStorage.loadPrescription(
					this, id);
			if (foundPrescription != null) {
				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medication_detail, menu);
		return true;
	}

	// Changes the menu action button to reflect add medication or view
	// prescription
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_medication:
			// Add Prescription to SimpleStorage
			Prescription newPrescription = new Prescription();
			newPrescription.setDrug(drug);
			try {
				SimpleStorage.addPrescription(this, newPrescription);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.invalidateOptionsMenu();
			Intent goToPrescription = new Intent(this, PrescriptionDetailActivity.class);
			goToPrescription.putExtra(Constants.DRUG_SET_ID, drug.getSetid());
			startActivity(goToPrescription);
			return true;
		case R.id.view_prescription:
			Intent viewPrescription = new Intent(this,
					com.snapmeds.PrescriptionDetailActivity.class);
			viewPrescription.putExtra(Constants.DRUG_SET_ID, drug.getSetid());
			startActivity(viewPrescription);
			return true;
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			Intent parentActivityIntent = new Intent(this, MainActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean inCabinet = inCabinet(drug.getSetid());
		MenuItem addMedication = menu.findItem(R.id.add_medication);
		addMedication.setVisible(!inCabinet);
		MenuItem viewPrescription = menu.findItem(R.id.view_prescription);
		viewPrescription.setVisible(inCabinet);
		return true;
	}
}