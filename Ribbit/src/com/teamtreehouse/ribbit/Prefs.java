package com.teamtreehouse.ribbit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

	}

	public Boolean checkpref() {
		SharedPreferences getprefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean check = getprefs.getBoolean("checkbox", true);
		return check;

	}

	public int listpref() {
		SharedPreferences getData = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		String values = getData.getString("list", "1");
		int x = Integer.parseInt(values);
		return x;
	}

}
