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
import android.widget.TextView;
import android.widget.Toast;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

import java.util.List;


public class weiCahtAddFriendsService extends AccessibilityService {
    private boolean startFunc2 = false;//标记是否开启功能;
    private int i = 0;//记录已打招呼的人数
    private int page = 1;//记录附近的人列表页码,初始页码为1
    private int prepos = -1;//记录页面跳转来源，0--从附近的人页面跳转到详细资料页，1--从打招呼页面跳转到详细资料页
    private String sendMessage = "你好、这是测试数据！如有打扰敬请见谅"; //
    private String WxQName = "";
    private boolean starts = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("dasdasf","oncreat");
    }

    @TargetApi(24)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        WxQName = intent.getStringExtra("wx_qName");
        sendMessage = intent.getStringExtra("wx_sendInfo");
        starts = intent.getBooleanExtra("starts", false);
        Log.i("WXQJF","获取的数据：" + WxQName + "/" + sendMessage);
        if (sendMessage.toString().equals("") | WxQName.toString().equals("")) {

        } else {
            Log.i("WXQJF","获取的数据：" + 1 + "/" + 1);

            startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
            onServiceConnected();
        }
        return super.onStartCommand(intent, flags, startId);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!starts) return ;
        int eventType = event.getEventType();
        Log.i("WXQJHY","even="+eventType);
        switch (eventType) {
            //窗口状态改变监听
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rowNode1 = event.getSource();
                if (rowNode1 == null) {
                    Log.i("WXQJF"," noteInfo is Null");
                    return;
                } else {
                    Log.i("WXQJF","===================================");
                    Log.i("WXQJF","TYPE_WINDOW_STATE_CHANGED");
                    Log.i("WXQJF","===================================");
                    recycle(rowNode1);
                }

                //主界面
                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    openNext("通讯录");
                    openDelay(2000, "群聊");
                } else if (event.getClassName().equals("com.tencent.mm.ui.contact.ChatroomContactUI")) {
                    Log.i("WXQJF","===================================");
                    Log.i("WXQJF","群聊 主页 --------");
                    Log.i("WXQJF","===================================");
                    openNext(WxQName);

                } else if (event.getClassName().equals("com.tencent.mm.ui.chatting.ChattingUI")) {
                    Log.i("WXQJF","===================================");
                    Log.i("WXQJF","群聊 主窗口 --------");
                    Log.i("WXQJF","===================================");
                    if(prepos == 0){
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }else {
                        openGroupInfo(rowNode1);
                    }
                } else if (event.getClassName().equals("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI")) {
                    Log.i("WXQJF","===================================");
                    Log.i("WXQJF","群聊 ADD窗口 --------");
                    Log.i("WXQJF","===================================");
                    prepos = 0;
                    final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4x");
                    if (list.size() > 0) {
                        Log.i("WXQJF","群 人数 " + list.size());
                        if (i < (list.size() * page)) {
                            list.get(i % list.size()).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            list.get(i % list.size()).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        } else if (i >= list.size() * page) {
                            Log.i("WXQJF","----------------------------------------------------------下滑操作");
                            //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
                            list.get(list.size() - 1).getParent().getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                            page++;
                            AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException mE) {
                                        mE.printStackTrace();
                                    }
                                    AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                                    List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4x");
                                    if(list_.size()>0) {
                                        list_.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        list_.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    }else{
                                        endStarts();
                                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                        return;
                                    }
                                }
                            }).start();
                        }
                    } else {
                        Log.i("WXQJF","无法获取人数");
                    }
                } else if (event.getClassName().equals("com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI")) {

                } else if (event.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                    Log.w("WXQJF","已添加人数 ： " + i);
                    //从附近的人跳转来的，则点击打招呼按钮
                    final AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.i("WXQJF","rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("发消息");
                    if (list.size() > 0) {
                        //如果遇到已加为好友的则界面的“打招呼”变为“发消息"，所以直接返回上一个界面并记录打招呼人数+1
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        i++;
                    } else {
                        if (prepos == 1) {
                            Log.i("WXQJF","----------------------" + i + "/" + prepos);
                            //从打招呼界面跳转来的，则点击返回到附近的人页面
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            i++;
                        } else if (prepos == 0) {
                            Log.i("WXQJF","----------------------" + i + "/" + prepos);
                            Log.i("WXQJF","----------     =   ------------");
                            //从附近的人跳转来的，则点击打招呼按钮
                            final AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();
                            if (nodeInfo1 == null) {
                                Log.i("WXQJF","rootWindow为空");
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    final List<AccessibilityNodeInfo> list1 = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/yf");
                                    if (list1.size() > 0) {
                                        Log.i("添加到通讯录 ", + list1.size() + "/" + list1.get(0).toString());
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException mE) {
                                                    mE.printStackTrace();
                                                }
                                                list1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                openDelay(1000,"添加到通讯录");
                                            }
                                        }).start();
                                    } else {
                                        Log.i("WXQJF","添加到通讯录 " + list1.size() + "/");
                                        //如果遇到已加为好友的则界面的“打招呼”变为“发消息"，所以直接返回上一个界面并记录打招呼人数+1
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                                i++;
                                            }
                                        }).start();
                                    }

                                }
                            }).start();
                       /*     Log.i("gfgd",o+"-");
                            List<AccessibilityNodeInfo> list_people = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/text1");
                            Log.i("llll",list_people.get(0).getText().toString()+"---");
                            o++;*/
                        }
                    }

                } else if (event.getClassName().equals("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI")) {
                    //当前在打招呼页面  SayHiWithSnsPermissionUI
                    prepos = 1;
                    Log.i("WXQJF","----------     =   ------------");
                    //从附近的人跳转来的，则点击打招呼按钮
                    AccessibilityNodeInfo nodeInfo1 = getRootInActiveWindow();
                    if (nodeInfo1 == null) {
                        Log.i("WXQJF","rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> listMessage = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b51");
                    if (listMessage.size() < 1) {
                        //                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    } else {
                        String Key = null;
                        if (sendMessage != null) {
                            Key = sendMessage;
                        } else {
                            Key = "0";
                            Toast.makeText(getApplicationContext(), "发送消息为空", Toast.LENGTH_LONG).show();
                        }
                        Bundle arguments = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Key);
                        listMessage.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

              /*          List<AccessibilityNodeInfo> listRemarks = nodeInfo1.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cat");
                        listRemarks.get(0).performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
                        listRemarks.get(0).performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                        Bundle arguments1 = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, setRemarks);
                        listRemarks.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments1);
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", setRemarks);
                        clipboard.setPrimaryClip(clip);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            listRemarks.get(0).performAction(AccessibilityNodeInfo.ACTION_PASTE);
                            listRemarks.get(0).performAction(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS);
                        }
                        */
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(800);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                openNext("发送");
                            }
                        }).start();
                    }

                } else if (event.getClassName().equals("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI")) {
                }
                //窗口变化监听
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                AccessibilityNodeInfo rowNode = getRootInActiveWindow();
                if (rowNode == null) {
                    Log.i("WXQJF"," noteInfo is Null");
                    return;
                } else {
                    Log.i("WXQJF","===================================");
                    Log.i("WXQJF","TYPE_WINDOWS_CHANGED");
                    Log.i("WXQJF","===================================");
                    openGroupInfo(rowNode);
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
            Log.i("WXQJF","rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        Log.i("WXQJF",str + "匹配个数: " + list.size());
        if (str.contains(WxQName)){
            if (list.size()<1){
            }
        }
        if (list.size() > 0) {
            Log.i("WXQJF","群聊" + list.get(list.size() - 1).getViewIdResourceName());
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
            Log.i("WXQJF","child widget----------------------------" + info.getClassName());
            Log.i("WXQJF","showDialog:" + info.canOpenPopup());
            Log.i("WXQJF","Text：" + info.getText());
            Log.i("WXQJF","windowId:" + info.getWindowId());
            Log.i("WXQJF","windowId:" + info.isPassword());
            Log.i("WXQJF","getViewIdResourceName:" + info.getViewIdResourceName());
            Log.i("WXQJF","canOpenPopup:" + info.getExtras());
            Log.i("WXQJF","getActions:" + info.getActions());
            Log.i("WXQJF","inputHello: null" + info.getViewIdResourceName());
            Log.i("WXQJF","inputHello: null" + info.getText());
            Log.i("WXQJF","inputHello: null" + info.toString());
            Log.i("WXQJF","inputHello: null" + info.getContentDescription());
            */
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    public void openGroupInfo(AccessibilityNodeInfo info) {
        //判断是否有子控件
        if (info.getChildCount() == 0) {
            if ("android.widget.TextView".equals(info.getClassName()) && "聊天信息".equals(info.getContentDescription())) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    openGroupInfo(info.getChild(i));
                }
            }
        }
    }

    int[] locationW = new int[2];
    int[] locationS = new int[2];
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
        i=0;
        page=1;
        Intent startIntent = new Intent(weiCahtAddFriendsService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
    }
}

