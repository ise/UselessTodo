package com.under_hair;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // データベース名の定数
    private static final String DB_NAME = "USELESS_TODO";
    
    private String[][] datas = new String[][]{
        {"test1"},
        {"test2"},
        {"やること４"},
        {"やること３"}
    };

    /**
     * コンストラクタ
     */
    public DatabaseOpenHelper(Context context) {
        // 指定したデータベース名が存在しない場合は、新たに作成されonCreate()が呼ばれる
        // バージョンを変更するとonUpgrade()が呼ばれる
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        
        try{
            // テーブルの生成
            StringBuilder createSql = new StringBuilder();
            createSql.append("create table " + Todo.TABLE_NAME + " (");
            createSql.append(Todo.COLUMN_ID + " integer primary key autoincrement not null,");
            createSql.append(Todo.COLUMN_BODY + " text not null,");
            createSql.append(Todo.COLUMN_DEL_FLG + " text default '1',");
            createSql.append(Todo.COLUMN_CREATE_DATE + " default CURRENT_TIMESTAMP");
            createSql.append(")");
            db.execSQL(createSql.toString());

            // サンプルデータの投入
            for( String[] data: datas){
                ContentValues values = new ContentValues();
                values.put(Todo.COLUMN_BODY, data[0]);
                db.insert(Todo.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースの更新
    }
}
