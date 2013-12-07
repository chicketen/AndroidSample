package chicketen.multitouchsample;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView mTextView1, mTextView2;
    private int mPointerID1, mPointerID2; // ポインタID記憶用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextView1 = (TextView)findViewById(R.id.textView1);
        mTextView2 = (TextView)findViewById(R.id.textView2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int eventAction = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        
        switch (eventAction) {
        case MotionEvent.ACTION_DOWN:
            // 最初の指の設定
            mPointerID1 = pointerId;
            mPointerID2 = -1;
            break;
            
        case MotionEvent.ACTION_POINTER_DOWN:
            // 3本目の指以降は無視する
            if (mPointerID2 == -1)
            {
                mPointerID2 = pointerId;
            }
            else if (mPointerID1 == -1)
            {
                mPointerID1 = pointerId;
            }
            break;
            
        case MotionEvent.ACTION_POINTER_UP:
            if (mPointerID1 == pointerId)
            {
                mPointerID1 = -1;
                mTextView1.setText("");
            }
            else if (mPointerID2 == pointerId)
            {
                mPointerID2 = -1;
                mTextView2.setText("");
            }
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mPointerID1 = -1;
            mPointerID2 = -1;
            mTextView1.setText("");
            mTextView2.setText("");
            break;
            
        case MotionEvent.ACTION_MOVE:
            // 指の座標の更新
            float x1 = 0.0f;
            float y1 = 0.0f;
            float x2 = 0.0f;
            float y2 = 0.0f;
            if (mPointerID1 >= 0)
            {
                int ptrIndex = event.findPointerIndex(mPointerID1);
                x1 = event.getX(ptrIndex);
                y1 = event.getY(ptrIndex);
            }
            if (mPointerID2 >= 0)
            {
                int ptrIndex = event.findPointerIndex(mPointerID2);
                x2 = event.getX(ptrIndex);
                y2 = event.getY(ptrIndex);
            }
            
            // ジェスチャー処理
            if (mPointerID1 >= 0 && mPointerID2 == -1)
            {
                // 1本目の指だけが動いてる時の処理
                mTextView1.setText(String.format("pointer1: %3.1f, %3.1f", x1, y1));
            }
            else if (mPointerID1 == -1 && mPointerID2 >= 0)
            {
                // 2本目の指だけが動いてる時の処理
                mTextView2.setText(String.format("pointer2: %3.1f, %3.1f", x2, y2));
            }
            else if (mPointerID1 >= 0 && mPointerID2 >= 0)
            {
                // 1本目と2本目の指が動いてる時の処理
                mTextView1.setText(String.format("pointer1/2: %3.1f, %3.1f", x1, y1));
                mTextView2.setText(String.format("pointer2/2: %3.1f, %3.1f", x2, y2));
            }
            break;
        }
        
        return true;
    }
}
