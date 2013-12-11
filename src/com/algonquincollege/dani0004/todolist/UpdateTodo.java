package com.algonquincollege.dani0004.todolist;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;


public class UpdateTodo extends Activity {
	private static final String TAG="UpdateTodo:";
	private TodoDataSource tdsource=null;
	private List<TodoModel> tdmodelList=null;
	private TodoModel tdm=null;
	private ArrayAdapter<TodoModel> tdadapter;
	private static String PARCELABLE_S="todoModel";
	private int CANCELLED=0;
	public static int RESULT_CANCELLED=1;
	private String selectedText="";
	private static int REQUEST_CODE=1;
	
	/* onCreate
	 * creates an instance of the datasource
	 * delegates the open of the database
	 * @param Bundle - the data that is sent to the activity
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_todo);
		tdsource=new TodoDataSource(this);
		tdsource.open();
		
		Bundle bb=getIntent().getExtras();
		if(bb==null) return;
		tdm = getIntent().getExtras().getParcelable("todoModel");
		//get the text from the todo model
		if(tdm!=null)
			selectedText=tdm.getTodo();
		else
			throw new Exception("No todoModel received from todoList");
	 
		
		 addSelectedText();	
		
		}
		catch(Exception ee){Log.i(TAG,ee.getMessage());}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_todo, menu);
		return true;
	}
	
	//Action listeners
		/* onUpdateTodo
		 * collects the data from the interface
		 * validates it
		 * if valid, delegates the creation of a row in the database
		 * calls finish() to return back to the main activity
		 * If invalid, shows a message to the user
		 * @param View - the view that generated the action
		*/
		public void onUpdateTodo(View view)
		{
			try
			{
				CANCELLED=0;
				EditText et=(EditText)(findViewById(R.id.updateTodo_txt));
				String etentry=et.getText().toString();
				etentry=etentry.trim();
				Log.v(TAG,"RRRRRRRUpdating txt is "+etentry);
				if(etentry.length()>0){
					tdm=tdsource.updateTodo(etentry,tdm);
					Log.v(TAG, "before calling finish in update");
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
		
		
				/* addSelectedText
				 * collects the data from the interface
				 * validates it
				 * if valid, delegates the creation of a row in the database
				 * calls finish() to return back to the main activity
				 * If invalid, shows a message to the user
				 * @param View - the view that generated the action
				*/
				public void addSelectedText()
				{
					try
					{
						//get the text to be updated from the intent
						TextView et=(TextView)(findViewById(R.id.selectedText));
						et.setText(selectedText);
						
				
					}
					catch(Exception e){
						Log.e(TAG,"Exception in addSelectedText "+e.getMessage());
						sendToast(R.string.error_condition);
					}
					
				}
				
		/*
		 * The function onActivityResult
		 * called when the sub-activity returns  back to the main activity
		 * Gets the parcelable data from the intent and adds it to the adapter
		 * @param int - the request code associated with this activity
		 * @param int resultcode - the result code associated with what the sub
		 * was doing 
		 */
		  @Override
		  protected void onActivityResult(int requestCode, int resultCode, Intent i) {
			  Log.v(TAG,"EEEEEEEEstart onactivityresult ");
		    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
		
					 tdm = i.getExtras().getParcelable("todoModel");
						//get the text from the todo model
					 selectedText=tdm.getTodo();
					 
					 Log.v(TAG,"!!!SELECTED TEXT IS "+selectedText);
					 addSelectedText();	
						
			      
		    }//END IF
	    } //END FN
		  
		  private void sendToast(int toastid)
			{
				//show a toast message
				// create a Toast displaying error information
		         Toast errorToast = Toast.makeText(UpdateTodo.this, 
		        		 UpdateTodo.this.getResources().getString(
		        				 toastid), Toast.LENGTH_LONG);
		         errorToast.setGravity(Gravity.CENTER, 0, 0);
		         errorToast.show(); // show the Toast 
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
