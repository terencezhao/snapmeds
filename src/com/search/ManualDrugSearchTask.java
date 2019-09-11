package com.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.query.DailyMedQueryer;
import com.utilities.Drug;

/**
 * Asynchronous task for manually searching drugs. This task was created to
 * avoid querying web in the main thread Offloads querying to another
 * thread.
 * 
 * @author bheidkamp3
 * 
 */
public class ManualDrugSearchTask extends
		AsyncTask<String, Void, List<Drug>> {

	/**
	 * 
	 */
	private final SearchActivity searchActivity;

	/**
	 * @param searchActivity
	 */
	ManualDrugSearchTask(SearchActivity searchActivity) {
		this.searchActivity = searchActivity;
	}

	ProgressDialog spinner;
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		spinner = new ProgressDialog(searchActivity);
		spinner.setMessage("Loading...");
		spinner.setIndeterminate(true);
		spinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		spinner.setCancelable(false);
		spinner.show();
	}
	
	/**
	 * background task method, Checks the queryString against three
	 * possibilities. NDC with hyphen, without hyphens, or a name query.
	 * Depending on type of input, the appropriate query will take place
	 */
	@Override
	protected List<Drug> doInBackground(String... queryStrings) {
		String query = queryStrings[0];
		DailyMedQueryer queryer = new DailyMedQueryer();
		List<Drug> drugs = null;
		String ndcHyphensPattern = "(\\d+)-(\\d+)-(\\d+)";
		String ndcNoHyphensPattern = "(\\d+)";
		String strippedQuery = query.replaceAll("\\s", "");
		if (Pattern.matches(ndcHyphensPattern, strippedQuery)) {
			Drug drug = queryer.getDrugFromNDC(strippedQuery);
			drugs = new ArrayList<Drug>();
			drugs.add(drug);
		} else if (Pattern.matches(ndcNoHyphensPattern, strippedQuery)) {
			// if upc is 12 in length then it contains padding,
			// otherwise it is the ndc
			if (strippedQuery.length() == 12) {
				String ndc = strippedQuery.substring(1, 11);
				drugs = queryer.getDrugsFromNDC(ndc);
			} else {
				drugs = queryer.getDrugsFromNDC(strippedQuery);
			}
		} else {
			drugs = queryer.getDrugsFromName(query);
		}
		return drugs;
	}

	protected void onPostExecute(List<Drug> drugs) {
		if(!drugs.isEmpty()){
			ArrayList<Drug> drugsArrayList = new ArrayList<Drug>(drugs);
			ArrayAdapter<Drug> drugsListAdapter = new DrugAdapter(
					searchActivity.getApplicationContext(), (Activity)searchActivity, drugsArrayList);
			spinner.setIndeterminate(false);
			spinner.dismiss();
			searchActivity.setListAdapter(drugsListAdapter);
			spinner.setIndeterminate(false);
			spinner.dismiss();
		}
		else
    	{
			spinner.setIndeterminate(false);
			spinner.dismiss();
    		Toast.makeText(searchActivity.getApplicationContext(), "Drug not found. Try a different keyword", Toast.LENGTH_LONG).show();
    	}
		
		
		
	}
}