//read by 宋璎航
package net.micode.notes.ui;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, AlarmAlertActivity.class);  
        //启动AlarmAlertActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //activity要存在于activity的栈中，而非activity的途径启动activity时必然不存在一个activity的栈
        //所以要新起一个栈装入启动的activity
        context.startActivity(intent);
    }
}
//这是实现alarm这个功能最接近用户层的包，基于上面的两个包，
//作用还需要深究但是对于setClass和addFlags的
 