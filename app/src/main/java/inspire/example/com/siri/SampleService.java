package inspire.example.com.siri;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class SampleService extends AccessibilityService implements TextToSpeech.OnInitListener {

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            isRecording = false;
            mAudioRecord.stop();
            Log.d("xingquan.he", "3-2-1.");
        }
    };

    private final int REQ_CODE_SPEECH_INPUT = 100;

    String userVoiceInput = null;

    private void enableGesture() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        setServiceInfo(info);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(getApplicationContext(), "Wakeup", Toast.LENGTH_SHORT).show();
        enableGesture();
        final AccessibilityServiceInfo info = getServiceInfo();
        info.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        setServiceInfo(info);
        mSpeech = new TextToSpeech(this, this);
    }

    ArrayList<AccessibilityNodeInfo> textViewNodes;

    private void findChildViews(AccessibilityNodeInfo parentView) {
        if (parentView == null || parentView.getClassName() == null) {
            return;
        }
        int childCount = parentView.getChildCount();
        if (childCount == 0) {
            String res = parentView.getClassName().toString();
            try {
                res += parentView.getText();
            } catch (Exception e) {

            }
            System.err.println(res);
        }
        if (childCount == 0 &&
                (parentView.getClassName().toString().contentEquals("android.widget.TextView")
                        || parentView.getClassName().toString().contentEquals("android.webkit.WebView"))
                ) {
            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i));
            }
        }
    }

    private String realGet(AccessibilityNodeInfo info) {
        String text = String.valueOf(info.getText());
        if (isEmptyOrNullString(text)) {
            text = String.valueOf(info.getContentDescription());
        }
        return text;
    }

    private boolean isEmptyOrNullString(String text) {
        return text == null || text.length() == 0;
    }
     int fg = 0;
     /**
     * getText from current window
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        //System.err.println(eventType);
        switch (eventType) {

//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
//
//                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//                textViewNodes = new ArrayList<>();
//
//                findChildViews(rootNode);
//                //System.err.println(textViewNodes.size());
//                for(AccessibilityNodeInfo mNode : textViewNodes){
//                    String curr = realGet(mNode);
//                    if(isEmptyOrNullString(curr)){
//                        return;
//                    }
//                    //System.err.println(curr);
//                }
//                break;
            //case AccessibilityEvent.TYPE_VIEW_CLICKED:
                /*if (fg == 0) {
                    showToast("speak");
                    initData();
                    onStart();
                    //System.err.println(userVoiceInput);
                    fg++;
                } else if (fg == 1) {
                    showToast("end speak, waiting");
                    onEnd();
                    fg++;
                } else if (fg == 2) {
                    fg++;
                    showToast(userVoiceInput);
                }*/
                //Toast.makeText(getApplicationContext(), "Response: "+ "hello", Toast.LENGTH_SHORT).show();

                //printAnswer("how many","abc");
                //iiiitextToSpeech = new TextToSpeech(this, this);
                /*if (textToSpeech != null && !textToSpeech.isSpeaking()) {
                    // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setPitch(0.5f);
                    //设定语速 ，默认1.0正常语速
                    textToSpeech.setSpeechRate(1.5f);
                    //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
                    textToSpeech.speak("hello", TextToSpeech.QUEUE_FLUSH, null,null);
                }*/

                //Intent intent = new Intent(getBaseContext(),MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(intent);
             //   break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                disableSelf();
                //textToSpeech.stop();
                //textToSpeech.shutdown();
                //disableSelf();
        }
    }

    private void printAnswer(String paragraph, String question) {
        question = question.replace("\"]","");
        question = question.replace("[\"","");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.20.77.71:5000/";
        try {
            paragraph = URLEncoder.encode(paragraph, "utf-8").replaceAll("\\+", "+");
        }catch (Exception e){

        }
        try {
            question = URLEncoder.encode(question, "utf-8").replaceAll("\\+", "+");
        }catch (Exception e){

        }
        url += "?para="+paragraph+"&q="+question;
        Log.e("In Response:", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        JSONObject answer = null;

                        String[] res = response.split("\"");

                        Log.e("In Response:", response);

                        Toast.makeText(getApplicationContext(), "Response: "+ response.split("\"")[0], Toast.LENGTH_SHORT).show();
                        playTTS(response.split("\"")[0]);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(),"网络请求超时，请重试！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(error instanceof ServerError) {
                    Toast.makeText(getApplicationContext(),"服务器异常",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(error instanceof NetworkError) {
                    Toast.makeText(getApplicationContext(),"请检查网络",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(error instanceof ParseError) {
                    Toast.makeText(getApplicationContext(),"数据格式错误",Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "That didn't work", Toast.LENGTH_SHORT).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    String getViewText() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        textViewNodes = new ArrayList<>();
        findChildViews(rootNode);
        StringBuilder result = new StringBuilder();
        for(AccessibilityNodeInfo mNode : textViewNodes){
            String curr = realGet(mNode);
            if(isEmptyOrNullString(curr)) {
                break;
            }
            boolean hasSpace = false;
            for (int i = 0; i < curr.length(); ++i) {
                if (curr.charAt(i) == ' ') {
                    hasSpace = true;
                    break;
                }
            }
            if (hasSpace) {
                result.append(curr).append('\n');
            }
        }
        return result.toString();
    }

    private void playTTS(String str) {
        if (mSpeech== null) mSpeech = new TextToSpeech(this, this);
        mSpeech.speak(str, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    @Override
    protected boolean onGesture (int gestureId){
        if(gestureId==GESTURE_SWIPE_RIGHT_AND_UP){
            playTTS("start recording");
            try{
                Thread.sleep(2000);
            }catch(Exception e){
            }
            Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
            initData();
            onStart();
        }else if(gestureId==GESTURE_SWIPE_LEFT_AND_DOWN){
            playTTS("recording finished");
            Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
            onEnd();
            while(userVoiceInput==null){
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    continue;
                }
            }
            Toast.makeText(getApplicationContext(), userVoiceInput, Toast.LENGTH_SHORT).show();
            String userText = getViewText();
            System.err.println("!!!!!!!!!!!!!!!!!user: "+userText+"\n"+userVoiceInput);
            printAnswer(userText, userVoiceInput);
            userVoiceInput = null;
        }

        return super.onGesture(gestureId);
    }
    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {

    }
    class HTTPHandle {
        private class AuthService {

            public String getAuth() {
                String clientId = "lWWInbCuzPMkyDhCjyymGIWs";
                String clientSecret = "iODW3tLwhdzhcItSexAVhOCCQp97Xawl";
                return getAuth(clientId, clientSecret);
            }

            public String getAuth(String ak, String sk) {
                String authHost = "https://openapi.baidu.com/oauth/2.0/token?";
                String getAccessTokenUrl = authHost
                        + "grant_type=client_credentials"
                        + "&client_id=" + ak
                        + "&client_secret=" + sk;
                try {
                    URL realUrl = new URL(getAccessTokenUrl);
                    HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    Map<String, List<String>> map = connection.getHeaderFields();
//					for (String key : map.keySet()) {
//						System.err.println(key + "--->" + map.get(key));
//					}
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line);
                    }
                    //System.err.println("result:" + result.toString());
                    JSONObject jsonObject = new JSONObject(result.toString());
                    return jsonObject.getString("access_token");
                } catch (Exception e) {
                    System.err.println("获取token失败！");
                    e.printStackTrace(System.err);
                }
                return null;
            }

        }

        public String getEnglishContext(ByteArrayOutputStream byteArrayOutputStream) {
            String result = null;
            try {
                //File file = new File(filePath);
//                FileInputStream fileInputStream = new FileInputStream(file);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
//                byte[] b = new byte[1024];
//                int n;
//                while ((n = fileInputStream.read(b)) != -1) {
//                    byteArrayOutputStream.write(b, 0, n);
//                }
//                fileInputStream.close();
//                byteArrayOutputStream.close();
                byte[] buffer = byteArrayOutputStream.toByteArray();
                String base64EncodeFile = new String(Base64.encode(buffer, Base64.DEFAULT));
                AuthService authService = new AuthService();
                String myToken = authService.getAuth();
                JSONObject json = new JSONObject();
            /*
            {
    "format":"pcm",
    "rate":16000,
    "dev_pid":1536,
    "channel":1,
    "token":xxx,
    "cuid":"baidu_workshop",
    "len":4096,
    "speech":"xxx", // xxx为 base64（FILE_CONTENT）
}
             */
                json.put("format", "pcm");
                json.put("rate", "16000");
                json.put("dev_pid", 1737);
                json.put("channel", "1");
                json.put("token", myToken);
                json.put("cuid", "1012291737");
                json.put("len", buffer.length);
                json.put("speech", base64EncodeFile.replace("\n", ""));
                System.err.println(json.toString());
                JSONObject response = makeRequest(json);
                result = String.valueOf(response.get("result"));
                //System.err.println("baidures:" + result);
            } catch (FileNotFoundException e) {
                //System.err.println(filePath);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        private JSONObject makeRequest(JSONObject json) throws Exception {
            URL url = new URL("http://vop.baidu.com/server_api");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type","application/json");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(json.toString());
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sbf = new StringBuffer();
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbf.append(lines);
            }
            System.out.println(sbf);
            reader.close();
            connection.disconnect();
            return new JSONObject(sbf.toString());
        }

    }


    private TextToSpeech mSpeech; // TTS对象

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mSpeech.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "data lost or not support", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private AudioTrack mAudioTrack;
    private AudioRecord mAudioRecord=null;
    private int mRecorderBufferSize;
    private byte[] mAudioData;

    /*默认数据*/
    private int mSampleRateInHZ = 16000; //采样率
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;  //位数
    private int mChannelConfig = AudioFormat.CHANNEL_IN_DEFAULT;


    private boolean isRecording = false;
    private String mTmpFileAbs = "";


    private void initData() {
        mRecorderBufferSize = AudioRecord.getMinBufferSize(mSampleRateInHZ, mChannelConfig, mAudioFormat);
        mAudioData = new byte[320];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, mSampleRateInHZ, mChannelConfig, mAudioFormat, mRecorderBufferSize);
//        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRateInHZ, mChannelConfig, mAudioFormat, mRecorderBufferSize * 2
//                , AudioTrack.MODE_STREAM);
    }

    void onStart() {
        if (isRecording) {
            showToast("on start");
            return;
        }
        timer.schedule(timerTask,60000);//60s后执行

//        String tmpName = System.currentTimeMillis() + "_" + mSampleRateInHZ + "";
//        final File tmpFile = createFile(tmpName + ".pcm");
//        final String path = tmpName + ".pcm";
//        assert tmpFile != null;
//        mTmpFileAbs = tmpFile.getAbsolutePath();

        isRecording = true;
        //mAudioRecord.release();
        mAudioRecord.startRecording();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    while (isRecording) {
                        int readSize = mAudioRecord.read(mAudioData, 0, mAudioData.length);
                        //Log.i(TAG, "run: ------>" + readSize);
                        outputStream.write(mAudioData);
                    }
                    outputStream.close();
                    //System.err.println(tmpFile.getAbsolutePath());
                    userVoiceInput = (new HTTPHandle()).getEnglishContext(outputStream);
                    outputStream.reset();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //System.err.println(path);

    }

    void onEnd() {
        if (!isRecording) {
            showToast("on end");
            return;
        }
        if(timer!=null) {
            timer.cancel();
            timerTask.cancel();
        }

        isRecording = false;
        mAudioRecord.stop();


    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }


}
