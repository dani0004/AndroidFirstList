/*
 * class TODOMODEL
 * Purpose: Store data by row
 * Provide the data to consumers 
 * Receive data from the DatabaseSource
 * Package and unpackage itself as a serializable object
 * to transfer data between activities
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



import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class TodoModel implements Parcelable {
	
	private int todoId;
	private String todo;
	private ArrayList<TodoModel> tmodels;
	/*
	 * No argument constructor
	 */
	public TodoModel()
	{
		
	}
	/*
	 * 2 argument constructor
	 * @param int the todoId
	 * @param String the todo text
	 */
	public TodoModel(int a, String text)
	{
		this.todoId=a;
		this.todo=text;
	}
	
	 public int getTodoId() {
		    return todoId;
	  }

	  public void setTodoId(int id) {
		    this.todoId = id;
	 }

	  public String getTodo() {
		    return todo;
		}

	 public void setTodo(String text) {
		    this.todo = text;
		}

		  // Will be used by the ArrayAdapter in the ListView
		  @Override
	 public String toString() {
		    return todo;
		  }
		  
	  public ArrayList<TodoModel> getTodoList() {
			    return tmodels;
			}

		 public void setTodoList(ArrayList<TodoModel> tt) {
			    this.tmodels = tt;
			}
		 
		
		    /********** Parcelable interface **********/
		 /* This is used to serialize this object and transfer
		  * it with the intent when 2 activities are communicating
		  */
		   

		    @Override
		    public int describeContents() { 
		        return 0;
		    }

		    @Override
		    public void writeToParcel(Parcel out, int flags) 
		    {
		        out.writeString(String.valueOf(todoId));
		        out.writeString(todo);
		    }

		    private static TodoModel readFromParcel(Parcel in) { 
		        String id = in.readString();
		        int pp=Integer.parseInt(id);
		        
		        String text = in.readString();
		        return new TodoModel(pp, text);
		    }

		    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() 
		   {
		        public TodoModel createFromParcel(Parcel in) 
		       {
		            return readFromParcel(in);
		        }

		        public TodoModel[] newArray(int size) { 
		            return new TodoModel[size];
		        }
		    };
		    
		    /** END PARCELABLE INTERFACE **************/

}
