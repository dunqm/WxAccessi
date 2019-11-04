package wxaccessi.dun.com.wxaccessi;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
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

import wxaccessi.dun.com.wxaccessi.bean.AdressBean;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

import static android.content.ContentValues.TAG;

/**
 * 作者 by K
 * 时间：on 2017/2/23 10:22
 * 邮箱 by yingzikeji@qq.com
 * <p>
 * 类用途：
 * 最后修改：
 *   by:dun
 *   2017年/4/15 14:50
 *   邮箱:dunqm@qq.com
 */
public class RobotService extends AccessibilityService {
    private static final String TAG = " RobotService :";
    private String wxID;
    private String wxKey;
    private String wxInput;
    private String wxSex="gb";
    private int twice=10;
    private List<AdressBean> mapBeen;
    int[] locationW = new int[2];
    int[] locationS = new int[2];
    TextView mTextView;
    private boolean starts = false;

    private boolean isFirst = true;

    int i = 0;//记录已打招呼的人数
    int page = 1;//记录附近的人列表页码,初始页码为1
    int prepos = -1;//记录页面跳转来源，0--从附近的人页面跳转到详细资料页，1--从打招呼页面跳转到详细资料页
    int location_i=0;
    int isNo1=1;
    private String hello = "你好啊";
    //MapLocation Location = new MapLocation();


    private void setLocation(double latitude,double longitude){
        Intent startIntent = new Intent(RobotService.this,WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude",latitude);
        bundle.putDouble("longitude",longitude);
        bundle.putInt("action", 4);
        startIntent.putExtras(bundle);
        startService(startIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Location.inilocation(RobotService.this);
        //Location.iniMap();
        createFolterView();
        Bundle bundle = intent.getExtras();
        ArrayList mapBeen_ = bundle.getParcelableArrayList("mapBeen");
        mapBeen = (List<AdressBean>) mapBeen_.get(0);
        setLocation(Double.valueOf(mapBeen.get(location_i).getLat()), Double.valueOf(mapBeen.get(location_i++).getLog()));
        //Location.setxy(Double.valueOf(mapBeen.get(location_i).getLat()),Double.valueOf(mapBeen.get(location_i++).getLog()));
        Log.d("测试信息", "发送完第 " + String.valueOf(location_i) + " 个位置");
        twice = intent.getIntExtra("Twice", 0);
        wxInput = intent.getStringExtra("wxInput");
        wxSex = intent.getStringExtra("wxSex");
        if (wxSex.equals("0")) {
            wxSex = "boy";
        } else if (wxSex.equals("1")) {
            wxSex = "girl";
        } else {
            wxSex = "gb";
        }
        starts = intent.getBooleanExtra("starts", false);
        System.out.println(TAG + " 拿到的信息：" + wxID + "/" + wxKey + "/" + wxInput + "/" + wxSex + "/" + String.valueOf(twice));
        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
        onServiceConnected();
        return super.onStartCommand(intent, flags, startId);
    }

    private void endStarts(){
        starts = false;// 停止
        isNo1=1;
        location_i=0;
        page=1;
        Intent startIntent = new Intent(RobotService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
    }

    private void createFolterView() {
        //创建悬浮按钮
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.layout_floattext, null);
        mTextView = (TextView) v.findViewById(R.id.text);
        v.setOnClickListener(new View.OnClickListener() {
            @TargetApi(24)
            @Override
            public void onClick(View v) {
                starts = false;// 停止
                isNo1=1;
                location_i=0;
                page=1;
                Intent startIntent = new Intent(RobotService.this, WXstarts.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("starts", starts);
                bundle.putInt("action", 3);
                startIntent.putExtras(bundle);
                startService(startIntent);
                FloatWindowManager.getInstance().dismissFloatWindow();
                //disableSelf();// 停止
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(!starts) return ;
        int evevtType = accessibilityEvent.getEventType();
        if (evevtType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //记录打招呼人数置零
            AccessibilityNodeInfo rowNode = getRootInActiveWindow();

            if (rowNode == null) {
                Log.i(TAG, "noteInfo is　null");
                return;
            } else {
                recycle(rowNode);
            }
            if (accessibilityEvent.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                i = 0;
                page=1;
                //当前在微信聊天页就点开发现
                openNext("发现");
                //然后跳转到附近的人
                openDelay(1000, "附近的人");

//                AccessibilityNodeInfo openlgin = getRootInActiveWindow();
//                openLogin(openlgin);
            } else if (accessibilityEvent.getClassName().equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI")) {
                if( i >= twice){
                    if(mapBeen.size() > location_i){
                        setLocation(Double.valueOf(mapBeen.get(location_i).getLat()),Double.valueOf(mapBeen.get(location_i).getLog()));
                        //Location.setxy(Double.valueOf(mapBeen.get(location_i).getLat()),Double.valueOf(mapBeen.get(location_i).getLog()));
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        Log.d("测试信息","发送完第 " + String.valueOf(location_i) + " 个位置");
                        location_i++;
                    }else{
                        endStarts();
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        Log.d("测试信息","发送完毕");
                        Toast.makeText(RobotService.this, "发送完毕", Toast.LENGTH_SHORT).show();
                    }
                    isNo1=1;
                    page=1;
                    return;
                }
                if (isNo1==1) {
                    openLogin(rowNode);
                }
                if (wxSex.equals("girl")) {
                    isNo1++;
                    //从附近的人跳转来的，则点击打招呼按钮
                    AccessibilityNodeInfo setFirst = getRootInActiveWindow();
                    if (setFirst == null) {
                        Log.w(TAG, "rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> listFirst = setFirst.findAccessibilityNodeInfosByText("只看女生");
                    System.out.println(TAG + "只看女生" + listFirst.size());
                    if (listFirst.size() > 0) {
                        listFirst.get(listFirst.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        listFirst.get(listFirst.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                } else if (wxSex.equals("boy")) {
                    //从附近的人跳转来的，则点击打招呼按钮
                    isNo1++;
                    AccessibilityNodeInfo setFirst = getRootInActiveWindow();
                    if (setFirst == null) {
                        Log.w(TAG, "rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> setlist = setFirst.findAccessibilityNodeInfosByText("只看男生");
                    System.out.println(TAG + "只看男生" + setlist.size());
                    if (setlist.size() > 0) {
                        setlist.get(setlist.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        setlist.get(setlist.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                } else if (wxSex.equals("gb")){
                    isNo1++;
                    AccessibilityNodeInfo setFirst = getRootInActiveWindow();
                    if (setFirst == null) {
                        Log.w(TAG, "rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> setlist = setFirst.findAccessibilityNodeInfosByText("查看全部");
                    System.out.println(TAG + "查看全部" + setlist.size());
                    if (setlist.size() > 0) {
                        setlist.get(setlist.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        setlist.get(setlist.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }


                prepos = 0;
                    //当前在附近的人界面就点选人打招呼
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    Log.w(TAG, "rootWindow为空");
                    return;
                }
                final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("以内");
                Log.i(TAG, "附近的人列表人数: " + list.size());
                if (i < (list.size() * page)) {
                    list.get(i % list.size()).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(i % list.size()).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else if (i == list.size() * page) {
                    Log.i(TAG, "1");
                    Log.i(TAG, "2 " + nodeInfo.getChild(0).getChildCount());
                    Log.i(TAG, "3 " + nodeInfo.getChildCount());
                    Log.i(TAG, "4  " + list.get(8).isScrollable());
                    Log.i(TAG, "5  com.tencent.mm:id/auh " + list.size());
                    list.get(i % list.size()).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    //本页已全部打招呼，所以下滑列表加载下一页，每次下滑的距离是一屏
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    page++;
                    AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByText("以内");
                    list.addAll(list_);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException mE) {
                                mE.printStackTrace();
                            }
                            Log.i(TAG, "列表人数: " + list.size());
                            //滑动之后，上一页的最后一个item为当前的第一个item，所以从第二个开始打招呼
//                                   list.get(i-1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                                   list.get(i-1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            AccessibilityNodeInfo nodeInfo_ = getRootInActiveWindow();
                            List<AccessibilityNodeInfo> list_ = nodeInfo_.findAccessibilityNodeInfosByText("以内");
                            list_.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            list_.get(1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }).start();
                }
            } else if (accessibilityEvent.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                if (prepos == 1) {
                    //从打招呼界面跳转来的，则点击返回到附近的人页面
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    i++;
                } else if (prepos == 0) {
                    //从附近的人跳转来的，则点击打招呼按钮
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.w(TAG, "rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("打招呼");
                    if (list.size() > 0) {
                        list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    } else {
                        //如果遇到已加为好友的则界面的“打招呼”变为“发消息"，所以直接返回上一个界面并记录打招呼人数+1
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        i++;
                    }
                }
            } else if (accessibilityEvent.getClassName().equals("com.tencent.mm.ui.contact.SayHiEditUI")) {
                //当前在打招呼页面
                prepos = 1;
                //输入打招呼的内容并发送
                if (wxInput != null) {
                    inputHello(wxInput);
                } else {
                    inputHello(hello);
                }
                openNext("发送");
            }
        }
    }



    @Override
    public void onInterrupt() {
        starts=false;
        Intent startIntent = new Intent(RobotService.this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        Log.e("WXQF","服务已中断");
        Toast.makeText(this, "服务已中断", Toast.LENGTH_SHORT).show();
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
            Log.w(TAG, "rootWindow为空");
            return;
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        System.out.println(str + "匹配个数: " + list.size());
        if (list.size() > 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    //自动输入打招呼内容
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void inputHello(String str) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        //找到当前获取焦点的view
        AccessibilityNodeInfo target = nodeInfo.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (target == null) {
            Log.d(TAG, "inputHello: null");
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", str);
        clipboard.setPrimaryClip(clip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    /**
     * 延迟打开界面
     */
    private void openDelay(final int delaytime, final String text) {
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException mE) {
                    mE.printStackTrace();
                }
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo == null) {
                    Log.w(TAG, "rootWindow为空");
                    return;
                }
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
                System.out.println(text + "匹配个数: " + list.size());
                if (list.size() > 0) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }).start();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info) {
        //判断是否有子控件

        if (info.getChildCount() == 0) {
            /*
            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            Log.i(TAG, "windowId:" + info.isPassword());
            Log.i(TAG, "getViewIdResourceName:" + info.getViewIdResourceName());
            Log.i(TAG, "canOpenPopup:" + info.getExtras());
            Log.i(TAG, "getActions:" + info.getActions());
            Log.d(TAG, "inputHello: null" + info.getViewIdResourceName());
            Log.i(TAG, "getActions:" + info.describeContents());
            */
            if ("android.widget.EditText".equals(info.getClassName()) && info.getViewIdResourceName().equals("com.tencent.mm:id/bq6")) {
//                info.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
                //找到当前获取焦点的view
                AccessibilityNodeInfo target = info.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                if (target == null) {
                    Log.d(TAG, "inputHello: null");
                    return;
                }
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = null;
                if (wxID != null) {
                    clip = ClipData.newPlainText("label", wxID);
                } else {
                    Toast.makeText(getApplicationContext(), "账号为空", Toast.LENGTH_LONG).show();
                    clip = ClipData.newPlainText("label", "0");
                }
                clipboard.setPrimaryClip(clip);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    target.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    target.performAction(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS);
                }
            }
            if ("android.widget.EditText".equals(info.getClassName()) && info.getViewIdResourceName().equals("com.tencent.mm:id/fa")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            if ("android.widget.EditText".equals(info.getClassName()) && info.getViewIdResourceName().equals("com.tencent.mm:id/ow")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLEAR_FOCUS);

            }

            if ("android.widget.EditText".equals(info.getClassName()) && info.getViewIdResourceName().equals("com.tencent.mm:id/gr")) {
                System.out.println(TAG + "-------------------" + "正在复制密码");
                /**
                 *  设置辅助焦点
                 * */
                info.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
                info.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                //找到当前获取焦点的view
                AccessibilityNodeInfo target2 = info.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
//                AccessibilityNodeInfo target2 = info.focusSearch(2);

                System.out.println(TAG + "fffffffff" + target2);

                System.out.println(TAG + "/00000000000" + info.getViewIdResourceName());
                if (target2 == null) {
                    Log.d(TAG, "inputHello: null");
                    return;
                }
                ClipboardManager clipboard2 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip2 = ClipData.newPlainText("label", "aaa");
                clipboard2.setPrimaryClip(clip2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    target2.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }

                String Key = null;
                if (wxKey != null) {
                    Key = wxKey;
                } else {
                    Key = "0";
                    Toast.makeText(getApplicationContext(), "密码为空", Toast.LENGTH_LONG).show();
                }
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, Key);
//                target2.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    /**
     * 打开指定Btn
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void openLogin(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
/*

            Log.i(TAG, "child widget----------------------------" + info.getClassName());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            Log.i(TAG, "windowId:" + info.isPassword());
            Log.i(TAG, "getViewIdResourceName:" + info.getViewIdResourceName());
            Log.i(TAG, "canOpenPopup:" + info.getExtras());
            Log.i(TAG, "getActions:" + info.getActions());
            Log.d(TAG, "inputHello: null" + info.getViewIdResourceName());
*/

            if ("android.widget.TextView".equals(info.getClassName()) && "更多".equals(info.getContentDescription())) {
                System.out.println(TAG + "==========                            ==FA");
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    openLogin(info.getChild(i));
                }
            }
        }
    }

    /**
     * 打开指定Btn
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setBtn(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if ("android.widget.TextView".equals(info.getClassName()) && info.getViewIdResourceName().equals("android:id/title")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    setBtn(info.getChild(i));
                }
            }
        }
    }
}
