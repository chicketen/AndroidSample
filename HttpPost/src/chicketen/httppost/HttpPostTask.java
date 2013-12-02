package chicketen.httppost;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import android.os.AsyncTask;

public class HttpPostTask extends AsyncTask<Void, Void, byte[]>{
    final static private String BOUNDARY = "MyBoundaryString";
    private HttpPostListener mListener;
    private String mURL;
    private HashMap<String, String> mTexts;
    private HashMap<String, byte[]> mImages;
    
    public HttpPostTask(String url)
    {
        super();
        
        mURL = url;
        mListener = null;
        mTexts = new HashMap<String, String>();
        mImages = new HashMap<String, byte[]>();
    }
    
    /**
     * リスナーをセットする。
     * @param listener
     */
    public void setListener(HttpPostListener listener)
    {
        mListener = listener;
    }
    
    /**
     * 送信するテキストを追加する。
     * @param text
     */
    public void addText(String key, String text)
    {
        mTexts.put(key, text);
    }
    
    /**
     * 送信する画像を追加する。
     * @param image
     */
    public void addImage(String key, byte[] data)
    {
        mImages.put(key, data);
    }
    
    /**
     * 送信を行う。
     * @return レスポンスデータ
     */
    private byte[] send(byte[] data)
    {
        if (data == null)
            return null;
        
        byte[] result = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;

        try {
            URL url = new URL(mURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 接続
            connection.connect();

            // 送信
            OutputStream os = connection.getOutputStream();
            os.write(data);
            os.close();

            // レスポンスを取得する
            byte[] buf = new byte[10240];
            int size;
            is = connection.getInputStream();
            while ((size = is.read(buf)) != -1)
            {
                baos.write(buf, 0, size);
            }
            result = baos.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {}

            try {
                connection.disconnect();
            } catch (Exception e) {}

            try {
                baos.close();
            } catch (Exception e) {}
        }

        return result;
    }
    
    /**
     * POSTするデータを作成する。
     * @return
     */
    private byte[] makePostData()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            // テキスト部分の設定
            for (Entry<String, String> entry : mTexts.entrySet())
            {
                String key = entry.getKey();
                String text = entry.getValue();
                
                baos.write(("--" + BOUNDARY + "\r\n").getBytes());
                baos.write(("Content-Disposition: form-data;").getBytes());
                baos.write(("name=\"" + key + "\"\r\n\r\n").getBytes());
                baos.write((text + "\r\n").getBytes());
            }
            
            // 画像の設定
            int count = 1;
            for (Entry<String, byte[]> entry: mImages.entrySet())
            {
                String key = entry.getKey();
                byte[] data = entry.getValue();
                String name = "upload_file" + count++;
                
                baos.write(("--" + BOUNDARY + "\r\n").getBytes());
                baos.write(("Content-Disposition: form-data;").getBytes());
                baos.write(("name=\"" + name + "\";").getBytes());
                baos.write(("filename=\"" + key + "\"\r\n").getBytes());
                baos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
                baos.write(data);
                baos.write(("\r\n").getBytes());
            }
            
            // 最後にバウンダリを付ける
            baos.write(("--" + BOUNDARY + "--\r\n").getBytes());
            
            return baos.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                baos.close();
            } catch (Exception e) {}
        }
    }

    /**
     * タスク処理
     */
    @Override
    protected byte[] doInBackground(Void... params) {
        byte[] data = makePostData();
        byte[] result = send(data);
        
        return result;
    }
    
    @Override
    protected void onPostExecute(byte[] result)
    {
        if (mListener != null)
        {
            if (result != null)
            {
                mListener.postCompletion(result);
            }
            else
            {
                mListener.postFialure();
            }
        }
    }
}
