package com.snapmeds;

import java.util.HashMap;

import com.utilities.Drug;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailBaseActivity extends Activity {
	protected Drug drug;
	protected HashMap<String, TextView> titleInfoPair;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		titleInfoPair = new HashMap<String, TextView>(16);
	}

	protected void initializeInformation() {
		((TextView) findViewById(R.id.name)).setText(drug.getName());
		addInformation(R.string.drug_generic_name, drug.getGenericnames());
		addInformation(R.string.drug_uses, drug.getUses());
		addInformation(R.string.drug_warnings, drug.getWarnings());
		addInformation(R.string.drug_precautions, drug.getPrecautions());
		addInformation(R.string.drug_adverse_reactions, drug.getAdversereactions());
		addInformation(R.string.drug_box_warnings, drug.getBoxwarnings());
		addInformation(R.string.drug_conflicting_conditions,
				drug.getConflictingconditions());
		addInformation(R.string.drug_medication_guide,
				drug.getMedicationguide());

	}

	protected void addInformation(int titleStringId, String information) {
		String title = getString(titleStringId);

		TextView titleView = new TextView(getApplicationContext());
		titleView.setText("- " + title);
		titleView.setTextAppearance(getApplicationContext(), R.style.detail_information_title);
		((LinearLayout) findViewById(R.id.medication_information_container))
				.addView(titleView);

		TextView informationView = new TextView(getApplicationContext());
		informationView.setText(information);
		informationView.setTextAppearance(getApplicationContext(), R.style.detail_information_content);
		((LinearLayout) findViewById(R.id.medication_information_container))
				.addView(informationView);

		titleView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TextView tv = (TextView) v;
				TextView informationView = titleInfoPair.get(tv.getText());
				CharSequence oldTitleText = tv.getText();
				oldTitleText = oldTitleText.subSequence(2,
						oldTitleText.length());
				if (informationView.getVisibility() == View.GONE) {
					informationView.setVisibility(View.VISIBLE);
					tv.setText("- " + oldTitleText);
				} else {
					informationView.setVisibility(View.GONE);
					tv.setText("+ " + oldTitleText);
				}
				titleInfoPair.put(tv.getText().toString(), informationView);
			}
		});

		titleInfoPair.put(titleView.getText().toString(), informationView);
	}

}
