package com.under_hair;

import java.io.Serializable;

public class Todo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8127318708982715593L;
    
    //table
    public static final String TABLE_NAME = "todo";
    
    //column
    public static final String COLUMN_ID = "todo_id";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_DEL_FLG = "del_flg";
    public static final String COLUMN_CREATE_DATE = "create_date";
    
    private Long todoId = null;
    private String body = null;
    private String delFlg = null;
    private String createDate = null;
    
    public Todo() {
        
    }
    
    public Todo(String body) {
        setBody(body);
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    
    public Long getTodoId() {
        return todoId;
    }
    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getDelFlg() {
        return delFlg;
    }
    public void setDelFlg(String delFlg) {
        this.delFlg = delFlg;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getBody());
        builder.append(":");
        builder.append(getCreateDate());
        return builder.toString();
    }
    
}
