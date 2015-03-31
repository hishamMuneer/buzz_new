package com.teamtreehouse.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class RibbitApplication extends Application {
	@SuppressWarnings("deprecation")
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "Uzm5VkjyRBP1gYh5oqiDWcg6O5wC0yZzjkKDd4m0",
				"n5fSbyo5Ie9iRH9zY10Pokb16vVY8aag1JqERK18");
		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();
		PushService.setDefaultPushCallback(this, MainActivity.class);
	    ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
	public static void updateParseInstallation(ParseUser user)
	{
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID,user.getObjectId());
		installation.saveInBackground();
	}
	
}
