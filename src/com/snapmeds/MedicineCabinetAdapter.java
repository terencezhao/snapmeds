package com.snapmeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.utilities.Drug;
import com.utilities.Prescription;
import com.utilities.dragsort.DragSortController;
import com.utilities.dragsort.SimpleDragSortCursorAdapter;

public class MedicineCabinetAdapter extends SimpleDragSortCursorAdapter {
	private Activity activity;

	public MedicineCabinetAdapter(Context ctxt, int rmid, Cursor c,
			String[] cols, int[] ids, int something) {
		super(ctxt, rmid, c, cols, ids, something);
	}

	public void setActivity(Activity a) {
		activity = a;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		View.OnClickListener onClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Toast.makeText(activity.getApplicationContext(), "Click",
				// Toast.LENGTH_SHORT).show();
				Prescription prescription = (Prescription) getPersistenceHandler()
						.getItem(position);
				Drug drug = prescription.getDrug();
				Intent detailIntent = new Intent(
						activity.getApplicationContext(),
						com.snapmeds.PrescriptionDetailActivity.class);
				detailIntent.putExtra(Constants.DRUG_SET_ID, drug.getSetid());
				activity.startActivity(detailIntent);
			}
		};

		OnLongClickListener onLongClick = new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(activity.getApplicationContext(), "Long Click",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		};
		//v.findViewById(R.id.cabinet_item).setOnClickListener(onClick);
		//v.findViewById(R.id.cabinet_item).setOnLongClickListener(onLongClick);
		v.findViewById(R.id.item_icon).setOnClickListener(onClick);
		//v.findViewById(R.id.title).setOnClickListener(onClick);
		//v.findViewById(R.id.note).setOnClickListener(onClick);
		return v;

	}
}
