package wxaccessi.dun.com.wxaccessi.WXaccessibilityService;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import  wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 作者 by K
 * 时间：on 2017/3/28 11:33
 * 邮箱 by yingzikeji@qq.com
 * <p/>
 * 类用途：
 * 最后修改：
 */
public class weiCahtMessService extends AccessibilityService {
    private String getText;
    private String getTime;
    private int prepos;
    private int i = 0;
    private int num = 0;
    private int AllNum;
    private int page = 1;
    int time=2000;
    private String hello = "你好啊 ";
    private boolean starts = false;
    private Set<AccessibilityNodeInfo> ListAll=new HashSet<AccessibilityNodeInfo>();

    @TargetApi(24)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        getText = intent.getStringExtra("wx_text");
        i=num=0;
        page=1;
        getTime = intent.getStringExtra("wx_time");
        starts = intent.getBooleanExtra("starts", false);
        Log.i("WXQXX","获取的数据：" + getText + "/" + getTime);
        if(getTime.equals("快")){
            time=2000;
        }
        if(getTime.equals("中")){
            time=3000;

        } if(getTime.equals("慢")){
            time=4000;
        }
        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
        onServiceConnected();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
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
                    openDelay(time, "群聊");   //获取当前界面 节点
                } else if (event.getClassName().equals("com.tencent.mm.ui.contact.ChatroomContactUI")) {
                    prepos = 0;

                    if(page>1){
                        AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                        List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e3");
                        for(int i=1;i<page;i++){
                            if(list_.size() >0){
                                list_.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException mE) {
                                    mE.printStackTrace();
                                }
                                nodeInfo_ = getRootInActiveWindow();
                                list_ = nodeInfo_.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e3");
                            }
                        }
                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException mE) {
                                mE.printStackTrace();
                            }
                            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                            if (nodeInfo == null) {
                                Log.i("WXQXX","rootWindow为空");
                                return;
                            }
                            final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ej");
                            final List<AccessibilityNodeInfo> chat_y5 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/y5");
                            if(chat_y5.size()>0){//匹配到这个元素则说明已经滑动到底部
                                AllNum=Integer.valueOf(chat_y5.get(0).getText().toString().replace("个群聊",""));
                                Log.w("WXQXX","群聊总数为:" + AllNum);
                                i=list.size()-(AllNum-num);
                                Log.w("WXQXX","i = " + i + " ,list.size() = " + list.size() + " ,num = " + num);
                            }
                            if(list.size()<=0) {

                            }else if (i < list.size() ) {
                                Log.i("WXQXX","群组列表: " + list.size() + " , 当前发送的序号:" + i);
                                //Log.i("WXQXX","群组列表: " + list.get(i % list.size()).toString());
                                list.get(i % list.size()).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                list.get(i % list.size()).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            } else if (i == list.size() ) {
                                if(chat_y5.size()>0){
                                    Log.i("WXQXX","当前页数：" + page +"，检测到底部，页数不再增加。");
                                    endStarts();
                                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                }else{
                                    Log.i("WXQXX","当前页数：" + page +"，页数增加一。");
                                    page++;
                                    i=0;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(time);
                                            } catch (InterruptedException mE) {
                                                mE.printStackTrace();
                                            }
                                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                        }
                                    }).start();
                                }
                            }else if(i>list.size()){
                                endStarts();
                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                //Toast.makeText(this, "全部发送完毕！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                    //获取当前界面 节点

                } else if (event.getClassName().equals("com.tencent.mm.ui.chatting.ChattingUI")) {
                    //当前在打招呼页面
                    prepos = 1;
                    //获取当前界面 节点
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    if (nodeInfo == null) {
                        Log.i("WXQXX","rootWindow为空");
                        return;
                    }
                    inputHello(nodeInfo);
                    openDelay(time, "发送");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(time);
                            } catch (InterruptedException mE) {
                                mE.printStackTrace();
                            }
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                    }).start();

                    i++;
                    num++;
                }
            case AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION:
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
        //Log.i("WXQXX",str + "匹配个数: " + list.size());
        if (list.size() > 0) {
            //Log.i("WXQXX","群聊" + list.get(list.size() - 1).getViewIdResourceName());
            if (str.equals("群聊") && list.get(list.size() - 1).getViewIdResourceName().equals("com.tencent.mm:id/eg")) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
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

    //自动输入打招呼内容
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void inputHello(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if ("android.widget.EditText".equals(info.getClassName()) && info.getViewIdResourceName().equals("com.tencent.mm:id/r1")) {
                /**
                 *  设置辅助焦点
                 * */
                info.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                String Key = null;
                if (getText != null) {
                    Key = getText;
                } else {
                    Key = hello;
                }
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Key);
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    inputHello(info.getChild(i));
                }
            }
        }
    }
    int[] locationW = new int[2];
    int[] locationS = new int[2];
    TextView mTextView;
    View v;

    //创建悬浮按钮
    private void createFloatView() {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layout_floattext, null);
        mTextView = (TextView) v.findViewById(R.id.text);
        v.setOnClickListener(new View.OnClickListener() {
            @TargetApi(24)
            @Override
            public void onClick(View v) {
                endStarts();
                v.getLocationOnScreen(locationW);
                v.getLocationOnScreen(locationS);
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
        i=num=0;
        page=1;
        Intent startIntent = new Intent(weiCahtMessService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
    }
}
