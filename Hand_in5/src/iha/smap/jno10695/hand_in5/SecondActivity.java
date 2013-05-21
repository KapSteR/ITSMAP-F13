package iha.smap.jno10695.hand_in5;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class SecondActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Second Activity", "OnCreate");
		setContentView(R.layout.activity_second);
		TextView contactView = (TextView) findViewById(R.id.textview2);
		
		
		
		
		Cursor cursor = getContacts();
		
		while (cursor.moveToNext()) {
			Log.d("Second Activity", "while loop");
			String displayName = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			contactView.append("Name: ");
			contactView.append(displayName);
			contactView.append("\n");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	private Cursor getContacts() {

		// Run query
		Log.d("Second Activity","getContacts");
		String[] projection = new String[] { ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME };
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '"
				+ ("1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		ContentResolver cr = getContentResolver();
		Log.d("Second Activity","getContacts done");
		// Return all rows
		return cr.query(MyContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);
	}
}
