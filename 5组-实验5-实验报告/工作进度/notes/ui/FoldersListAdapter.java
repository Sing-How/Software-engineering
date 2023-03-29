package net.micode.notes.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.NoteColumns;


public class FoldersListAdapter extends CursorAdapter {
    //CursorAdapter是Cursor和ListView的接口
    //FoldersListAdapter继承了CursorAdapter的类
    //主要作用是便签数据库和用户的交互
    //这里就是用folder（文件夹）的形式展现给用户
    public static final String [] PROJECTION = {
            NoteColumns.ID,
            NoteColumns.SNIPPET
    };//调用数据库中便签的ID和片段

    public static final int ID_COLUMN   = 0;
    public static final int NAME_COLUMN = 1;

    public FoldersListAdapter(Context context, Cursor c) {
        super(context, c);
        // TODO Auto-generated constructor stub
    }//数据库操作

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //ViewGroup是容器
        return new FolderListItem(context);
    }//创建一个文件夹，对于各文件夹中子标签的初始化

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (view instanceof FolderListItem) {
            String folderName = (cursor.getLong(ID_COLUMN) == Notes.ID_ROOT_FOLDER) ? context
                    .getString(R.string.menu_move_parent_folder) : cursor.getString(NAME_COLUMN);
            ((FolderListItem) view).bind(folderName);
        }
    }//将各个布局文件绑定起来

    public String getFolderName(Context context, int position) {
        Cursor cursor = (Cursor) getItem(position);
        return (cursor.getLong(ID_COLUMN) == Notes.ID_ROOT_FOLDER) ? context
                .getString(R.string.menu_move_parent_folder) : cursor.getString(NAME_COLUMN);
    }//根据数据库中标签的ID得到标签的各项内容

    private class FolderListItem extends LinearLayout {
        private TextView mName;

        public FolderListItem(Context context) {
            super(context);
            //操作数据库
            inflate(context, R.layout.folder_list_item, this);
            //根据布局文件的名字等信息将其找出来
            mName = (TextView) findViewById(R.id.tv_folder_name);
        }

        public void bind(String name) {
            mName.setText(name);
        }
    }

}