package com.mob.js.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.mob.demo.mobpush.R;
import com.mob.js.MobPushUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by yyfu on 2018/5/21.
 */

public class MainActivity extends Activity {

    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.logo)
    ImageView logo;

    private String TAG = "robot";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 背景颜色
            this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            // 黑色文字
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // 白色文字
            // getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        String url = getString(R.string.url);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //webview 中localStorage无效的解决方法
        webSettings.setDomStorageEnabled(true);//DOM存储API是否可用,默认false
        webSettings.setAppCacheMaxSize(1024 * 1024 * 1024);//设置应用缓存内容的最大值
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);//设置应用缓存文件的路径
        webSettings.setAllowFileAccess(true);//是否允许访问文件,默认允许
        webSettings.setAppCacheEnabled(true);//应用缓存API是否可用,默认值false,结合setAppCachePath(String)使用
        WebViewClient wvClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                logo.setVisibility(View.VISIBLE);
                Log.d(TAG, "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                logo.setVisibility(View.GONE);
                Log.d(TAG, "onPageFinished");

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "error" + error.getDescription());
                }


            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return true;
            }
        };
        mWebView.setWebViewClient(wvClient);
        // you must call the following line after the webviewclient is set into the webview

        MobPushUtils.prepare(mWebView, wvClient);
        mWebView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

    protected void onDestroy() {
        Log.d(TAG, "webView:" + mWebView);
        if (mWebView != null) {
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearCache(true);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
