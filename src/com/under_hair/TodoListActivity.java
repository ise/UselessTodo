package com.under_hair;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListActivity extends Activity// implements OnItemClickListener
{
    private ListView _list;
    private ArrayList<Todo> _curList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list_simple);
        this._list = (ListView) this.findViewById(R.id.todo_list_simple);
        
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
        //this._list.setOnItemClickListener(this);
        this._list.setFocusableInTouchMode(true);
        this._list.setCacheColorHint(Color.TRANSPARENT);
    }
/*
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.i("INFO", "arg0:" + arg0);
        Log.i("INFO", "arg1:" + arg1);
        Log.i("INFO", "arg2:" + arg2);
        TodoDao dao = new TodoDao(this);
        dao.delete(this._curList.get(arg2));
        Log.i("INFO", "delete todo...:id=" + this._curList.get(arg2).getTodoId());
        updateList(dao);
    }
*/
    
    
    class TodoArrayAdapter extends ArrayAdapter {
        private LayoutInflater _inflater;
        private ArrayList<Todo> _todoList;
        private int _textViewResourceId;
        private Hashtable<Integer, Integer> _buttons;
        
        public TodoArrayAdapter(Context context, int textViewResourceId, ArrayList<Todo> items)
        {
            super(context, textViewResourceId, items);
            this._textViewResourceId = textViewResourceId;
            this._todoList = items;
            this._inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this._buttons = new Hashtable<Integer, Integer>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view;
            if(convertView != null){
                view = convertView;
            } else {
                view = this._inflater.inflate(R.layout.todo_item, null);
            }
            Todo todo = this._todoList.get(position);
            ImageButton imgButton = (ImageButton)view.findViewById(R.id.ImageButton01);
            this._buttons.put(new Integer(imgButton.hashCode()), new Integer(position));
            imgButton.setImageResource(R.drawable.delete);
            imgButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TodoDao dao = new TodoDao(TodoListActivity.this);
                    int pos = TodoArrayAdapter.this._buttons.get(new Integer(v.hashCode()));
                    dao.delete(TodoArrayAdapter.this._todoList.get(pos));
                    Log.i("INFO", "delete todo...:id=" + TodoArrayAdapter.this._todoList.get(pos).getTodoId());
                    //Log.i("INFO", "delete todo...:body=" + TodoArrayAdapter.this._todoList.get(pos).getBody());
                    updateList(dao);
                }
            });
            TextView textView = (TextView)view.findViewById(R.id.TodoText);
            textView.setText(todo.getBody());
            
            //’·‰Ÿ‚µ‘Î‰ž
            //view.setLongClickable(true);
            //view.setClickable(true);
            
            return view;
        }
    }
}
