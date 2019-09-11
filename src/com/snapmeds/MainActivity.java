package com.snapmeds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.search.SearchActivity;
import com.snapmeds.scanner.IntentIntegrator;
import com.snapmeds.scanner.IntentResult;
import com.snapmeds.scanner.RetrieveDrugsTask;
import com.storage.SimpleStorage;
import com.utilities.Prescription;
import com.utilities.dragsort.DragSortListView;

public class MainActivity extends Activity implements PersistenceHandler {
	List<Prescription> cabinetList;
	MedicineCabinetAdapter adapter;
	private boolean cabinetChanged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medicine_cabinet);

		loadCabinet();
		cabinetChanged = false;

		String[] cols = { "name", "note" };
		int[] ids = { R.id.title, R.id.note };
		adapter = new MedicineCabinetAdapter(this,
				R.layout.medicine_cabinet_item, null, cols, ids, 0);
		adapter.setActivity(this);
		adapter.setPersistenceHandler(this);

		DragSortListView list = (DragSortListView) findViewById(R.id.cabinet_list);
		list.setAdapter(adapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (cabinetChanged) {
			try {
				SimpleStorage.clearPrescriptions(this);
				SimpleStorage.addPrescriptions(this, cabinetList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			cabinetChanged = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadCabinet();
		buildCabinetList();
	}

	private void buildCabinetList() {
		MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "name",
				"note" });

		int i = 0;
		for (Prescription p : cabinetList) {
			cursor.newRow().add(i).add(p.getDrug().getName()).add(p.getNote());
			i++;
		}
		adapter.changeCursor(cursor);
	}

	public void loadCabinet() {
		try {
			cabinetList = SimpleStorage.loadPrescriptions(this);
		} catch (Exception e) {
			cabinetList = new ArrayList<Prescription>();
		}
	}

	@Override
	public Object getItem(int pos) {
		return cabinetList.get(pos);
	}

	public void reorderItems(int to, int from) {
		if (to == from) {
			return;
		}
		cabinetChanged = true;
		Prescription p = cabinetList.get(from);
		if (from < to) {
			cabinetList.add(to + 1, p);
			cabinetList.remove(from);
		} else {
			cabinetList.add(to, p);
			cabinetList.remove(from + 1);
		}
	}

	public void removeItem(int pos) {
		cabinetChanged = true;
		cabinetList.remove(pos);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * onSearchRequested is called when the search button is pressed
	 * This method starts the search activity
	 */
	@Override
	public boolean onSearchRequested(){
		if (isOnline()) {
			Intent search_intent = new Intent(this, SearchActivity.class);
			startActivity(search_intent);
			return true;
		} else {
			Toast.makeText(this,
					"Network Connection Required for Manual Search",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_calendar:
			// Open calendar for viewing
			Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
			builder.appendPath("time");
			ContentUris.appendId(builder, Calendar.getInstance()
					.getTimeInMillis());
			Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder
					.build());
			startActivity(intent);
			return true;
		case R.id.menu_scan:
			if (isOnline()) {
				IntentIntegrator.initiateScan(this);
				return true;
			} else {
				Toast.makeText(this, "Network Connection Required for Scanner",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		case R.id.menu_search:
			return onSearchRequested();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE: {
			if (resultCode != RESULT_CANCELED) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, data);
				if (scanResult != null) {
					String upc = scanResult.getContents();
					Log.d("UPC", upc);
					new RetrieveDrugsTask(this).execute(upc);
				}
			}
			break;
		}
		}
	}

	/**
	 * isOnline checks to make sure the phone has an internet connection
	 * 
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * isLastActivity checks to see if this activity is the last activity from
	 * snapmeds on the back stack
	 * 
	 * @return true if it is the last, false otherwise
	 */
	private boolean isLastActivity() {
		final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningTaskInfo> tasksInfo = am.getRunningTasks(1024);

		final String ourAppPackageName = getPackageName();
		RunningTaskInfo taskInfo;
		final int size = tasksInfo.size();
		for (int i = 0; i < size; i++) {
			taskInfo = tasksInfo.get(i);
			if (ourAppPackageName
					.equals(taskInfo.baseActivity.getPackageName())) {
				return taskInfo.numActivities == 1;
			}
		}

		return false;
	}


	/**
	 * onBackPressed handles the case where the back button is pressed and would
	 * exit the application. Prompts user to confirm that the application will
	 * be exited
	 */
	@Override
	public void onBackPressed() {
		if (isLastActivity()) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Closing Activity")
					.setMessage("Are you sure you want to close this activity?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}

							}).setNegativeButton("No", null).show();
		} else {
			super.onBackPressed();
		}
	}
}
