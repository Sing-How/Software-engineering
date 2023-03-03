//read by 宋璎航
package net.micode.notes.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.NoteColumns;


public class AlarmInitReceiver extends BroadcastReceiver {

    private static final String [] PROJECTION = new String [] {
            NoteColumns.ID,
            NoteColumns.ALERTED_DATE
    };
    //对数据库的操作，调用标签ID和闹钟时间
    private static final int COLUMN_ID                = 0;
    private static final int COLUMN_ALERTED_DATE      = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        long currentDate = System.currentTimeMillis();
        //System.currentTimeMillis()产生一个当前的毫秒
        //这个毫秒其实就是自1970年1月1日0时起的毫秒数
        Cursor c = context.getContentResolver().query(Notes.CONTENT_NOTE_URI,
                PROJECTION,
                NoteColumns.ALERTED_DATE + ">? AND " + NoteColumns.TYPE + "=" + Notes.TYPE_NOTE,
                new String[] { String.valueOf(currentDate) },
                //将long变量currentDate转化为字符串
                null);
        //Cursor在这里的作用是通过查找数据库中的标签内容，找到和当前系统时间相等的标签

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    long alertDate = c.getLong(COLUMN_ALERTED_DATE);
                    Intent sender = new Intent(context, AlarmReceiver.class);
                    sender.setData(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, c.getLong(COLUMN_ID)));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sender, 0);
                    AlarmManager alermManager = (AlarmManager) context
                            .getSystemService(Context.ALARM_SERVICE);
                    alermManager.set(AlarmManager.RTC_WAKEUP, alertDate, pendingIntent);
                } while (c.moveToNext());
            }
            c.close();
        }
        //然而通过网上查找资料发现，对于闹钟机制的启动，通常需要上面的几个步骤
        //如新建Intent、PendingIntent以及AlarmManager等
        //这里就是根据数据库里的闹钟时间创建一个闹钟机制
    }
}