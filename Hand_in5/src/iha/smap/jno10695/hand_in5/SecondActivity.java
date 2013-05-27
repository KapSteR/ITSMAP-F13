package iha.smap.jno10695.hand_in5;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
		insertContact(5, "jeppe", "læssøesgade");
		deleteContact(3);
		Cursor cursor = getContact();

		while (cursor.moveToNext()) {
			Log.d("Second Activity", "while loop until no more id");
			String displayId = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseHandler.KEY_ID));
			contactView.append("ID: ");
			contactView.append(displayId);
			String displayName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseHandler.KEY_NAME));
			contactView.append("  Name: ");
			contactView.append(displayName);
			String displayAddress = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseHandler.KEY_ADDRESS));
			contactView.append("  Address: ");
			contactView.append(displayAddress);
			contactView.append("\n");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}

	private Cursor getContact() {

		// Run query
		Log.d("Second Activity", "getContacts");
		String[] projection = { DatabaseHandler.KEY_ID,
				DatabaseHandler.KEY_NAME, DatabaseHandler.KEY_ADDRESS };
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;

		ContentResolver cr = getContentResolver();
		Log.d("Second Activity", "getContacts done");
		// Return all rows
		return cr.query(MyContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, sortOrder);
	}

	private Uri insertContact(int id, String name, String address) {

		// Run query
		Log.d("Second Activity", "insert Contacts");
		// Create a new row of values to insert.
		ContentValues newValues = new ContentValues();
		// Assign values for each row.
		newValues.put(DatabaseHandler.KEY_ID, id);
		newValues.put(DatabaseHandler.KEY_NAME, name);
		newValues.put(DatabaseHandler.KEY_ADDRESS, address);
		ContentResolver cr = getContentResolver();
		Log.d("Second Activity", "insert Contacts done");
		// Return all rows
		return cr.insert(MyContentProvider.CONTENT_URI, newValues);
	}

	private void deleteContact(int id) {
		Log.d("Second Activity", "delete Contacts");
		// Specify a where clause that determines which row(s) to delete.
		// Specify where arguments as necessary.
		String whereArgs[] = null;
		// Get the Content Resolver.
		ContentResolver cr = getContentResolver();
		// Delete the matching rows
		Log.d("Second Activity", "insert Contacts done");
		cr.delete(MyContentProvider.CONTENT_URI, DatabaseHandler.KEY_ID + "=" + id, whereArgs);
	}
}
