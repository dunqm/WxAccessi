package wxaccessi.dun.com.wxaccessi;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wxaccessi.dun.com.wxaccessi.Alarm.AlarmReceiver;
import wxaccessi.dun.com.wxaccessi.WXaccessibilityService.*;
import wxaccessi.dun.com.wxaccessi.bean.AdressBean;
import wxaccessi.dun.com.wxaccessi.wiget.ToastUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/4/15.
 */

public class WXstarts extends Service{
    private int action;
    private boolean starts = false;
    private MapLocation Location = new MapLocation();
    private settingAccessibility SettingAccessibility;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("WXstarts","数据准备接收。");
        action = intent.getIntExtra("action",0);
        if(((action != 3) && (action != 4) && (action != 101)) && starts){
            Log.e("WXstarts","不能同时开始两个服务。");
            ToastUtils.toastShow(this, "不能同时开始两个服务。", ToastUtils.LENGTH_SHORT);
        }else {
            switch (action) {
                case 1:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("RobotService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("RobotService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("RobotService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        String wxInput;
                        String wxSex = "gb";
                        int twice = 10;
                        List<AdressBean> mapBeen;
                        Log.e("WXstarts", "定点加粉启动中...");
                        twice = intent.getIntExtra("Twice", 10);
                        wxInput = intent.getStringExtra("wxInput");
                        wxSex = intent.getStringExtra("wxSex");
                        starts = intent.getBooleanExtra("starts", false);
                        ArrayList<String> mapLat = intent.getStringArrayListExtra("mapLat");
                        ArrayList<String> mapLog = intent.getStringArrayListExtra("mapLog");
                        mapBeen = mapbeen_(mapLat, mapLog);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent intent_post = new Intent(WXstarts.this, RobotService.class);
                        Bundle bundle_post = new Bundle();
                        ArrayList mapBeen_ = new ArrayList();//这个arraylist是可以直接在bundle里传的，所以我们可以借用一下它的功能
                        mapBeen_.add(mapBeen);
                        bundle_post.putParcelableArrayList("mapBeen", mapBeen_);
                        bundle_post.putString("wxInput", wxInput);
                        bundle_post.putString("wxSex", wxSex);
                        bundle_post.putInt("Twice", Integer.valueOf(twice));
                        bundle_post.putBoolean("starts", true);
                        intent_post.putExtras(bundle_post);
                        startService(intent_post);
                        //startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;
                case 2:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtMessService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.weiCahtMessService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtMessService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        String getText;
                        String getTime;
                        Log.e("WXstarts", "微信群自动发消息启动中...");
                        getText = intent.getStringExtra("wx_text");
                        getTime = intent.getStringExtra("wx_time");
                        starts = intent.getBooleanExtra("starts", false);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, weiCahtMessService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("wx_text", getText);
                        bundle.putString("wx_time", getTime);
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        //startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 3:
                    Log.e("WXstarts","服务停止！");
                    starts = intent.getBooleanExtra("starts", false);
                    break;

                case 4:
                    Log.e("WXstarts","虚拟定位");
                    double latitude = intent.getDoubleExtra("latitude",31.3029742);
                    double longitude = intent.getDoubleExtra("longitude",120.6097126);
                    Log.e("WXstarts","虚拟定位：x:" + latitude + " ,y:" + longitude);
                    Location.setxy(latitude,longitude);
                    break;

                case 5:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtAddFriendsService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.weiCahtAddFriendsService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtAddFriendsService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        String wx_qName;
                        String wx_sendInfo;
                        Log.e("WXstarts", "微信群加好友息启动中...");
                        wx_qName = intent.getStringExtra("wx_qName");
                        wx_sendInfo = intent.getStringExtra("wx_sendInfo");
                        starts = intent.getBooleanExtra("starts", false);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, weiCahtAddFriendsService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("wx_qName", wx_qName);
                        bundle.putString("wx_sendInfo", wx_sendInfo);
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        //startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 6:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtAutoReplyService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.weiCahtAutoReplyService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtAutoReplyService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        String settime;
                        int endtime;
                        ArrayList<String> listmessage=new ArrayList<>();
                        Log.e("WXstarts", "微信自动回复启动中...");
                        settime=intent.getStringExtra("timemessage");
                        listmessage = intent.getStringArrayListExtra("listmessage");
                        starts = intent.getBooleanExtra("starts", false);
                        endtime = intent.getIntExtra("endTime",0);
                        if(endtime > 0){ //如果有结束时间则设置结束任务,单位为分钟
                            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent i = new Intent(this, AlarmReceiver.class);
                            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                            manager.set(AlarmManager.RTC_WAKEUP ,System.currentTimeMillis() + 1000 * 60 * endtime, pi);
                        }
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, weiCahtAutoReplyService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("timemessage", settime);
                        bundle.putStringArrayList("listmessage",listmessage);
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 7:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.WeChatFriendsCircleService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.WeChatFriendsCircleService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.WeChatFriendsCircleService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        Log.e("WXstarts", "微信好友验证启动中...");
                        starts = intent.getBooleanExtra("starts", false);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, WeChatFriendsCircleService.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 8:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtFriendsMessService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.weiCahtFriendsMessService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.weiCahtFriendsMessService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        Log.e("WXstarts", "微信好友群发启动中...");
                        starts = intent.getBooleanExtra("starts", false);
                        String getText = intent.getStringExtra("wx_text");
                        int getStartFID = intent.getIntExtra("start_text", 1);
                        int getEndFID = intent.getIntExtra("end_text", 2);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, weiCahtFriendsMessService.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("wx_text", getText);
                        bundle.putInt("start_text", getStartFID);
                        bundle.putInt("end_text", getEndFID);
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 9:
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.WeiChatBoomFriendsService")) {
                        Log.e("WXstarts","尝试开启无障碍功能--");
                        //SettingAccessibility.settingAccessibility_("WXaccessibilityService.WeiChatBoomFriendsService");
                    }
                    if(!SettingAccessibility.isAccessibilitySettingsOn("WXaccessibilityService.WeiChatBoomFriendsService")){
                        Log.e("WXstarts","未开启无障碍功能！");
                        ToastUtils.toastShow(this, "未开启无障碍功能！", ToastUtils.LENGTH_SHORT);
                        Intent dialogIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }else {
                        Log.e("WXstarts", "微信通讯录爆粉启动中...");
                        starts = intent.getBooleanExtra("starts", false);
                        Log.e("WXstarts", "数据接收完毕，准备发送。");

                        Intent startIntent = new Intent(WXstarts.this, WeiChatBoomFriendsService.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("starts", true);
                        startIntent.putExtras(bundle);
                        startService(startIntent);
                        Log.e("WXstarts", "数据发送完毕。");
                    }
                    break;

                case 101:
                    int versionCode=1;
                    Log.e("WXstarts", "版本号查询 : " + versionCode);
                    String classname=intent.getStringExtra("classname");;
                    Log.e("WXstarts", "数据接收完毕，准备发送。");

                    Intent startIntent = new Intent(classname);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("versionInfo",true);
                    bundle.putInt("versionCode",versionCode);
                    startIntent.putExtras(bundle);
                    startActivity(startIntent);
                    Log.e("WXstarts", "数据发送完毕。");
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private ArrayList<AdressBean> mapbeen_(ArrayList<String> mapLat,ArrayList<String> mapLog){
        ArrayList<AdressBean> mmapBeen= new ArrayList();
        for(int i=0;i<mapLat.size();i++){
            AdressBean mapBeen=new AdressBean(mapLat.get(i),mapLog.get(i));
            mmapBeen.add(mapBeen);
        }
        return mmapBeen;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SettingAccessibility = new settingAccessibility(getApplicationContext());

        Intent notificationIntent = new Intent(this, WXstarts.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("新消息")
                .setContentTitle("微信全自动操作服务")
                .setContentText("请勿关闭此服务")
                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setContentInfo("服务");
        Notification notification = builder.build();;
        //把该service创建为前台service
        startForeground(1, notification);


        Log.e("WXstarts","虚拟定位配置启动中--");
        Location.inilocation(WXstarts.this);
        Location.iniMap();
        Log.e("WXstarts","虚拟定位启动成功！");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
