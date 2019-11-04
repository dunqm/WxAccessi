package wxaccessi.dun.com.wxaccessi.WXaccessibilityService;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

/**
 * 作者 by K
 * 时间：on 2017/3/28 11:33
 * 邮箱 by yingzikeji@qq.com
 * <p/>
 * 类用途：
 * 最后修改：
 */
public class weiCahtFriendsMessService extends AccessibilityService {
    private String getText;
    private int getStartFID = 1;
    private int getEndFID = 199;
    private boolean isFirst;
    private int prepos;
    private int i = 0;
    private int page = 1;
    private String hello = " 测试数据   ";
    private boolean starts = false;

    private boolean isForEnd = false;//循环点击结束

    @TargetApi(24)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        getText = intent.getStringExtra("wx_text");
        getStartFID = intent.getIntExtra("start_text", 1);
        getEndFID = intent.getIntExtra("end_text", 2);
        starts = intent.getBooleanExtra("starts", false);
        isFirst = true;
        if (getText == null || getText.equals("")) {
            Toast.makeText(this, "没有正常启动", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
            onServiceConnected();
        }
        Log.i("WXHYQF","获取的数据：" + getText);

        return super.onStartCommand(intent, flags, startId);
    }


    List<AccessibilityNodeInfo> list = null;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!starts) return ;
        int eventType = event.getEventType();
        //窗口状态改变监听
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rowNode1 = event.getSource();
                if (rowNode1 == null) {
                    Log.i("WXHYQF"," noteInfo is Null");
                    return;
                } else {
                    Log.i("WXHYQF","===================================");
                    Log.i("WXHYQF","TYPE_WINDOW_STATE_CHANGED");
                    Log.i("WXHYQF","===================================");
                    recycle(rowNode1);
                }
                //主界面
                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    openNext("我");
                    openNext("设置");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")) {
                    openNext("通用");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.setting.ui.setting.SettingsAboutSystemUI")) {
                    openNext("功能");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.setting.ui.setting.SettingsPluginsUI")) {
                    openNext("群发助手");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                    openNext("开始群发");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.masssend.ui.MassSendHistoryUI")) {
                    openNext("新建群发");
                } else if (event.getClassName().equals("com.tencent.mm.plugin.masssend.ui.MassSendSelectContactUI")) {  //选择好友页面
                    //获取当前界面 节点
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.i("WXHYQF","rootWindow为空");
                        return;
                    }

                    list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/g_");//每个好友名称

                    if (isFirst) {
                        sroll(list);//获取好友列表保存
                        isFirst = false;
                    } else {
                        DemoTask demoTask = new DemoTask();
                        demoTask.execute();//好友点击
                    }

                } else if (event.getClassName().equals("com.tencent.mm.plugin.masssend.ui.MassSendMsgUI")) {  //发送消息前的页面
                    //当前在打招呼页面
                    prepos = 1;
                    Log.i("WXHYQF","===================================");
                    Log.i("WXHYQF","获取当前界面 节点  -   消息发送界面");
                    Log.i("WXHYQF","===================================");
                    //获取当前界面 节点
                    AccessibilityNodeInfo nodeInfo = event.getSource();
                    if (nodeInfo == null) {
                        Log.i("WXHYQF","rootWindow为空");
                        return;
                    }
                    inputHello(nodeInfo);
                    openDelay(300, "发送");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException mE) {
                                mE.printStackTrace();
                            }
                        }
                    }).start();
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

    /**
     * 点击匹配的nodeInfo
     *
     * @param str text关键字
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openNext(String str) {
        //获取当前界面 节点
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.i("WXHYQF","rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        Log.i("WXHYQF",str + "匹配个数: " + list.size());

        list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);

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
                if (text != null) {
                    openNext(text);
                }
            }
        }).start();
    }


    public void recycle(AccessibilityNodeInfo info) {
        //判断是否有子控件
        if (info.getChildCount() == 0) {
/*            Log.i("WXHYQF","child widget----------------------------" + info.getClassName());
            Log.i("WXHYQF","Text：" + info.getText());

            Log.i("WXHYQF","getViewIdResourceName:" + info.getViewIdResourceName());
//            Log.i("WXHYQF","getViewI"+infoCompat.getBoundsInScreen(b));
            Log.i("WXHYQF","inputHello: null  " + info.toString());
            Log.i("WXHYQF","inputHello: null  " + info.getContentDescription());*/


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

    private void endStrats(){
        isFirst = true;
        i = 0;
        starts=false;
        Intent startIntent = new Intent(weiCahtFriendsMessService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
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
                endStrats();
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

    /**
     * 循坏列表点击
     */
    int k;
    int j = 0;//全局接收K值
    boolean isForFirst = true;//第一次循环

    public void forNodeInfo(final List<AccessibilityNodeInfo> list) {

        for (k = getStartFID - 1; k < getEndFID; ) {
            if (isForFirst) {//第一次循环
                isForFirst = false;
                forClick(k, list);
            } else {
                forClick(j, list);
                if (j == getEndFID) {
                    break;
                }
            }

            try {
                Thread.sleep(500);
                srollClick(j, list);
                if (isForEnd) {
                    //获取当前界面 节点
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.i("WXHYQF","rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> next = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aog");//底下的按钮

                    //截取下一步（）里的人数

                    String text = next.get(0).getText().toString();
                    String nextText = next.get(0).getText().toString();
                    text = text.substring(4, text.length() - 1);
                    int num = Integer.parseInt(text);

                    if (num == (getEndFID - getStartFID + 1)) {
                        openNext(nextText);
                    } else {
                        Toast.makeText(this, "人数不对，请重试", Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * 循环点击
     *
     * @param k
     * @param list_
     */
    private void forClick(int k, List<AccessibilityNodeInfo> list_) {
        for (AccessibilityNodeInfo item : list_) {
            if (itemList.get(k).toString().equals(item.getText().toString())) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                item.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                k++;

                if (k == getEndFID) {
                    break;
                }
            }
        }
        j = k;
    }

    private void srollClick(int k, List<AccessibilityNodeInfo> list) {
        if (list.size() != 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            if (list.get(list.size() - 1).getParent() != null) {
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                if (list.get(list.size() - 1).getParent().getParent() != null) {
                    list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                    try {
                        Thread.sleep(300);
                        AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                        if (nodeInfo_ == null) {
                            Log.i("WXHYQF","rootWindow为空");
                            return;
                        }
                        final List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/g_");

                        for (AccessibilityNodeInfo item : list_) {

                            if (itemList.get(k).toString().equals(item.getText().toString())) {
                                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                item.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                k++;
                                j = k;
                                if (k == getEndFID) {
                                    isForEnd = true;
                                    break;
                                }
                            }
                            if (k == getEndFID) {
                                isForEnd = true;
                                break;
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public class DemoTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                Thread.sleep(1000);
                forNodeInfo(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

    //    滚动
    List<String> itemList = new ArrayList<>();
    //添加被删除的重复数据
    List<String> addItem = new ArrayList<>();

    private void sroll(List<AccessibilityNodeInfo> list) {


        for (AccessibilityNodeInfo item : list) {
//        是否有相同名字
            if (!itemList.contains(item.getText().toString())) {
                itemList.add(item.getText().toString());
            }
        }
        List<AccessibilityNodeInfo> list_ = null;
        if (list.size() != 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            if (list.get(list.size() - 1).getParent() != null) {
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                if (list.get(list.size() - 1).getParent().getParent() != null) {
                    list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                    try {
                        Thread.sleep(100);
                        AccessibilityNodeInfo info = getRootInActiveWindow();

                        if (info == null) {
                            Log.i("WXHYQF","rootWindow为空");
                        }
                        list_ = info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/g_");//好友名称
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

            if (sameList(list, list_)) {//滚动结束

                if (itemList != null) {
                    for (String item : addItem) {
                        if (itemList.contains(addItem.get(i))) {
                            itemList.remove(addItem.get(i));
                        }
                    }

                    for (String items : itemList) {
                        Log.i("WXHYQF","Items:" + items.toString() + "      Size:" + itemList.size());
                    }
                }
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

            } else {
                try {
                    Thread.sleep(100);
                    if (list_ != null) {
                        sroll(list_);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 对比两个列表是否一样
     *
     * @param list
     * @param list2
     * @return
     */
    private boolean sameList(List<AccessibilityNodeInfo> list, List<AccessibilityNodeInfo> list2) {
        int same = 0;
        for (AccessibilityNodeInfo item : list) {
            for (AccessibilityNodeInfo item2 : list2) {
                if (item.getText().toString().equals(item2.getText().toString())) {
                    same++;
                    if (same == list2.size()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}