//read by 曾梦媛
package net.micode.notes.gtask.remote;
//异步操作类，实现GTask的异步操作过程
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import net.micode.notes.R;
import net.micode.notes.ui.NotesListActivity;
import net.micode.notes.ui.NotesPreferenceActivity;


public class GTaskASyncTask extends AsyncTask<Void, String, Integer> {
    //同步提示id
    private static int GTASK_SYNC_NOTIFICATION_ID = 5234235;

    public interface OnCompleteListener {
        void onComplete();
    }

    private Context mContext;

    private NotificationManager mNotifiManager;

    private GTaskManager mTaskManager;

    private OnCompleteListener mOnCompleteListener;
    //构造函数实现初始化
    public GTaskASyncTask(Context context, OnCompleteListener listener) {
        mContext = context;
        mOnCompleteListener = listener;
        mNotifiManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mTaskManager = GTaskManager.getInstance();
    }

    public void cancelSync() {
        mTaskManager.cancelSync();
    }
    // 发布进度单位，系统将会调用onProgressUpdate()方法更新这些值
    public void publishProgess(String message) {
        publishProgress(new String[] {
            message
        });
    }
    
    //向用户提示当前同步的状态，是一个用于交互的方法
    private void showNotification(int tickerId, String content) {
        Notification notification = new Notification(R.drawable.notification, mContext
                .getString(tickerId), System.currentTimeMillis());
        notification.defaults = Notification.DEFAULT_LIGHTS;// 调用系统自带灯光
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击清除按钮或点击通知后会自动消失
        PendingIntent pendingIntent;//一个描述了想要启动一个Activity、Broadcast或是Service的意图
        //如果同步不成功，那么从系统取得一个用于启动一个NotesPreferenceActivity的PendingIntent对象
        if (tickerId != R.string.ticker_success) {
            pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                    NotesPreferenceActivity.class), 0);
        //如果同步成功，那么从系统取得一个用于启动一个NotesListActivity的PendingIntent对象
        } else {
            pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,
                    NotesListActivity.class), 0);
        }
        notification.setLatestEventInfo(mContext, mContext.getString(R.string.app_name), content,
                pendingIntent);
        //通过NotificationManager对象的notify（）方法来执行一个notification的消息
        mNotifiManager.notify(GTASK_SYNC_NOTIFICATION_ID, notification);
    }

    //此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间
    @Override
    protected Integer doInBackground(Void... unused) {
        publishProgess(mContext.getString(R.string.sync_progress_login, NotesPreferenceActivity
                .getSyncAccountName(mContext)));//利用getString,将把 NotesPreferenceActivity.getSyncAccountName(mContext))的字符串内容传入sync_progress_login
        return mTaskManager.sync(mContext, this);//进行后台同步具体操作
    }

    //可以使用进度条增加用户体验度。 此方法在主线程执行，用于显示任务执行的进度
    @Override
    protected void onProgressUpdate(String... progress) {
        showNotification(R.string.ticker_syncing, progress[0]);
        if (mContext instanceof GTaskSyncService) {//instanceof 判断mContext是否是GTaskSyncService的实例
            ((GTaskSyncService) mContext).sendBroadcast(progress[0]);
        }
    }

    //相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI
    @Override
    protected void onPostExecute(Integer result) {//用于在执行完后台任务后更新UI,显示结果 
        //几种不同情况下的结果显示
        if (result == GTaskManager.STATE_SUCCESS) {
            showNotification(R.string.ticker_success, mContext.getString(
                    R.string.success_sync_account, mTaskManager.getSyncAccount()));
            NotesPreferenceActivity.setLastSyncTime(mContext, System.currentTimeMillis());//设置最新同步的时间
        } else if (result == GTaskManager.STATE_NETWORK_ERROR) {
            showNotification(R.string.ticker_fail, mContext.getString(R.string.error_sync_network));
        } else if (result == GTaskManager.STATE_INTERNAL_ERROR) {
            showNotification(R.string.ticker_fail, mContext.getString(R.string.error_sync_internal));
        } else if (result == GTaskManager.STATE_SYNC_CANCELLED) {
            showNotification(R.string.ticker_cancel, mContext
                    .getString(R.string.error_sync_cancelled));
        }
        if (mOnCompleteListener != null) {
            new Thread(new Runnable() {

                public void run() {//完成后的操作，使用onComplete()将所有值都重新初始化，相当于完成一次操作
                    mOnCompleteListener.onComplete();
                }
            }).start();
        }
    }
}
