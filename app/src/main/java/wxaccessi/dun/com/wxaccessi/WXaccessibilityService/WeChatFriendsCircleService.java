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
import android.widget.Toast;

import java.util.List;

import wxaccessi.dun.com.wxaccessi.R;
import wxaccessi.dun.com.wxaccessi.WXstarts;
import wxaccessi.dun.com.wxaccessi.wiget.FloatWindowManager;

/**
 * Created by Jun on 2017/4/10.
 */

public class WeChatFriendsCircleService extends AccessibilityService {
    private int i = 0;
    private int mScrollPage = 10;
    private boolean isHaveNewFriends;//是否有新的朋友
    private int mFirstPageNum;//第一页存在多少个新的朋友，如果存在超过 6 个，需要进行翻页操作
    private boolean isFirstEnter = true;//是否第一次进来
    private boolean starts = false;

    @TargetApi(24)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatView();
        starts = intent.getBooleanExtra("starts", false);
        startActivity(getPackageManager().getLaunchIntentForPackage("com.tencent.mm"));
        onServiceConnected();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(!starts) return ;
        Log.i("WXHYYZ","onAccessibilityEvent: ");
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                AccessibilityNodeInfo rowNode1 = event.getSource();
                if (rowNode1 == null) {
                    Log.i("WXHYYZ"," noteInfo is Null");
                    return;
                } else {
                    Log.i("WXHYYZ","===================================");
                    Log.i("WXHYYZ","TYPE_WINDOW_STATE_CHANGED");
                    Log.i("WXHYYZ","===================================");
                    recycle(rowNode1);
                }
                //主界面
                if (event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
                    openNext("通讯录");
                    openDelay(2000, "新的朋友");   //获取当前界面 节点
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.i("WXHYYZ","rootWindow为空");
                        return;
                    }
                } else if (event.getClassName().equals(
                        "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI")) {

                    Log.i("WXHYYZ","新的朋友界面");
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (nodeInfo == null) {
                        Log.i("WXHYYZ","rootWindow为空");
                        return;
                    }
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("接受");

                    if (isFirstEnter) {
                        mFirstPageNum = list.size(); //记录第一次进入这个界面存在新朋友个数
                    }

                    if (list.size() == 0 && isFirstEnter) {
                        Toast.makeText(this, "当前暂时没有新的朋友",
                                Toast.LENGTH_SHORT).show();
                        isHaveNewFriends = false;
                        endStarts();
                    } else {
                        if (mFirstPageNum > 6) { //第一页如果超过六个接受，进行翻页操作
                            DemoTask task = new DemoTask(list);
                            task.execute();
                        } else {
                            openDelay(2000, "接受");
                        }
                    }

                    isFirstEnter = false;

                } else if (event.getClassName().equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException mE) {
                                mE.printStackTrace();
                            }
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        }
                    }).start();
                }
                break;

        }
    }

    private void agree(List<AccessibilityNodeInfo> list) throws InterruptedException {
        if (i < list.size()) {
            for (AccessibilityNodeInfo info : list) {
                Thread.sleep(200);
                openNext("接受");
                i++;
            }
        }
//        else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException mE) {
//                        mE.printStackTrace();
//                    }
//                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//                }
//            }).start();
//        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        //判断是否有子控件
        if (info.getChildCount() == 0) {
/*            Log.i("WXHYYZ","child widget----------------------------" + info.getClassName());
            Log.i("WXHYYZ","showDialog:" + info.canOpenPopup());
            Log.i("WXHYYZ","Text：" + info.getText());
            Log.i("WXHYYZ","windowId:" + info.getWindowId());
            Log.i("WXHYYZ","windowId:" + info.isPassword());
            Log.i("WXHYYZ","getViewIdResourceName:" + info.getViewIdResourceName());
            Log.i("WXHYYZ","canOpenPopup:" + info.getExtras());
            Log.i("WXHYYZ","getActions:" + info.getActions());
            Log.i("WXHYYZ","inputHello: null" + info.getViewIdResourceName());
            Log.i("WXHYYZ","inputHello: null" + info.getText());
            Log.i("WXHYYZ","inputHello: null" + info.toString());
            Log.i("WXHYYZ","inputHello: null" + info.getContentDescription());*/
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    // 滚动
    private void scroll(List<AccessibilityNodeInfo> list) {
        if (i == list.size()) {
            Log.i("WXHYYZ","list.size()-1: " + (list.size() - 1));
            if (list.size() != 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                if (list.get(list.size() - 1).getParent() != null) {
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (list.get(list.size() - 1).getParent().getParent() != null) {
                        list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                }
            }
            Log.i("WXHYYZ","我滚动一页了");
            i = 0;
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
            Log.i("WXHYYZ","rootWindow为空");
            return;
        }

        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(str);
        Log.i("WXHYYZ",str + "匹配个数: " + list.size());
        if (list.size() > 0) {
            Log.i("WXHYYZ",str + list.get(list.size() - 1).getViewIdResourceName());
            if (str.equals("新的朋友")) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            List<AccessibilityNodeInfo> listId = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a9n");
            for (AccessibilityNodeInfo info : listId) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
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

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "服务已中断", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "连接服务", Toast.LENGTH_SHORT).show();
    }

    private void endStarts(){
        starts=false;
        i=0;
        isHaveNewFriends=true;
        isFirstEnter = true;
        Intent startIntent = new Intent(this, WXstarts.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("starts", starts);
        bundle.putInt("action", 3);
        startIntent.putExtras(bundle);
        startService(startIntent);
        FloatWindowManager.getInstance().dismissFloatWindow();
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

    public class DemoTask extends AsyncTask<String, Void, Void> {

        private List<AccessibilityNodeInfo> list;

        public DemoTask(List<AccessibilityNodeInfo> list) {
            this.list = list;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                if (!isHaveNewFriends) {
                    Log.i("WXHYYZ","新的朋友已经添加结束了");
                } else {
                    //获取当前界面 节点
                    for (int j = 0; j < mScrollPage; j++) {
                        if (!isHaveNewFriends)
                            break;

                        Log.i("WXHYYZ","第" + j + "页");
                        Log.i("WXHYYZ","接受按钮个数: " + list.size());
                        Thread.sleep(2000);
                        agree(list);
                        scroll(list);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
