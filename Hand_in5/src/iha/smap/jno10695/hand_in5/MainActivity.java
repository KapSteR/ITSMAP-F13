package iha.smap.jno10695.hand_in5;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DatabaseHandler db = new DatabaseHandler(this);

		// Inserting Costumers
		Log.d("Insert: ", "Inserting ..");
		db.addCostumer(new Costumer(1, "Ravi", "9100000000"));
		db.addCostumer(new Costumer(2, "Srinivas", "9199999999"));
		db.addCostumer(new Costumer(3, "Tommy", "9522222222"));
		db.addCostumer(new Costumer(4, "Lolface", "37708"));
		
		//for(int i = 0 ; i < 100 ; i++)
		//{
		//	db.deleteCostumer(new Costumer(i, "", ""));
		//}

		// Reading all costumers
		Log.d("Reading: ", "Reading all costumers..");
		List<Costumer> costumers = db.getAllCostumers();

		for (Costumer cn : costumers) {
			String log = "Id: " + cn.getID() + " ,Name: " + cn.getName()
					+ " ,Phone: " + cn.getAddress(); // Writing costumers to log
			Log.d("Name: ", log);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    Intent intent = new Intent(this, SecondActivity.class);
	    startActivity(intent);
	}

}
