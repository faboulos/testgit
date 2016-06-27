package com.kerawa.app.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.kerawa.app.WebPageLoader;
import com.kerawa.app.utilities.Krw_functions;

public class JavaScriptInterface {
    protected Activity parentActivity;
    protected WebView mWebView;
    
    
    public JavaScriptInterface(Activity _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;
        
    }




    @JavascriptInterface
	public void loadURL(String url) {
		final String u = url;

		parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(u);
            }
        });
	}


    
   /* @JavascriptInterface
    public void setResult(int val){
        Log.v("mylog","JavaScriptHandler.setResult is called : " + val);
        this.parentActivity.javascriptCallFinished(val);
    }
    
    @JavascriptInterface
    public void calcSomething(int x, int y){
        this.parentActivity.changeText("Result is : " + (x * y));
    }
    */
    @JavascriptInterface
    public String testString() {
    	return "test string from java";
    }

    @JavascriptInterface
    public void toastString(String text) {
        Krw_functions.Show_Toast(this.parentActivity.getApplicationContext(), text, true);
    }

    @JavascriptInterface
    public void Openurl(String url) {
        Intent lien=new Intent( this.parentActivity, WebPageLoader.class);
        lien.putExtra("lien",url);
        this.parentActivity.startActivity(lien);
    }


}
