package com.under_hair;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // �f�[�^�x�[�X���̒萔
    private static final String DB_NAME = "USELESS_TODO";
    
    private String[][] datas = new String[][]{
        {"test1"},
        {"test2"},
        {"��邱�ƂS"},
        {"��邱�ƂR"}
    };

    /**
     * �R���X�g���N�^
     */
    public DatabaseOpenHelper(Context context) {
        // �w�肵���f�[�^�x�[�X�������݂��Ȃ��ꍇ�́A�V���ɍ쐬����onCreate()���Ă΂��
        // �o�[�W������ύX�����onUpgrade()���Ă΂��
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        
        try{
            // �e�[�u���̐���
            StringBuilder createSql = new StringBuilder();
            createSql.append("create table " + Todo.TABLE_NAME + " (");
            createSql.append(Todo.COLUMN_ID + " integer primary key autoincrement not null,");
            createSql.append(Todo.COLUMN_BODY + " text not null,");
            createSql.append(Todo.COLUMN_DEL_FLG + " text default '1',");
            createSql.append(Todo.COLUMN_CREATE_DATE + " default CURRENT_TIMESTAMP");
            createSql.append(")");
            db.execSQL(createSql.toString());

            // �T���v���f�[�^�̓���
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
        // �f�[�^�x�[�X�̍X�V
    }
}
