package com.under_hair;

import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;

public class TodoListActivity extends Activity implements OnItemClickListener
{
    private TodoListView _list;
    //private ListView _list;
    private ArrayList<Todo> _curList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list);
        //setContentView(R.layout.todo_list_simple);
        this._list = (TodoListView) this.findViewById(R.id.todo_list);
        //this._list = (ListView) this.findViewById(R.id.todo_list_simple);
        //this._list.setSortMode(true);
        
        final TodoDao dao = new TodoDao(this);
        updateList(dao);
        final EditText inputForm = (EditText) this.findViewById(R.id.todo_input);
        Button addBtn = (Button) this.findViewById(R.id.add_button);
        addBtn.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //close keyboard
                        InputMethodManager inputMethodManager = 
                            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        
                        //save todo item
                        String body = inputForm.getText().toString();
                        dao.save(new Todo(body));
                        inputForm.setText("");
                        updateList(dao);
                    }
                }
            );
    }
    
    public void updateList(TodoDao dao) {
        this._curList = dao.getList();
        ArrayList<String> strList = new ArrayList<String>();
        for (Todo todo:this._curList) {
            strList.add(todo.getBody());
        }
        this._list.setAdapter(new TodoArrayAdapter(this, R.layout.todo_item, this._curList));
        //this._list.setAdapter(new ArrayAdapter(this, R.layout.todo_item_simple, strList));
        this._list.setOnItemClickListener(this);
        this._list.setFocusableInTouchMode(true);
        this._list.setCacheColorHint(Color.TRANSPARENT);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (arg0 == this._list) {
            /*
            TodoDao dao = new TodoDao(this);
            dao.delete(this._curList.get(arg2));
            Log.i("INFO", "delete todo...:id=" + this._curList.get(arg2).getTodoId());
            updateList(dao);
            */
        }
        Log.i("INFO", "arg2:" + arg2);
    }
}
