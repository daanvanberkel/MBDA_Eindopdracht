package nl.daanvanberkel.schiphol.requests;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.daanvanberkel.schiphol.helpers.SchipholApiCredentials;

public class SchipholRequest extends JsonObjectRequest {

    public SchipholRequest(int method, String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, SchipholApiCredentials.BASE_URL + url, jsonRequest, listener, errorListener);
    }

    public SchipholRequest(String url, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(SchipholApiCredentials.BASE_URL + url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Accept", "application/json");
        params.put("app_id", SchipholApiCredentials.APP_ID);
        params.put("app_key", SchipholApiCredentials.APP_KEY);
        params.put("ResourceVersion", SchipholApiCredentials.RESOURCE_VERSION);

        return params;
    }
}
