/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.ui.dialogs.FormulaEditorDialog;

public class ChangeVolumeByBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private Sprite sprite;
	private double volume;

	private transient View view;

	private Formula volumeFormula;

	private transient Brick instance = null;
	private transient FormulaEditorDialog formulaEditor;
	public transient boolean editorActive = false;

	public ChangeVolumeByBrick(Sprite sprite, double changeVolume) {
		this.sprite = sprite;
		this.volume = changeVolume;

		volumeFormula = new Formula(Double.toString(volume));
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		volume = volumeFormula.interpret();

		float currentVolume = SoundManager.getInstance().getVolume();
		currentVolume += volume;
		if (currentVolume < 0.0f) {
			currentVolume = 0.0f;
		} else if (currentVolume > 100.0f) {
			currentVolume = 100.0f;
		}
		SoundManager.getInstance().setVolume(currentVolume);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public double getVolume() {
		return volume;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		if (instance == null) {
			instance = this;
		}

		if (volumeFormula == null) {
			volumeFormula = new Formula(Double.toString(volume));
		}

		view = View.inflate(context, R.layout.brick_change_volume_by, null);

		TextView text = (TextView) view.findViewById(R.id.brick_change_volume_by_text_view);
		EditText edit = (EditText) view.findViewById(R.id.brick_change_volume_by_edit_text);
		volumeFormula.setTextFieldId(R.id.brick_change_volume_by_edit_text);
		volumeFormula.refreshTextField(view);

		text.setVisibility(View.GONE);
		edit.setVisibility(View.VISIBLE);

		edit.setOnClickListener(this);

		return view;
	}

	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_change_volume_by, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new ChangeVolumeByBrick(getSprite(), getVolume());
	}

	public void onClick(View view) {
		final Context context = view.getContext();

		if (!editorActive) {
			editorActive = true;
			formulaEditor = new FormulaEditorDialog(context, instance);
			formulaEditor.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface editor) {

					//size = formulaEditor.getReturnValue();
					formulaEditor.dismiss();

					editorActive = false;
				}
			});
			formulaEditor.show();
		}

		formulaEditor.setInputFocusAndFormula(volumeFormula);

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		//		final EditText input = new EditText(context);
		//		input.setText(String.valueOf(volume));
		//		input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
		//				| InputType.TYPE_NUMBER_FLAG_SIGNED);
		//		input.setSelectAllOnFocus(true);
		//		dialog.setView(input);
		//		dialog.setOnCancelListener((OnCancelListener) context);
		//		dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				try {
		//					volume = Float.parseFloat(input.getText().toString());
		//				} catch (NumberFormatException exception) {
		//					Toast.makeText(context, R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
		//				}
		//				dialog.cancel();
		//			}
		//		});
		//		dialog.setNeutralButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				dialog.cancel();
		//			}
		//		});
		//
		//		AlertDialog finishedDialog = dialog.create();
		//		finishedDialog.setOnShowListener(Utils.getBrickDialogOnClickListener(context, input));
		//
		//		finishedDialog.show();
	}

}
