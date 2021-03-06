package com.app.restfulapp;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restfulapp.ultis.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import cz.msebera.android.httpclient.Header;

/**
 * Created by minhpham on 12/16/15.
 */
public class FrgLogin extends BaseFrg implements View.OnClickListener {

    private static final String LOGIN_URL = "http://visitme.cloudapp.net:83/Home/Login";
    private String params = "?Email=%s&Password=%s";
    private TextView errorMsg;
    private EditText emailET;
    private EditText pwdET;
    private Button btnLogin;

    @Override
    protected void initView() {
        // Find Error Msg Text View control by ID
        errorMsg = (TextView) rootView.findViewById(R.id.login_error);
        // Find Email Edit View control by ID
        emailET = (EditText) rootView.findViewById(R.id.loginEmail);
        // Find Password Edit View control by ID
        pwdET = (EditText) rootView.findViewById(R.id.loginPassword);
        // Find login button
        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);


    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(String params) {
        // Show Progress Dialog
        mActivity.showLoading(true);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = mActivity.getCookieStore();
        // clear cookie to make the fresh cookie, to ensure the newest cookie is being send
        myCookieStore.clear();
        // set the new cookie
        client.setCookieStore(myCookieStore);
        client.get(LOGIN_URL + params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                // Hide Progress Dialog
                mActivity.showLoading(false);
//                try {
//                    // JSON Object
//                    JSONObject obj = new JSONObject(response);
//                    // When the JSON response has status boolean value assigned with true
//                    if(obj.getBoolean("status")){
//                        Toast.makeText(mActivity, "You are successfully logged in!", Toast.LENGTH_LONG).show();
//                        // Navigate to Home screen
//                        goHomeScreen();
//                    }
//                    // Else display error message
//                    else{
//                        errorMsg.setText(obj.optString("error_msg"));
//                        Toast.makeText(mActivity, obj.optString("error_msg"), Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    Toast.makeText(mActivity, "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//
//                }
                Utility.saveSecurity(mActivity, new String(headers[0].getValue()));
                Utility.saveString(mActivity, "code", emailET.getText().toString());
                goHomeScreen();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                mActivity.showLoading(false);
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(mActivity, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(mActivity, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(mActivity, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public String getName() {
        return "FrgLogin";
    }

    @Override
    protected int defineLayout() {
        return R.layout.frg_login;
    }

    private void goHomeScreen() {
        mActivity.addFragment(new FrgMain(), false);
    }

    @Override
    public void onClick(View v) {
//        goHomeScreen();
        // Get Email Edit View Value
        String email = emailET.getText().toString();
        // Get Password Edit View Value
        String password = pwdET.getText().toString();

        // When Email Edit View and Password Edit View have values other than Null
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            String para = String.format(params, email, password);
            invokeWS(para);
        } else {
            Toast.makeText(mActivity, "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }
    }
}
