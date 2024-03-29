package wxaccessi.dun.com.wxaccessi.wiget;

import android.content.Context;
import android.view.View;

/**
 *
 * Created by john on 2017/3/10.
 */

public class FloatWindowManager {
    private static FloatWindowManager manager;
    private FloatWindow floatWindow;

    private FloatWindowManager(){

    }
    public static synchronized FloatWindowManager getInstance(){
        if(manager==null){
            manager=new FloatWindowManager();
        }
        return manager;
    }

    public void showFloatWindow(Context context, View view,int x,int y){
        if(floatWindow!=null){
            floatWindow.dismiss();
        }
        floatWindow=new FloatWindow(context);
        floatWindow.show(view,x,y);
    }



    public void showFloatWindow(Context context, View view, int x, int y, OutsideTouchListener listener,KeyBackListener backListener){
        if(floatWindow!=null){
            floatWindow.dismiss();
        }
        floatWindow=new FloatWindow(context);
        floatWindow.show(view,0,0,listener,backListener);
    }

    public void dismissFloatWindow(){
        if(floatWindow!=null){
            floatWindow.dismiss();
        }
    }

    public void justHideWindow(){
        floatWindow.justHideWindow();
    }
    /**
     * 更新位置
     * @param offsetX
     * @param offsetY
     */
    public  void updateWindowLayout(float offsetX, float offsetY){
        floatWindow.updateWindowLayout(offsetX,offsetY);
    };
}
