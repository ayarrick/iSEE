package inspire.example.com.siri;




        import java.util.ArrayList;
        import java.util.Locale;

        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.Intent;
        import android.os.Bundle;
        import android.speech.RecognizerIntent;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.android.volley.NetworkError;
        import com.android.volley.ParseError;
        import com.android.volley.ServerError;
        import com.android.volley.TimeoutError;
        import com.android.volley.AuthFailureError;
        import com.android.volley.NoConnectionError;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.Request;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;

        import android.util.Log;
        import org.json.JSONException;
        import org.json.JSONObject;
        import org.json.JSONArray;


public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private TextView txtParagraph;
    private TextView txtAnswer;
    private EditText txtInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        txtParagraph = (TextView) findViewById(R.id.txtParagraph);
        txtParagraph.setText(R.string.paragraph);
        txtAnswer = (TextView) findViewById(R.id.txtAnswer);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        txtInput = (EditText) findViewById(R.id.txtInput);
        // hide the action bar
       // getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                Log.e("In Listener!:", "begin");
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    Log.e("In Listener!:", "OK");
                    printAnswer(txtInput.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
    }


    private void printAnswer(String question) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://allgood.cs.washington.edu:1995/";
        Log.e("In Request!:", "begin");
        JSONObject req = new JSONObject();
        try {
            req.put("paragragh", R.string.paragraph);
            req.put("question", question);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("In answer:", "!!!!!!!json");
        }
        Log.e(req.toString(), req.getClass().getName());
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, req, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        txtAnswer.setText("Response: " + response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("In Request!:", error.getClass().getName());
//                        txtAnswer.setText(error.toString());
//                        error.printStackTrace();
//                    }
//                });
//        queue.add(jsonObjectRequest);
        // Request a string response from the provided URL.
        String paragraph = getResources().getString(R.string.paragraph);
        url += "submit?paragraph="+paragraph+"&question="+question;
        Log.e("In Response:", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        JSONObject answer = null;

                            String[] res = response.split("\"");

                        Log.e("In Response:", response);
                            txtAnswer.setText("Response: "+ response.split("\"")[3]);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                txtAnswer.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    printAnswer(result.get(0));
                }
                break;
            }

        }
    }

    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.e("In parse!:", message);
        } catch (JSONException e) {
            Log.e("In parse!:", "json");
        } catch (java.io.UnsupportedEncodingException errorr) {
            Log.e("In parse!:", "other");
        }
    }

}
