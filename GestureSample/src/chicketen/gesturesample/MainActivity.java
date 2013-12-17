package chicketen.gesturesample;

import java.io.IOException;
import java.io.InputStream;

import chicketen.gesture.RotationGestureDetector;
import chicketen.gesture.RotationGestureListener;
import chicketen.gesture.TranslationGestureDetector;
import chicketen.gesture.TranslationGestureListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends Activity {
    final static private String TAG = "GestureSample";
    private MySurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new MySurfaceView(getApplicationContext());
        setContentView(mSurfaceView);
    }

    class MySurfaceView extends SurfaceView
    implements SurfaceHolder.Callback, View.OnTouchListener
    {
        private Bitmap mBitmap;
        private SurfaceHolder mHolder;
        private Matrix mMatrix;
        private float mScale;
        private float mTranslateX, mTranslateY;
        private float mAngle;
        
        private RotationGestureDetector mRotationGestureDetector;
        private TranslationGestureDetector mTranslationGestureDetector;
        private ScaleGestureDetector mScaleGestureDetector;
        
        public MySurfaceView(Context context) {
            super(context);
            
            // 画像を読み込み
            AssetManager manager = getAssets();
            InputStream is = null;
            try {
                is = manager.open("test.png");
                mBitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
            } finally {
                try {
                    is.close();
                } catch (IOException e) {}
            }
            
            // ジェスチャー用の変数初期化
            mMatrix = new Matrix();
            mScale = 1.0f;
            
            mScaleGestureDetector = new ScaleGestureDetector(context, mOnScaleListener);
            mTranslationGestureDetector = new TranslationGestureDetector(mTranslationListener);
            mRotationGestureDetector = new RotationGestureDetector(mRotationListener);
            
            getHolder().addCallback(this);
            setOnTouchListener(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mHolder = holder;

            mTranslateX = width / 2;
            mTranslateY = height / 2;
            
            present();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
        
        /**
         * タッチ処理
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mRotationGestureDetector.onTouchEvent(event);
            mTranslationGestureDetector.onTouch(v, event);
            mScaleGestureDetector.onTouchEvent(event);

            present();
            return true;
        }
        
        /**
         * 描画する。
         */
        public void present()
        {
            Canvas canvas = mHolder.lockCanvas();

            mMatrix.reset();
            mMatrix.postScale(mScale, mScale);
            mMatrix.postTranslate(-mBitmap.getWidth() / 2 * mScale, -mBitmap.getHeight() / 2 * mScale);
            mMatrix.postRotate(mAngle);
            mMatrix.postTranslate(mTranslateX, mTranslateY);

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(mBitmap, mMatrix, null);

            mHolder.unlockCanvasAndPost(canvas);
        }
        
        /**
         * 拡大縮小処理
         */
        private SimpleOnScaleGestureListener mOnScaleListener
        = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.i(TAG, "scale begin");
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.i(TAG, "scale end");
                super.onScaleEnd(detector);
            }
            
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScale *= detector.getScaleFactor();
                return true;
            };
        };
        
        /**
         * 移動処理
         */
        private TranslationGestureListener mTranslationListener
        = new TranslationGestureListener() {
            @Override
            public void onTranslationEnd(TranslationGestureDetector detector) {
                Log.i(TAG, "translation end:" + detector.getX() + "," + detector.getY());
            }

            @Override
            public void onTranslationBegin(TranslationGestureDetector detector) {
                Log.i(TAG, "translation begin:" + detector.getX() + "," + detector.getY());
            }

            @Override
            public void onTranslation(TranslationGestureDetector detector) {
                mTranslateX += detector.getDeltaX();
                mTranslateY += detector.getDeltaY();
            }
        };
        
        /**
         * 回転処理
         */
private RotationGestureListener mRotationListener
= new RotationGestureListener() {
    @Override
    public void onRotation(RotationGestureDetector detector) {
        mAngle += detector.getDeltaAngle();
    }

    @Override
    public void onRotationBegin(RotationGestureDetector detector) {
        Log.i(TAG, "rotation begin");
    }

    @Override
    public void onRotationEnd(RotationGestureDetector detector) {
        Log.i(TAG, "rotation end");
    }
};
    }
}
