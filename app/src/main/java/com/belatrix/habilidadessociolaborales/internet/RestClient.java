package com.belatrix.habilidadessociolaborales.internet;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestClient {

    private static RestClient sInstance = null;
    private static Context sContext = null;
    private static RequestQueue sQueue;

    public static RestClient getInstance(Context context) {

        RestClient.sContext = context;

        if (sInstance == null) {
            sInstance = new RestClient();

            File cacheDir = new File(RestClient.sContext.getCacheDir(), "volley");
            String userAgent = "volley/0";
            try {
                String packageName = RestClient.sContext.getPackageName();
                PackageInfo info = RestClient.sContext.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + "/" + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            HttpStack stack;
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
            Network network = new BasicNetwork(stack);
            RequestQueue tempQueue = new RequestQueue(new DiskBasedCache(cacheDir), network, 100);
            tempQueue.start();

            sQueue = tempQueue;
        }

        return sInstance;
    }

    public void get(String url, Map<String, String> params, final SyncHandler responseHandler) {

        if (params != null) {
            url = addParametersToUrl(url, params);
        }

        Log.d("PARAMETERS", url);

        final CustomRequest request = new CustomRequest(Request.Method.GET, url, params, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseHandler.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseHandler.onFailure(new Error(error.getMessage()));
            }
        });

        sQueue.add(request);
    }

    public void getImage(String imageUrl, final ImageHandler handler){
        ImageRequest ir = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                handler.onResponse(response);
            }


        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handler.onErrorResponse(error);
            }
        });

        RestClient.getInstance(sContext).getQueue().add(ir);
    }

    private String addParametersToUrl(String url, Map<String, String> params) {
        if (!url.endsWith("?")) url += "?";

        List<NameValuePair> tempParams = new LinkedList<NameValuePair>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            tempParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        String paramString = URLEncodedUtils.format(tempParams, "utf-8");

        url += paramString;
        return url;
    }

/*    public JSONObject getSync(String url, Map<String, String> params) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final CustomRequest request = new CustomRequest(Request.Method.GET, url, params, null, future, future);

        sQueue.add(request);
        // future.setRequest(request);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "RestClient Error: " + e.getMessage());
            return null;
        } catch (ExecutionException e) {
            Log.d(TAG, "RestClient Error: " + e.getMessage());
            return null;
        } catch (TimeoutException e) {
            Log.d(TAG, "RestClient Error: " + e.getMessage());
            return null;
        }
    }

*//*    public void post(String url, JSONObject jsonRequest, final SyncHandler responseHandler) {
        final CustomRequest request = new CustomRequest(Request.Method.POST, url, new HashMap<String, String>(), jsonRequest, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                responseHandler.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseHandler.onFailure(new Error(error));
            }
        }
        );
        sQueue.add(request);
    }

    public static String normalizeHTTPHeader(String header) {
        header = header.replace("; rel=\"next\"", "");
        header = header.replace("<", "");
        header = header.replace(">", "");

        return header;
    }

    public static String getNextURL(JSONObject headers) throws JSONException {
        String nextURL;

        if (headers.has("Link")) {
            nextURL = headers.getString("Link");

            if (nextURL.equals("")) {
                nextURL = "pagination_dont_ask_more";
            }
        } else {
            nextURL = "pagination_dont_ask_more";
        }

        return nextURL;
    }*/

    public class CustomRequest extends JsonObjectRequest {
        private Map<String, String> mParams;
        private JSONObject mJSONBody = null;

        public CustomRequest(int method, String url, Map<String, String> params, JSONObject jsonRequest, Listener<JSONObject> listener,
                             ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
            this.mParams = params;
            this.mJSONBody = jsonRequest;
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> headers = new HashMap<String, String>();

            //headers.put("Authorization", "Token token=" + Utils.getInstance(context).getUserToken());
            //headers.put("Accept", "application/vnd.hoopla-mobile-api.v1");

            if (getMethod() == Request.Method.POST) {//adding content-type header. It is required for hoppla servers in POST calls.
                headers.put("Content-Type", "application/json");
            }

            return headers;
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mParams;
        }

        @Override
        public byte[] getBody() {//if POST, hoopla requires body to be sent as JSON
            if (getMethod() == Request.Method.POST) {
                return mJSONBody.toString().getBytes();
            } else return super.getBody();
        }

        @Override
        public String getBodyContentType() {//if POST, hoopla servers require body to be sent as JSON
            if (getMethod() == Request.Method.POST) {
                return "application/json;charset=utf-8";
            } else {
                return super.getBodyContentType();
            }
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("response_data", getJSON(response.data));
                jsonObject.put("response_header", new JSONObject(response.headers));

                return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));

            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        private Object getJSON(byte[] data) {
            JSONTokener token = new JSONTokener(new String(data));
            if (token.more()) {
                Object json;
                try {
                    json = token.nextValue();
                    return json;

                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public RequestQueue getQueue(){
        return sQueue;
    }

    public interface SyncHandler {
        void onSuccess(Object result);
        void onFailure(Error error);
    }

    public interface ImageHandler {
        void onResponse(Bitmap bitmap);
        void onErrorResponse(Exception e);
    }
}
