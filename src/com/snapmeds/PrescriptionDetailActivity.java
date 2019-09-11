package com.snapmeds;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dosage.DosageParserActivity;
import com.storage.SimpleStorage;
import com.utilities.Dosage;
import com.utilities.Prescription;

public class PrescriptionDetailActivity extends DetailBaseActivity {
	private static final int PRESCRIPTION_DETAIL_ACTIVITY_REQUEST_CODE = 1;
	private Prescription prescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prescription_detail);
		
		String setId = getIntent().getStringExtra(Constants.DRUG_SET_ID);
		try {
			prescription = SimpleStorage.loadPrescription(this, setId);
			drug = prescription.getDrug();
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView) findViewById(R.id.name)).setText(drug.getName());

		initializeNoteEdit();
		
		initializeImage();
		
		initializeInformation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prescription_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dosage:
			Intent dosage_intent = new Intent(this, DosageParserActivity.class);
			dosage_intent.putExtra(Constants.DRUG_SET_ID, prescription.getDrug().getSetid());
			startActivity(dosage_intent);
			return true;
		case R.id.remove_prescription:
			AlertDialog removeDialog = createRemoveDialog();
			removeDialog.show();
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

	/**
	 * showRemoveDialog creates the confirmation alert dialog for removing a
	 * drug. "Cancel" will cancel the dialog, "remove" will remove the
	 * prescription
	 * 
	 * @return AlertDialog for removing a prescription
	 */
	private AlertDialog createRemoveDialog() {

		// Reload prescription, prescription loaded on create may be stale
		// if user uses back button
		try {
			prescription = SimpleStorage.loadPrescription(this, prescription.getDrug().getSetid());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.remove_dialog_title);
		builder.setMessage(R.string.remove_dialog_message);
		builder.setIconAttribute(android.R.attr.alertDialogIcon);
		builder.setPositiveButton(R.string.remove,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							Dosage dosage = prescription.getDosage();
							if (dosage != null) {
								Log.d("remove", "removed reminders");
								dosage.cancelReminders(PrescriptionDetailActivity.this);
							} else {
								Log.d("remove", "no reminders to remove");
							}
							SimpleStorage.removePrescription(
									PrescriptionDetailActivity.this,
									prescription);
						} catch (IOException e) {
							e.printStackTrace();
						}
						Intent goHome = new Intent(
								PrescriptionDetailActivity.this,
								MainActivity.class);
						goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(goHome);
						finish();
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
		return builder.create();

	}

	protected void initializeNoteEdit() {
		String note = prescription.getNote() == null ? "" : prescription
				.getNote();
		EditText noteField = (EditText) findViewById(R.id.note);
		noteField.setEnabled(false);
		noteField.setText(note);
		final Button editNoteButton = (Button) findViewById(R.id.prescription_note_edit);
		final Activity activity = this;

		editNoteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText noteField = (EditText) findViewById(R.id.note);
				boolean isEnabled = noteField.isEnabled();
				if (isEnabled) {
					String note = noteField.getText().toString();
					editNoteButton.setText("Edit Note");
					updatePrescriptionNote(note);
				} else {
					editNoteButton.setText("Save");
					noteField.requestFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(noteField,
							InputMethodManager.SHOW_IMPLICIT);
				}

				noteField.setEnabled(!isEnabled);
			}

			private void updatePrescriptionNote(String note) {
				try {
					prescription.setNote(note);
					List<Prescription> prescriptions = SimpleStorage
							.loadPrescriptions(activity);
					if (prescriptions.contains(prescription)) {
						SimpleStorage.editPrescription(activity, prescription);
					} else {
						SimpleStorage.addPrescription(activity, prescription);
					}
					Prescription p = SimpleStorage.loadPrescription(activity,
							prescription.getDrug().getSetid());
					System.out.println(p.getNote());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void initializeImage() {
		ImageView imageView = (ImageView) findViewById(R.id.image);
		final File basePath = new File(
				Environment.getExternalStorageDirectory() + File.separator
						+ "SnapMeds" + File.separator);
		System.out.println("BASE PATH: " + basePath.getAbsolutePath());
		basePath.mkdirs();

		if (prescription.getImagePath() != null) {
			File imagePath = new File(prescription.getImagePath());
			Bitmap thumbnail = getBitmapFromFile(imagePath);
			imageView.setImageBitmap(thumbnail);
		}

		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				File imagePath = new File(basePath, prescription.getDrug()
						.getName());

				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(imagePath));
				startActivityForResult(cameraIntent,
						PRESCRIPTION_DETAIL_ACTIVITY_REQUEST_CODE);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PRESCRIPTION_DETAIL_ACTIVITY_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			final File basePath = new File(
					Environment.getExternalStorageDirectory() + File.separator
							+ "SnapMeds");
			File imagePath = new File(basePath, prescription.getDrug()
					.getName());
			Bitmap thumbnail = getBitmapFromFile(imagePath);
			ImageView imageView = (ImageView) findViewById(R.id.image);
			imageView.setImageBitmap(thumbnail);
			updatePrescriptionImage(imagePath);
		}
	}

	private Bitmap getBitmapFromFile(File imagePath) {
		Bitmap photo = BitmapFactory.decodeFile(imagePath.getAbsolutePath());
		Bitmap thumbnail = Bitmap.createScaledBitmap(photo, 100, 100, true);
		return thumbnail;
	}

	private void updatePrescriptionImage(File imagePath) {
		if (prescription.getImagePath() != null) {
			File oldImagePath = new File(prescription.getImagePath());
			oldImagePath.delete();
		}
		prescription.setImagePath(imagePath.getAbsolutePath());

		try {
			List<Prescription> prescriptions = SimpleStorage
					.loadPrescriptions(this);
			if (prescriptions.contains(prescription)) {
				SimpleStorage.editPrescription(this, prescription);
			} else {
				SimpleStorage.addPrescription(this, prescription);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
