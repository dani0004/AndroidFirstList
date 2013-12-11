/*
 * class CREATETODO
 * Purpose: Create the activity that collects data from the user
 * User provides the todo item
 * Which will be stored in the database
 * @author: Wendy-Anne Daniel
 * @date: December 2013
 * @version:1.0
 * @see:com.algonquincollege.dani0004.todolist
 * @see: android.database.sqlite.SQLiteOpenHelper
 *  
 * 
*/
package com.algonquincollege.dani0004.todolist;

import java.util.List;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTodo extends Activity {
	
	private static final String TAG="CreateTodo:";
	private TodoDataSource tdsource=null;
	private List<TodoModel> tdmodelList=null;
	private TodoModel tdm=null;
	private ArrayAdapter<TodoModel> tdadapter;
	private static String PARCELABLE_S="todoModel";
	private int CANCELLED=0;
	public static int RESULT_CANCELLED=1;
	
	/* onCreate
	 * creates an instance of the datasource
	 * delegates the open of the database
	 * @param Bundle - the data that is sent to the activity
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_todo);
		tdsource=new TodoDataSource(this);
		tdsource.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_todo, menu);
		return true;
	}
	//Action listeners
	/* onAddTodo
	 * collects the data from the interface
	 * validates it
	 * if valid, delegates the creation of a row in the database
	 * calls finish() to return back to the main activity
	 * If invalid, shows a message to the user
	 * @param View - the view that generated the action
	*/
	public void onAddTodo(View view)
	{
		try
		{
			CANCELLED=0;
			EditText et=(EditText)(findViewById(R.id.addTodo_txt));
			String etentry=et.getText().toString();
			etentry=etentry.trim();
			if(etentry.length()>0){
				tdm=tdsource.createTodo(etentry);
				finish();
			}
			else{
				sendToast(R.string.missing_todo_error);
				
			}
	
		}
		catch(Exception e){
			Log.e(TAG,"Exception in onAddTodo "+e.getMessage());
			sendToast(R.string.error_condition);
		}
		
	}
	
	private void sendToast(int toastid)
	{
		//show a toast message
		// create a Toast displaying error information
         Toast errorToast = Toast.makeText(CreateTodo.this, 
        		 CreateTodo.this.getResources().getString(
        				 toastid), Toast.LENGTH_LONG);
         errorToast.setGravity(Gravity.CENTER, 0, 0);
         errorToast.show(); // show the Toast 
	}
	/* onCancelTodo
	 * sets a cancel state for this activity
	 * calls finish() to return back to the main activity
	 * If invalid, shows a message to the user
	 * @param View - the view that generated the action
	*/
	public void onCancelTodo(View view)
	{
		CANCELLED=1;
		finish();
		
	}
	/* finish is called when the sub-activity goes back **/
	/* If the activity is not in the cancelled state
	 * sends information back to the calling activity via intent
	 * sends the updated model to the main activity
	 * If activity is in cancelled state
	 * creates an intent and sends result cancelled back to main activity
	*/
	@Override
	public void finish() {
		if(CANCELLED==0){
	  // Prepare data intent 
		 Intent tdlistIntent=new Intent(this, TodoList.class);
		 tdlistIntent.putExtra(PARCELABLE_S, tdm);
		
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, tdlistIntent);
		}
		if(CANCELLED==1){
			  // Prepare data intent
				
				 Intent tdlistIntent=new Intent();
			     setResult(RESULT_CANCELLED, tdlistIntent);
		}
	  super.finish();
	} 

}
