/*
 * class TODODATASOURCE
 * Purpose: Create the database controller
 * Provide the data to consumers via CRUD
 * Receive data from the DatabaseModel
 * This is the interface between the consumers and data providers
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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoDataSource {
	
	private static final String TAG="TodoDataSource";
	private DatabaseController dbcontroller;
	private SQLiteDatabase db;
	private String SQLquery="";
	private String[] resultSet = { "todoId","todo"};
	private static final String TODOID="todoId";
	private static final String TODOTXT="todo";
	private static final String TODOTBLNAME="todolist";
	private static int returnCode=0;
	
			
	
	/* one arg constructor
	 * @param context the application context 
	 */
	public TodoDataSource(Context context)
	{
		//create the controller
		dbcontroller=new DatabaseController(context);
		
		open();
		dbcontroller.onCreate(db);
		returnCode=dbcontroller.getReturnCode();
		
		//create the model
	
	}
	/*
	 * return code gives the error state of the 
	 * data model and its providers
	 */
	public int getReturnCode(){
		return returnCode;
	}
	/* The function open 
	 * Opens the database for read and write
	 * @param 
	 * @return void
	 */
	public void open()
	{
		
		db = dbcontroller.getWritableDatabase();
		
	
	}
	/* The function close
	 * Closes the database
	 * @param 
	 * @return void
	 */
	public void close()
	{
		dbcontroller.close();
	}
	
	/* The function createTodo
	 * C of CRUD creates a row in the table
	 * @param String - the todo to be inserted
	 * @return TodoModel - the data model
	 */
	public TodoModel createTodo(String todoText)
	{
		TodoModel tmodel=null; long insertId=0;
		try{
			
			Log.i(TAG,"Start create ");
			Log.i(TAG,"todo is  "+todoText);
			//use content values to store name-value pairs
			 ContentValues values = new ContentValues();
			 
			 values.put(TODOTXT, todoText);
			 insertId = db.insert(TODOTBLNAME, 
									 null, 
									 values);
			 int a=(int)insertId;
			 Log.i(TAG,"successful insert todoId is "+a);
			 
			//update the model
			//the latest insert will be the topmost row in the cursor
			
			//only one row will be returned in the cursor
			 Log.i(TAG,"updating model ");
			 Cursor results = db.query(TODOTBLNAME,
						new String[]{TODOID,TODOTXT},//columns to return
						"todoId="+a, //where clause excludes the where
						null, //conditions for filtering where
						null, //group by
						null, //filter for groups
						null //order by
						);
				
			 
			 results.moveToFirst();
			  tmodel = sendResultsToModel(results);
			 results.close();
			 Log.i(TAG,"end Create "); 
		}
		catch(Exception e){Log.v(TAG,"ex in CreateTodoList "+e.getMessage());}
		
		return tmodel;
	}
	/* The function readTodo
	 * Return all rows with todoId and todo in the cursor
	 * does a push of data to the model
	 * Updates the array list
	 * @param int search by primary key
	 * @param String search by text
	 * @return List<TodoModel>
	 */
	public List<TodoModel> readTodo(int id,String text)
	{
			TodoModel tmodel=null;
			List<TodoModel> tmodels = new ArrayList<TodoModel>();
			SQLquery="";
		try{
			//do not use raw queries
		
			Cursor results = db.query(TODOTBLNAME,
					new String[]{TODOID,TODOTXT},//columns to return
					null, //where clause
					null, //conditions for filtering where
					null, //group by
					null, //filter for groups
					null //order by
					);
			Log.i(TAG, "select success ");
			
			//populate the database model
			results.moveToFirst();
			while (!results.isAfterLast()) {
			     tmodel = sendResultsToModel(results);
			     tmodels.add(tmodel);
			     results.moveToNext();
			}
			results.close();
			
		}
		catch(Exception e){
			Log.v(TAG,"ex in CreateTodoList "+e.getMessage());
			returnCode=2;
		}
		
		return tmodels;
	}
	/* for sqlite update is insert and delete
	 * Update the model with this latest row after the insert/delete
	 * @param 
	 * @return void
	 */
	public TodoModel updateTodo(String todoText,TodoModel tm)
	{
		TodoModel tmodel=null;
		try{
			Log.i(TAG,"start update todo");
			Log.i(TAG,"todo text is "+todoText);
			tmodel=createTodo(todoText);
			Log.i(TAG,"after create new todo ");
			if(tmodel!=null) //if row was inserted
			{
				//delete the identified row
				Log.i(TAG,"deleting todo ");
				
				deleteTodo(tm);
			}
			//update the array list
			readTodo(0,null);
			Log.i(TAG,"end update todo"); 
		}
		catch(Exception e){Log.v(TAG,"ex in CreateTodoList "+e.getMessage());}
		return tmodel;
	}
	/* Update the ArrayList after the delete
	 * @param 
	 * @return void
	 */
	public List<TodoModel> deleteTodo(TodoModel tdm)
	{
		Log.i(TAG,"start delete todo "); 
		Log.i(TAG,"todo model to delete is  "+tdm.toString());
		List<TodoModel> tmodels = new ArrayList<TodoModel>();
		try{
			long id = tdm.getTodoId();
		   
		    db.delete(TODOTBLNAME, 
		    			TODOID + " = " + id, 
		    			null);
			Log.i(TAG, "delete success ");
			
			//update the array list
			tmodels=readTodo(0,null);
			Log.i(TAG,"after updating array list ");
			Log.i(TAG,"end delete todo ");
		}
		catch(Exception e){
			Log.v(TAG,"ex in DeleteTodo "+e.getMessage());
			returnCode=3;
		}
		return tmodels;
	}
	/* the function sendResultsToModel
	 * Updates the model after the create
	 * reference the columns by index getInt(0) - first column
	 * getString(1) second column 
	 * this cursor has only one row
	 * @return - TodoModel the current row
	 * */
	 
	private TodoModel sendResultsToModel(Cursor results)
	{
		TodoModel tmodel=null;
		try{
			 tmodel = new TodoModel();
			tmodel.setTodoId(results.getInt(0));
			tmodel.setTodo(results.getString(1));
		    
		}
		catch(Exception e){Log.v(TAG,"ex in sendResultsToModel "+e.getMessage());}
		return tmodel;
	}
	
	

}
