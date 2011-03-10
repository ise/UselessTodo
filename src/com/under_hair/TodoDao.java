package com.under_hair;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TodoDao {
    private DatabaseOpenHelper _doh;
    public TodoDao(Context context) {
        this._doh = new DatabaseOpenHelper(context);
    }
    
    public Todo save(Todo todo) {
        SQLiteDatabase db = this._doh.getWritableDatabase();
        Todo result = null;
        try {
            ContentValues values = new ContentValues();
            values.put(Todo.COLUMN_BODY, todo.getBody());
            Long id = db.insert(Todo.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
        return result;
    }
    
    public void delete(Todo todo) {
        SQLiteDatabase db = this._doh.getWritableDatabase();
        try {
            db.delete(Todo.TABLE_NAME, Todo.COLUMN_ID + "=?", new String[]{ String.valueOf(todo.getTodoId())});
        } finally {
            db.close();
        }
    }
    
    public ArrayList<Todo> getList() {
        SQLiteDatabase db = this._doh.getReadableDatabase();
        ArrayList<Todo> resultList = new ArrayList<Todo>();
        try {
            Cursor cursor = db.query(Todo.TABLE_NAME, null, null, null, null, null, Todo.COLUMN_ID);
            cursor.moveToFirst();
            while( !cursor.isAfterLast()){
                resultList.add(this._setTodoInfo(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } finally {
            db.close();
        }
        return resultList;
    }
    
    private Todo _setTodoInfo(Cursor cursor) {
        Todo todo = new Todo();
        todo.setTodoId(cursor.getLong(0));
        todo.setBody(cursor.getString(1));
        todo.setDelFlg(cursor.getString(2));
        todo.setCreateDate(cursor.getString(3));
        return todo;
    }
    
}
