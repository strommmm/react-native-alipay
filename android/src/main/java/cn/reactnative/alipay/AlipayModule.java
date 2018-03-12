package cn.reactnative.alipay;

import android.app.Activity;
import android.os.AsyncTask;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.app.EnvUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONValue;

/**
 * Created by tdzl2003 on 3/31/16.
 */
public class AlipayModule extends ReactContextBaseJavaModule {

    public AlipayModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private static String converMap(Map<String, String> data) {
        StringWriter out = new StringWriter();
        String jsonText = "";
        try{
            JSONValue.writeJSONString(data, out);
            jsonText = out.toString();
        } catch(Exception e) {
            jsonText = e.toString();
        }
        return jsonText;
    }

    @Override
    public String getName() {
        return "RCTAlipay";
    }

    @Override
    public void initialize() {
    }

    @Override
    public void onCatalystInstanceDestroy() {
    }

    @ReactMethod
    public void pay(String orderInfo, boolean showLoading, Promise promise) {
        // EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//沙箱环境
        AsyncPayTask task = new AsyncPayTask();
        task.orderInfo = orderInfo;
        task.showLoading = showLoading;
        task.promise = promise;
        task.activity = this.getCurrentActivity();
        if (task.activity == null) {
            promise.reject("NoActivity", "Cannot get current activity.");
            return;
        }

        task.execute();
    }

    private static class AsyncPayTask extends AsyncTask<Void, Void, Void>
    {
        public String orderInfo;
        public boolean showLoading;
        public Promise promise;
        public Activity activity;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, showLoading);
                String resultString = converMap(result);
                promise.resolve(resultString);
            } catch (Throwable e){
                promise.reject(e);
            }
            return null;
        }
    }
}
