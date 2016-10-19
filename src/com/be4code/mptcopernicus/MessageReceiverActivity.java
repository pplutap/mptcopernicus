package com.be4code.mptcopernicus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

public class MessageReceiverActivity extends Activity {

	
	protected boolean _active = true;
	protected int _splashTime = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Czy wyłączyć aplikację?")
			       .setCancelable(false)
			       .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                MessageReceiverActivity.this.finish();
			           }
			       })
			       .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
