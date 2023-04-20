package net.micode.notes.ui;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import net.micode.notes.R;

public class DropdownMenu {
    private Button mButton;
    private PopupMenu mPopupMenu;
    //声明一个下拉菜单
    private Menu mMenu;

    public DropdownMenu(Context context, Button button, int menuId) {
        mButton = button;
        mButton.setBackgroundResource(R.drawable.dropdown_icon);
        //设置这个view的背景
        mPopupMenu = new PopupMenu(context, mButton);
        mMenu = mPopupMenu.getMenu();
        mPopupMenu.getMenuInflater().inflate(menuId, mMenu);
        //MenuInflater是用来实例化Menu目录下的Menu布局文件
        //根据ID来确认menu的内容选项
        mButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mPopupMenu.show();
            }
        });
    }

    public void setOnDropdownMenuItemClickListener(OnMenuItemClickListener listener) {
        if (mPopupMenu != null) {
            mPopupMenu.setOnMenuItemClickListener(listener);
        }//设置菜单的监听
    }

    public MenuItem findItem(int id) {
        return mMenu.findItem(id);
    }//对于菜单选项的初始化，根据索引搜索菜单需要的选项

    public void setTitle(CharSequence title) {
        mButton.setText(title);
    }//布局文件，设置标题
}