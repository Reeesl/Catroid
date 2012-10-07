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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.physics.PhysicSettings;
import org.catrobat.catroid.physics.PhysicWorld;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.dialogs.BrickTextDialog;

public class SetMassBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient PhysicWorld physicWorld;
	private Sprite sprite;
	private float mass;

	private transient View view;

	public SetMassBrick() {
		physicWorld = ProjectManager.getInstance().getCurrentProject().getPhysicWorld();
	}

	public SetMassBrick(PhysicWorld physicWorld, Sprite sprite, float mass) {
		this.physicWorld = physicWorld;
		this.sprite = sprite;
		this.mass = mass;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		physicWorld.getPhysicObject(sprite).setMass(mass);
	}

	public void setPhysicWorld(PhysicWorld physicWorld) {
		this.physicWorld = physicWorld;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_set_mass, null);

		EditText editMass = (EditText) view.findViewById(R.id.brick_set_mass_edit_text);
		editMass.setText(String.valueOf(mass));
		editMass.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_set_mass, null);
	}

	@Override
	public Brick clone() {
		return new SetMassBrick(physicWorld, getSprite(), mass);
	}

	@Override
	public void onClick(View view) {

		ScriptTabActivity activity = (ScriptTabActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				input.setText(String.valueOf(mass));
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					mass = Float.parseFloat(input.getText().toString());
					if (mass < PhysicSettings.Object.MIN_MASS) {
						mass = 0.0f;
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};

		editDialog.show(activity.getSupportFragmentManager(), "dialog_set_mass_brick");
	}
}