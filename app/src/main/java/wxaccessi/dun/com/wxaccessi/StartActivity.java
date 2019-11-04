package wxaccessi.dun.com.wxaccessi;

        import android.os.Bundle;
        import android.app.Activity;
        import android.content.SharedPreferences;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;

public class StartActivity extends Activity {
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        editor = getSharedPreferences("versionInfo",MODE_WORLD_READABLE).edit();
        editor.putBoolean("light_on", false);
        editor.commit();
        */
    }


}