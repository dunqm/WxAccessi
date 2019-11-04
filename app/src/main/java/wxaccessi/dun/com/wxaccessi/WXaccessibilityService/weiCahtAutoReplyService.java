package wxaccessi.dun.com.wxaccessi.WXaccessibilityService;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 作者 by K
 * 时间：on 2017/2/23 10:22
 * 邮箱 by yingzikeji@qq.com
 * <p>
 * 类用途：
 * 最后修改：
 */
public class weiCahtAutoReplyService extends AccessibilityService {
    private final static String MM_PNAME = "com.tencent.mm";
    boolean hasAction = false;

    public static weiCahtAutoReplyService app;
    boolean locked = false;
    boolean background = false;
    private String name;
    private String scontent;
    AccessibilityNodeInfo itemNodeinfo;
    private KeyguardManager.KeyguardLock kl;
    String message=null;
    String settime=null;
    Long mills;
    private Handler handler = new Handler();
    Random mRandom=new Random();
    ArrayList<String> listmessage=new ArrayList<>();
    private boolean starts = false;
    private boolean isNO1 = true;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra("endStarts",false)){
            endStarts();
        }else{
            createFloatView();
            isNO1=true;
            message="1";
            settime=intent.getStringExtra("timemessage");
            listmessage = intent.getStringArrayListExtra("listmessage");
            starts = intent.getBooleanExtra("starts", false);
            if(settime.equals("收到马上回复")){
                mills=0l;
            }if(settime.equals("1秒")){
                mills=1000l;
            }if(settime.equals("2秒")){
                mills=2000l;
            }
            if(settime.equals("3秒")){
                mills=3000l;
            }
            if(settime.equals("4秒")){
                mills=4000l;
            }
            Log.i("listmessage",listmessage.size()+"---");
            startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
            onServiceConnected();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 必须重写的方法，响应各种事件。
     * @param event
     */

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if(!starts) return ;
        int eventType = event.getEventType();
        Log.d("maptrix", "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                Log.d("maptrix", "get notification event");
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (!TextUtils.isEmpty(content)) {
                            if (isScreenLocked()) {
                                locked = true;
                                wakeAndUnlock();
                                Log.d("maptrix", "the screen is locked");
                                if (isAppForeground(MM_PNAME)) {
                                    background = false;
                                    Log.d("maptrix", "is mm in foreground");
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendNotifacationReply(event);
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    Log.d("maptrix", "is mm in background");
                                    sendNotifacationReply(event);
                                }
                            } else {
                                locked = false;
                                Log.d("maptrix", "the screen is unlocked");
                                if (isAppForeground(MM_PNAME)) {
                                    background = false;
                                    Log.d("maptrix", "is mm in foreground");
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    Log.d("maptrix", "is mm in background");
                                    sendNotifacationReply(event);
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if(isNO1){
                    isNO1=false;
                    back2Home();
                }
                Log.d("maptrix", "get type window down event");
                if (!hasAction) break;
                itemNodeinfo = null;
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    if (fill()) {
                        send();
                    }else {
                        if(itemNodeinfo != null){
                            itemNodeinfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (fill()) {
                                        send();
                                    }
                                    back2Home();
                                    release();
                                    hasAction = false;
                                }
                            }, 1000);
                            break;
                        }
                    }
                }
                //bring2Front();
                back2Home();
                release();
                hasAction = false;
                break;
        }
    }

    /**
     * 寻找窗体中的“发送”按钮，并且点击。
     */
    @SuppressLint("NewApi")
    private void send() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("发送");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo n : list) {
                    if(n.getClassName().equals("android.widget.Button") && n.isEnabled()){
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

            } else {
                List<AccessibilityNodeInfo> liste = nodeInfo
                        .findAccessibilityNodeInfosByText("Send");
                if (liste != null && liste.size() > 0) {
                    for (AccessibilityNodeInfo n : liste) {
                        if(n.getClassName().equals("android.widget.Button") && n.isEnabled()){
                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
            pressBackButton();
        }
    }
    /**
     * 模拟back按键
     */
    private void pressBackButton(){
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 拉起微信界面
     * @param event
     */
    private void sendNotifacationReply(AccessibilityEvent event) {
        hasAction = true;
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event
                    .getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");
            name = cc[0].trim();
            scontent = cc[1].trim();

            Log.i("maptrix", "sender name =" + name);
            Log.i("maptrix", "sender content =" + scontent);


            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private boolean fill() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            if(message==null){
                Toast.makeText(this,"请设置好自动回复app的信息回复",Toast.LENGTH_LONG).show();
                Toast.makeText(this,"若不使用，请到辅助功能关闭其权限",Toast.LENGTH_LONG).show();
            }else{
                try {
                    Thread.sleep(mills);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int i = mRandom.nextInt(listmessage.size());
                return findEditText(rootNode, listmessage.get(i));
            }
        }
        return false;
    }


    @SuppressLint("NewApi")
    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        int count = rootNode.getChildCount();

        Log.d("maptrix", "root class=" + rootNode.getClassName() + ","+ rootNode.getText()+","+count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo == null) {
                Log.d("maptrix", "nodeinfo = null");
                continue;
            }

            Log.d("maptrix", "class=" + nodeInfo.getClassName());
            Log.e("maptrix", "ds=" + nodeInfo.getContentDescription());
            if(nodeInfo.getContentDescription() != null){
                int nindex = nodeInfo.getContentDescription().toString().indexOf(name);
                int cindex = nodeInfo.getContentDescription().toString().indexOf(scontent);
                Log.e("maptrix", "nindex=" + nindex + " cindex=" +cindex);
                if(nindex != -1){
                    itemNodeinfo = nodeInfo;
                    Log.i("maptrix", "find node info");
                }
            }
            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                Log.i("maptrix", "==================");
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                ClipData clip = ClipData.newPlainText("label", content);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }

            if (findEditText(nodeInfo, content)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 判断指定的应用是否在前台运行
     *
     * @param packageName
     * @return
     */
    private boolean isAppForeground(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }


    /**
     * 将当前应用运行到前台
     */
    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    /**
     * 回到系统桌面
     */
    private void back2Home() {
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);

        startActivity(home);
    }


    /**
     * 系统是否在锁屏状态
     *
     * @return
     */
    private boolean isScreenLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    private void wakeAndUnlock() {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }

    private void release() {

        if (locked && kl != null) {
            Log.d("maptrix", "release the lock");
            //得到键盘锁管理器对象
            kl.reenableKeyguard();
            locked = false;
        }
    }
    TextView mTextView;
    //创建悬浮按钮
    private void createFloatView() {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_floattext, null);
        mTextView = (TextView) v.findViewById(R.id.text);
        v.setOnClickListener(new View.OnClickListener() {
            @TargetApi(24)
            @Override
            public void onClick(View v) {
                endStarts();
            }
        });
        v.setOnTouchListener(new View.OnTouchListener() {
            float startX, startY, tempX, tempY, moveX, moveY;//temp临时存储按下时的位置

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = tempX = event.getRawX();
                        startY = tempY = event.getRawY();
                        v.setLongClickable(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getRawX();
                        moveY = event.getRawY();
                        FloatWindowManager.getInstance().updateWindowLayout(moveX - startX, moveY - startY);
                        startX = moveX;
                        startY = moveY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - tempX) > 5 && Math.abs(event.getRawY() - tempY) > 5) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
                return false;//默认消费掉该事件
            }
        });
        FloatWindowManager.getInstance().showFloatWindow(getApplicationContext(), v, 0, 0);
    }

    private void endStarts(){
        starts=false;
        Intent startIntent = new Intent(weiCahtAutoReplyService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
    }
}