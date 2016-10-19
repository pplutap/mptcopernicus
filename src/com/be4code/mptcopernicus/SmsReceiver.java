package com.be4code.mptcopernicus;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	
	NotificationManager notificationManager;
	Notification notification;
	PendingIntent pendingIntent;
	final int TaskIDNotification = new Random().nextInt(99999999);
	
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String messages = "";
        if ( extras != null ) {
            Object[] smsExtra = (Object[]) extras.get( "pdus" );

            for ( int i = 0; i < smsExtra.length; ++i ) {
            	SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
            	String body = sms.getMessageBody();
            	String address = sms.getOriginatingAddress();
            	if(address.equals("665660660")) {
	            	messages += "SMS from " + address + " :\n";
	            	messages += body + "\n";
	            	
	            	notificationManager = (NotificationManager) context.getSystemService("notification");
	    			notification = new Notification(R.drawable.ic_launcher,	"Express Radio Taxi", System.currentTimeMillis());
	    			notification.flags = notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    			pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class), 0);
	    			notification.setLatestEventInfo(context, "Pobieranie pliku.", "Piosenka: ", pendingIntent);
	    			notificationManager.notify(TaskIDNotification, notification);
            	}
            }
        }
    }
}
