package chicketen.httppost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements HttpPostListener {
    final static private String TAG = "HttpPost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button button = (Button)findViewById(R.id.button_Start);
        button.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.i(TAG, "post start!");
                HttpPostTask task = new HttpPostTask("http://192.168.0.10/test/post/upload3.php");
                
                // テキストを追加
                task.addText("param1", "犬");
                task.addText("param2", "猫");
                
                // 画像を追加
                AssetManager manager = getAssets();
                for (int i = 1; i <= 2; i++)
                {
                    InputStream is = null;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                        is = manager.open("test" + i + ".jpg");
                        int len;
                        byte[] buffer = new byte[10240];
                        
                        while ((len = is.read(buffer)) > 0)
                        {
                            baos.write(buffer, 0, len);
                        }
                        task.addImage("image" + i + ".jpg", baos.toByteArray());
                    } catch (Exception e) {
                        
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {}
                        try {
                            baos.close();
                        } catch (IOException e) {}
                    }
                }
                
                // リスナーをセットする
                task.setListener(MainActivity.this);
                
                //実行
                task.execute();
            }
        });
    }

    @Override
    public void postCompletion(byte[] response) {
        Log.i(TAG, "post completion!");
        Log.i(TAG, new String(response));
    }

    @Override
    public void postFialure() {
        Log.i(TAG, "post failure!");
    }

}
