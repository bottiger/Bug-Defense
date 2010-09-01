package com.game.towerdefense;

import android.content.Context;
import android.content.Intent;

public class StatReporter {
	
	public static void sendEmail(Context context, int level) {
	    // Setup the recipient in a String array
//	    String[] mailto = { "bottiger@gmail.com" };
//	    // Create a new Intent to send messages
//	    Intent sendIntent = new Intent(Intent.ACTION_SEND);
//	    // Add attributes to the intent
//	    sendIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
//	    sendIntent.putExtra(Intent.EXTRA_SUBJECT,
//	        "[Bug defense] level: " + level);
//	    sendIntent.putExtra(Intent.EXTRA_TEXT,
//	        "");
//	    sendIntent.setType("text/plain");
//	    Intent.createChooser(sendIntent, "MySendMail");
//	    //startActivity(Intent.createChooser(sendIntent, "MySendMail"));
	    
	    /* Create the Intent */  
	    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
	      
	    /* Fill it with Data */  
	    emailIntent.setType("plain/text");  
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"bottiger@gmail.com"});  
	    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");  
	    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");  
	    
	    /* Send it off to the Activity-Chooser */  
	    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));  
	}

}
