package com.under_hair;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class TodoArrayAdapter extends ArrayAdapter {
    
    private LayoutInflater _inflater;
    private ArrayList<Todo> _todoList;
    private int _textViewResourceId;
    
    public TodoArrayAdapter(Context context, int textViewResourceId, ArrayList<Todo> items)
    {
        super(context, textViewResourceId, items);
        this._textViewResourceId = textViewResourceId;
        this._todoList = items;
        this._inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        imgButton.setImageResource(R.drawable.delete);
        imgButton.setClickable(true);
        TextView textView = (TextView)view.findViewById(R.id.TodoText);
        textView.setText(todo.getBody());
        
        //’·‰Ÿ‚µ‘Î‰ž
        //view.setLongClickable(true);
        view.setClickable(true);
        
        return view;
    }
}
