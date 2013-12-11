/*
 * class TODOLIST
 * Purpose: Main activity to start the application
 * Provide interface to users 
 * Consume data provided by the Observables
 * Provide a listview interface
 * Use an adapter to bind data to the listview
 *Provide CRUD actions on the data
 *This class remains in memory until the sub-activity comes back
 *check what happens if it placed on suspend
 * Ref: http://www.vogella.com/articles/AndroidSQLite/article.html
 * @author: Wendy-Anne Daniel
 * @date: December 2013
 * @version:1.0
 * @see:com.algonquincollege.dani0004.todolist
 *
 *  
 * 
*/
package com.algonquincollege.dani0004.todolist;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;



import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.view.View;
import android.content.*;

//Reference: http://www.vogella.com/articles/AndroidSQLite/article.html
public class TodoList extends ListActivity  {
	
	private static final String TAG="TodoList:";
	private TodoDataSource tdsource=null;
	private List<TodoModel> tdmodelList=null;
	private TodoModel tdm=null;
	private ArrayAdapter<TodoModel> tdadapter;
	private static int REQUEST_CODE=1;
	private static int REQUEST_CODE_U=2;
	private int listposClicked=-1;
	private final static int ABOUT_DIALOG_ID=10;
	Dialog aboutDialog;
	private static int NO_SELECTED_ITEM=-1;
	private static String PARCELABLE_S="todoModel";
	Toast errorToast;
	TextView selectedListItem;

	/*
	 * OnCreate function
	 * Instantiates the datasource
	 * delegates the opening of the database for read and write
	 * Loads the data for the listview
	 * Instantiates the adapter for the listview and attaches the adapter
	 * to itself
	 * using simple_list_item_1 - uses only 1 list
	 * using simple_list_item_2 - uses 2 list sources from[] and to[]
	 * using simple_list_item_checked - puts a check mark by the side of each item
	 * @param Bundle any saved information that is coming in
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);
		 Log.v(TAG,"EEEin start activity #1 ");
		tdsource=new TodoDataSource(this);
		int rc=tdsource.getReturnCode();
		if(rc==0)
			tdsource.open();
		else
			showToast(R.string.database_error);
		//create the list 
		tdmodelList=tdsource.readTodo(0,null);
		 rc=tdsource.getReturnCode();
		//Bind the adapter to the model
		 if(rc==0){
		 tdadapter = new ArrayAdapter<TodoModel>(this,
		        android.R.layout.simple_list_item_1, tdmodelList);
		//set up a listener for the listview
		 setListAdapter(tdadapter);
		
		 }
		 else
			 showToast(R.string.database_error);
		 
		
	}
	private void showToast(int toastid){
		
		//show a toast message
		// create a Toast displaying error information
         errorToast = Toast.makeText(TodoList.this, 
        		 TodoList.this.getResources().getString(
            toastid), Toast.LENGTH_LONG);
         errorToast.setGravity(Gravity.CENTER, 0, 0);
         errorToast.show(); // show the Toast 
	}
	/*
	 * onCreateOptionsMenu
	 * Inflates the menu which is a fragment provided
	 * by the infrastructure
	 * @param Bundle any saved information that is coming in
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}
	 /* The function onOptionsItemSelected
		 * Called when a menu item is clicked 
		 * @param MenuItem - the item that was clicked
		 * @return boolean - true or false 
		 */
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			switch(item.getItemId()){
			
			case (R.id.action_about):
				aboutDialog=onCreateDialog(ABOUT_DIALOG_ID);
				aboutDialog.show();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
			
			
			}
		}	
	/*
	 * The function onClick
	 * Responds to all the buttons in the menu bar
	 * Add - inflates the CreateTodo activity and adds a row to the 
	 * list
	 * Delete - deletes the selected row from the list
	 * There are 3 states for the delete: list has items, list has only one item
	 * and list is empty
	 * The user is free to tap the delete button at any point in time
	 * 
	 */
	public void onClick(View view)
	{
		
		switch(view.getId())
		{
			case R.id.add:
			//start the add activity
			Intent i = new Intent(this, CreateTodo.class);
			startActivityForResult(i,REQUEST_CODE); 
			break;
			case R.id.delete:
			//get the id from the adapter
			//this will delete the selected item
			if(getListAdapter().getCount() == 1 && listposClicked==NO_SELECTED_ITEM) {
					//need to delete the last item even if it is not selected
				//however prompt the user to warn that a delete will occur
				listposClicked=0;
				showToast(R.string.delete_last);
				doDeleteList();
			
			}
			else
			if (getListAdapter().getCount() > 0) {
				if(listposClicked>=0)
					doDeleteList();
				else
					showToast(R.string.delete_select);
			 
			 }
			if(getListAdapter().getCount() == 0) {
				showToast(R.string.missing_todo_error);
				
			}
			break;
			case R.id.update:
				doUpdateList();
				break;
			
		}//end switch
		//the adapter this is a model that communicates with the listview
		tdadapter.notifyDataSetChanged();
		
	}//end function
	/*
	 * The function doUpdateList 
	 * Handles the different states of the main activity list
	 * The list can be in 3 states: having multiple items, having one item and
	 * empty
	 * The user is free to click the update button at any point in time
	 */
	private void doUpdateList(){
	
		try{
			//check if the user has selected a todo
			if(listposClicked>=0)
			{
				Intent i = new Intent(this, UpdateTodo.class);
				//send the tdm to the update class
				
		        
				tdm = (TodoModel)this.getListAdapter().getItem(listposClicked);
				 i.putExtra(PARCELABLE_S, tdm);
				 
				startActivityForResult(i,REQUEST_CODE_U); 
				
			}
			else
				if(listposClicked==NO_SELECTED_ITEM && getListAdapter().getCount() == 0 )
					showToast(R.string.missing_todo_error);
				else if(listposClicked==NO_SELECTED_ITEM && getListAdapter().getCount() == 1)
				{	
					showToast(R.string.update_last);
					errorToast.setGravity(Gravity.BOTTOM, 0, 0);
					
					
						listposClicked=0;
						Intent i = new Intent(this, UpdateTodo.class);
						//send the tdm to the update class
						
						tdm = (TodoModel)this.getListAdapter().getItem(listposClicked);
						 i.putExtra(PARCELABLE_S, tdm);
						  
						startActivityForResult(i,REQUEST_CODE_U);
						
				}
				else
					if(listposClicked==NO_SELECTED_ITEM)
						showToast(R.string.update_select);
			
			resetSelectedItem();
		
		}
		catch(Exception ee){Log.v(TAG,"Ex in doUpdateList"+ee.getMessage());}
	}
	
	private void resetSelectedItem()
	{
		if(selectedListItem !=null)
		{
			
			selectedListItem.setBackgroundResource(R.layout.default_shape_drawable);
		}
	}
	
	private void doDeleteList(){
		
		try{
			 tdm = (TodoModel)this.getListAdapter().getItem(listposClicked);
		        tdsource.deleteTodo(tdm);
		        int rc=tdsource.getReturnCode();
		        if(rc==0){
			        tdadapter=(ArrayAdapter<TodoModel>)this.getListAdapter();      
			        tdadapter.remove(tdm);
			        //set selected activity to no selected 
			        listposClicked=NO_SELECTED_ITEM;
			       
		        }
		        else 
		        	showToast(R.string.database_error);
			
		        resetSelectedItem();
		
		}
		catch(Exception ee){
			Log.e(TAG,"Ex in doUpdateList"+ee.getMessage());
			showToast(R.string.error_condition);
		}
	}
	
	
	 @Override
	  protected void onResume() {
		 tdsource.open();
	    super.onResume();
	  }

	  @Override
	  protected void onPause() {
		  tdsource.close();
	    super.onPause();
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
		  
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
	    	
			
			 TodoModel data = i.getExtras().getParcelable("todoModel");
			if(data!=null)			
				tdadapter.add(data);
			else
				showToast(R.string.database_error);
			listposClicked=NO_SELECTED_ITEM;	
	      
	    }//END IF
	    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_U) {
	    	
	    	 
	    	 int rc=tdsource.getReturnCode();
		        if(rc==0){
			        tdadapter=(ArrayAdapter<TodoModel>)this.getListAdapter();      
			        tdadapter.remove(tdm);
			       
		        }
			 TodoModel data = i.getExtras().getParcelable("todoModel");
			
			 if(data!=null)
				tdadapter.insert(data,listposClicked);
			 else
				 showToast(R.string.database_error);
			listposClicked=NO_SELECTED_ITEM;
				
	      
	    }//END IF
	  } //END FN
	  /*
		 * The function onListItem
		 * The listener for a click event on a list item
		 * @param - ListView - the listview from where the event originated
		 * @param - int - the item position in the array
		 * @param long - ?
		 */
	  public void onListItemClick(ListView l, View v, int pos, long id)
	  {
		 
			  listposClicked=pos;
		  
		 
		  
		  resetSelectedItem();
		  selectedListItem = (TextView) v;
		  selectedListItem.setBackgroundResource(R.layout.shape_drawable);
	  }//end fn
	  
	  /* The function onCreateDialog
		 * Called when the OK button in the dialog box is clicked 
		 * @param int the id of the dialog that is operative
		 * @return Dialog - the dialog that will be shown
		 */
		
		protected Dialog onCreateDialog(int id){
			
			Builder dialogBuilder=new AlertDialog.Builder(this);
			
			switch (id){
			
			case ABOUT_DIALOG_ID:
				dialogBuilder.setCancelable(false);
				dialogBuilder.setTitle(R.string.action_about);
				
				dialogBuilder.setMessage(R.string.author);
				
				dialogBuilder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int button){
						
						dialog.dismiss();
					}//end onclick
					
				});//end anonymous listener
			}//end switch
			return dialogBuilder.create();
		}//end fn
	  
		
}//end class
