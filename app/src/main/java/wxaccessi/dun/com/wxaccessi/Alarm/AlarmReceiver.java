package wxaccessi.dun.com.wxaccessi.Alarm;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import wxaccessi.dun.com.wxaccessi.WXaccessibilityService.weiCahtAutoReplyService;
import wxaccessi.dun.com.wxaccessi.WXstarts;

/**
 * Created by Administrator on 2017/4/11.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm","开始运行定时任务！");
        wakeUpAndUnlock(context); //唤醒屏幕并解锁
        //ToastUtils.toastShow(context,"开始运行定时任务！",ToastUtils.LENGTH_LONG);

        Intent startIntent = new Intent(context, weiCahtAutoReplyService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("endStarts", true);
        startIntent.putExtras(bundle);
        context.startService(startIntent);

    }
    public static void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }
}