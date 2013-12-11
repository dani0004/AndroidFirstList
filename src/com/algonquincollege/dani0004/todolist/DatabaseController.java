/*
 * class DATABASECONTROLLER
 * Purpose: Create, drop and update the SQLite database
 * based on the SQLiteHelper class in Lars Vogel's tutorial
 * Created by the DataSource based on the context
 * Ref: http://www.vogella.com/articles/AndroidSQLite/article.html
 * @author: Wendy-Anne Daniel
 * @date: December 2013
 * @version:1.0
 * @see:com.algonquincollege.dani0004.todolist
 * @see: android.database.sqlite.SQLiteOpenHelper
 *  
 * 
*/
package com.algonquincollege.dani0004.todolist;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseController extends SQLiteOpenHelper {

	private static final String TAG="DBController";
	//Define the database 
	private static final String DATABASE_NAME="todolist";
	private static final int DATABASE_VERSION=1;
	private String SQLquery="";
	private int returncode=0;
	
	/* One argument constructor
	 * @param Context - the application namespace
	 * each database is created in each applications namesapce
	 */
	public DatabaseController(Context context) 
	{
		//create the db context
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }
	
	/*
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	  public void onCreate(SQLiteDatabase database) {
		
	try{
		SQLquery="Create table if not exists todolist (todoId integer primary key autoincrement,";
		SQLquery+="todo text not null);";
		
			database.execSQL(SQLquery);
	
		}
	    
	    catch(Exception e){
	    	Log.v(TAG,"exception in oncreate table "+e.getMessage());
	    	returncode=1;
	    }
		
	  }
	/*
	 * Returncodes will give the state of the database
	 * RETURNCODE=0 - ALL GOOD
	 * RETURNCODE=1 - table could not be created
	 */
	public int getReturnCode()
	{
		return returncode;
	}
	/*
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase)
	 */

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(DatabaseController.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    SQLquery="DROP TABLE IF EXISTS todolist ";
	    db.execSQL(SQLquery);
	    onCreate(db);
	  } 
}
