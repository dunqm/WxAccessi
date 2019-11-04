package wxaccessi.dun.com.wxaccessi.WXaccessibilityService;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;
import wxaccessi.dun.com.wxaccessi.wiget.ToastUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/5/16.
 */

public class WeiChatBoomFriendsService  extends AccessibilityService {
    private boolean starts = false;
    private int number=0;
    int time=500;

    @TargetApi(24)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        starts = intent.getBooleanExtra("starts", false);
        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
        onServiceConnected();
        return super.onStartCommand(intent, flags, startId);
    }

    //创建悬浮按钮
    private void createFloatView() {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_floattext, null);
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
        number=0;
        starts=false;
        Intent startIntent = new Intent(this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.e("CS",event.getClassName().toString());
        if(!starts) return ;
        int eventType = event.getEventType();
        //窗口状态改变监听
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rowNode1 = event.getSource();
                if (rowNode1 == null) {
                    Log.i("WXQXX"," noteInfo is Null");
                    return;
                } else {
                    //Log.i("WXQXX","===================================");
                    //Log.i("WXQXX","TYPE_WINDOW_STATE_CHANGED");
                    //Log.i("WXQXX","===================================");
                    recycle(rowNode1);
                }
                //主界面
                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    openNext("通讯录");
                    openDelay(time, "新的朋友");   //获取当前界面 节点
                } else if (event.getClassName().equals("com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI")) {
                    //当前在新的朋友界面
                    openDelay(time, "手机好友");
                } else if (event.getClassName().equals("com.tencent.mm.ui.bindmobile.MobileFriendUI")) {
                    //当前在查看手机通讯录界面
                    while(true){
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException mE) {
                            mE.printStackTrace();
                        }
                        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                        if (nodeInfo == null) {
                            Log.w(TAG, "rootWindow为空");
                            return;
                        }
                        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("添加");
                        Log.i("WXTXLBF","获取到的数量：" + list.size());
                        if(list.size() > 0){
                            for(int i=0;i<list.size();i++){
                                if(list.get(i).getText().equals("添加")){
/*                                    try {
                                        Thread.sleep(time);
                                    } catch (InterruptedException mE) {
                                        mE.printStackTrace();
                                    }*/
                                    list.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    list.get(i).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    number++;
                                }
                            }
                            Log.i("WXTXLBF","此页添加完毕，共添加数量为：" + number);
                            //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
                            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                        }else{
                            ToastUtils.toastShow(this, "已添加完毕！", ToastUtils.LENGTH_LONG);
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            endStarts();
                            break;
                        }
                    }
                }
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION:
        }

    }



    /**
     * 延迟打开界面
     */
    private void openDelay(final int delaytime, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException mE) {
                    mE.printStackTrace();
                }
                openNext(text);
            }
        }).start();
    }

    public void recycle(AccessibilityNodeInfo info) {
        //判断是否有子控件
        if (info.getChildCount() == 0) {
            /*
            Log.i("WXQXX","child widget----------------------------" + info.getClassName());
            Log.i("WXQXX","showDialog:" + info.canOpenPopup());
            Log.i("WXQXX","Text：" + info.getText());
            Log.i("WXQXX","windowId:" + info.getWindowId());
            Log.i("WXQXX","windowId:" + info.isPassword());
            Log.i("WXQXX","getViewIdResourceName:" + info.getViewIdResourceName());
            Log.i("WXQXX","canOpenPopup:" + info.getExtras());
            Log.i("WXQXX","getActions:" + info.getActions());
            Log.i("WXQXX","inputHello: null" + info.getViewIdResourceName());
            Log.i("WXQXX","inputHello: null" + info.getText());
            Log.i("WXQXX","inputHello: null" + info.toString());
            Log.i("WXQXX","inputHello: null" + info.getContentDescription());
            */
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openNext(String str) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.i("WXQXX","rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);

        if (list.size() > 0) {
            if (str.equals("新的朋友")) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务已中断", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "连接服务", Toast.LENGTH_SHORT).show();

    }

}
