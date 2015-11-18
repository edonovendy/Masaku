package twiscode.masakuuser.Control;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twiscode.masakuuser.Model.ModelCart;
import twiscode.masakuuser.Utilities.ConfigManager;


public class JSONControl {
    private JSONResponse _JSONResponse;


    public JSONControl() {
        _JSONResponse = new JSONResponse();
    }



    public JSONObject postLogin(String phone, String password) {
        JSONObject jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phoneNumber", phone));
            params.add(new BasicNameValuePair("password", password));
            jsonObj = _JSONResponse.POSTResponse(ConfigManager.LOGIN, ConfigManager.DUKUHKUPANG, params);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("url login", ConfigManager.LOGIN);
        Log.d("params login", phone + "-" + password);
        Log.d("return login", jsonObj.toString());
        return jsonObj;

    }

    public JSONObject postRegister(String name, String email, String password) {

        JSONObject jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("phoneNumber", email));
            params.add(new BasicNameValuePair("password", password));
            jsonObj = _JSONResponse.POSTResponse(ConfigManager.REGISTER, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String postForgotPassword(String phone) {

        String jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phoneNumber", phone));
            jsonObj = _JSONResponse.POSTResponseString(ConfigManager.FORGOT_PASSWORD, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String postCheckCode(String phone, String token) {

        String jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phoneNumber", phone));
            params.add(new BasicNameValuePair("token", token));
            jsonObj = _JSONResponse.POSTResponseString(ConfigManager.CHECK_RESET_PASSWORD, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String postResendCode(String phone) {

        String jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phoneNumber", phone));
            jsonObj = _JSONResponse.POSTResponseString(ConfigManager.RESEND_RESET_PASSWORD, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String postResetPassword(String phone, String token, String password) {

        String jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("phoneNumber", phone));
            params.add(new BasicNameValuePair("token", token));
            params.add(new BasicNameValuePair("password", password));
            jsonObj = _JSONResponse.POSTResponseString(ConfigManager.RESET_PASSWORD, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public JSONObject getMenuSpeed(int page) {

        JSONObject jsonObj = new JSONObject();

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            jsonObj = _JSONResponse.GETResponseToken(ConfigManager.MENU_SPEED + page, ConfigManager.DUKUHKUPANG);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public JSONObject calculatePrice(String kode, String accessToken,List<ModelCart> cart) {

        JSONObject jsonObj = new JSONObject();

        try {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            HashMap<String,Integer> ca = new HashMap<>();
            for(int i=0;i<cart.size();i++){
                ca.put(cart.get(i).getId(), cart.get(i).getJumlah());
            }
            Gson gson = new Gson();
            String json = gson.toJson(ca);
            json = json.substring(1, json.length()-1);
            String[] arr = json.split(",");
            JSONArray jsArr = new JSONArray(arr);
            //Log.d("arr",""+jsArr.toString());

            params.add(new BasicNameValuePair("orders", jsArr.toString()));
            params.add(new BasicNameValuePair("promoCode", kode));
            Log.d("params size",""+params.size());
            for(int i=0;i<params.size();i++){
                Log.d(params.get(i).getName(),params.get(i).getValue());
            }
            jsonObj = _JSONResponse.POSTResponseToken(ConfigManager.CALCULATE_PRICE, ConfigManager.DUKUHKUPANG, accessToken, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    public String postLogoutAll(String token) {

        String jsonObj = null;

        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            jsonObj = _JSONResponse.POSTLogoutAll(ConfigManager.LOGOUT_ALL, token, ConfigManager.DUKUHKUPANG, params);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }









}
