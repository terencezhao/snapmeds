package com.query;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.utilities.Drug;

public class DailyMedQueryer extends Queryer {

	/**
	 * Returns of all possible drugs from an ndc (checks all variants of hyphens)
	 * 
	 * @param	ndc	the National Drug Code to search from
	 * @return		a list of Drugs with the NDC. 
	 * @see 		Drug
	 */
	@Override
	public List<Drug> getDrugsFromNDC(String ndc) {
		LinkedList<Drug> drugs = new LinkedList<Drug>();
		ndc = ndc.replace("-", "");

		String[] ndcs = new String[3];
		ndcs[0] = ndc.substring(0, 4) + "-" + ndc.substring(4, 8) + "-"
				+ ndc.substring(8);
		ndcs[1] = ndc.substring(0, 5) + "-" + ndc.substring(5, 8) + "-"
				+ ndc.substring(8);
		ndcs[2] = ndc.substring(0, 5) + "-" + ndc.substring(5, 9) + "-"
				+ ndc.substring(9);

		for (int i = 0; i < 3; i++) {
			Drug drug = getDrugFromNDC(ndcs[i]);
			if (drug.isPopulated()) {
				drugs.add(drug);
			}
		}
		return drugs;
	}

	/**
	 * Checks database for drug with that ndc. If it does not exist, find the setid from
	 * Dailymed webpage, put it in the database, and get the drug.
	 * 
	 * @param ndcWithHyphens 	the National Drug Code with hyphens
	 * 
	 * @return					the {@link Drug} object corresponding to the NDC				
	 */
	public Drug getDrugFromNDC(String ndcWithHyphens) {
		Drug drug = new Drug();
		// String ndcWithoutHyphens = ndcWithHyphens.replace("-","");
		String ndcWithoutHyphens = ndcWithHyphens;
		// call our database, if does not exist, then put it in
		JSONArray json = getDrugJSONFromNDC(ndcWithoutHyphens);
		try {
			if (json == null ||
					((String) ((JSONObject) json.get(5)).get("name")).isEmpty()) {
				String setid = getSetIDFromNDC(ndcWithHyphens); // call Dailymed
																// webpage
				if (setid == null) {
					return drug;
				}
				setNDCSetIDTable(ndcWithoutHyphens, setid);
				json = getDrugJSONFromNDC(ndcWithoutHyphens);
			}
		} catch (JSONException e) {
			// do nothing
		}
		populateDrug(drug, json);
		return drug;
	}

	public List<Drug> getDrugsFromName(String name) {
		LinkedList<Drug> drugs = new LinkedList<Drug>();
		JSONArray drugsJson = getDrugsJSONFromName(name);

		if (drugsJson != null) {
			for (int i = 0; i < drugsJson.length(); i++) {
				Drug drug = new Drug();
				JSONArray drugJson;
				try {
					drugJson = drugsJson.getJSONArray(i);
					populateDrug(drug, drugJson);
					if (drug.isPopulated()){
						drugs.add(drug);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return drugs;
	}

	protected JSONArray getDrugsJSONFromName(String name) {
		String get = "?username=" + getDatabaseUsername() + "&password="
				+ getDatabasePassword() + "&name=" + name;
		String url = getGetDatabaseURL();
		return getJSONFromURL(url + get);
	}

	@SuppressWarnings("finally")
	protected JSONArray getDrugJSONFromNDC(String ndc) {
		String get = "?username=" + getDatabaseUsername() + "&password="
				+ getDatabasePassword() + "&ndc=" + ndc;
		String url = getGetDatabaseURL();
		JSONArray json = null;
		// This will only return a single Drug, so just get the first Drug
		try {
			json = getJSONFromURL(url + get).getJSONArray(0);
		} catch (JSONException e) {
			json = new JSONArray("[]");
		} finally {
			return json;
		}

	}

	@SuppressWarnings("finally")
	protected JSONArray getJSONFromURL(String url) {
		JSONArray json = null;
		String urljson = "";
		try {
			urljson = readURL(url);
			json = new JSONArray(urljson);
		} catch (JSONException e) {
			return new JSONArray("[[]]");
		} finally {
			return json;
		}
	}

	protected void setNDCSetIDTable(String ndc, String setid) {
		String get = "?username=" + getDatabaseUsername() + "&password="
				+ getDatabasePassword() + "&ndc=" + ndc + "&setid=" + setid;
		readURL(getAddToDatabaseURL() + get);
	}

	protected String getSetIDFromNDC(String ndc) {
		String setid = null;
		try {
			JSONObject json = getSPLsFromNDC(ndc);
			JSONArray data = (JSONArray) json.get("DATA");
			JSONArray first = (JSONArray) data.get(0);
			setid = (String) first.get(0);
		} catch (Exception e) {
			// do nothing
		}
		return setid;
	}

	protected void populateDrug(Drug drug, JSONArray json) {
		try {
			drug.setAdversereactions(((JSONObject) json.get(0))
					.getString("adversereactions"));
			drug.setBoxwarnings(((JSONObject) json.get(1))
					.getString("boxwarnings"));
			drug.setConflictingconditions(((JSONObject) json.get(2))
					.getString("conflictingconditions"));
			drug.setGenericnames(((JSONObject) json.get(3))
					.getString("genericnames"));
			drug.setMedicationguide(((JSONObject) json.get(4))
					.getString("medicationguide"));
			drug.setName(((JSONObject) json.get(5)).getString("name"));
			drug.setPrecautions(((JSONObject) json.get(6))
					.getString("precautions"));
			drug.setUses(((JSONObject) json.get(7)).getString("uses"));
			drug.setWarnings(((JSONObject) json.get(8)).getString("warnings"));
			drug.setSetid(((JSONObject) json.get(9)).getString("setid"));
		} catch (Exception e) {
			// do nothing
		}
	}

	@Override
	protected String getSPLsURLFromNDC(String ndc) {
		return "http://dailymed.nlm.nih.gov/dailymed/services/v1/ndc/" + ndc
				+ "/spls.json";
	}

	@Override
	protected String getImprintDataURLFromNDC(String ndc) {
		return "http://dailymed.nlm.nih.gov/dailymed/services/v1/ndc/" + ndc
				+ "/imprintdata.json";
	}

	@Override
	protected String getSPLsURLFromDrugname(String name) {
		return "http://dailymed.nlm.nih.gov/dailymed/services/v1/drugname/"
				+ name + "/spls.json";
	}

	protected String getDatabaseUsername() {
		return "snapmeds_admin";
	}

	protected String getDatabasePassword() {
		return "safeerides";
	}

	protected String getGetDatabaseURL() {
		return "http://snapmeds.web.engr.illinois.edu/getFromDatabase.php";
	}

	protected String getAddToDatabaseURL() {
		return "http://snapmeds.web.engr.illinois.edu/addToDatabase.php";
	}
}
