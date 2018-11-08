package com.naijaplanet.magosla.android.thebaker.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


public class RecipeServiceReceiver extends ResultReceiver {
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     *
     * hint https://proandroiddev.com/intentservice-and-resultreceiver-70de71e5e40a
     *
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler the {@link Handler} instance
     */
    public RecipeServiceReceiver(Handler handler) {
        super(handler);
    }
    public static final int RESULT_OK= 23232;
    public static final int RESULT_ERROR= 2322;
    public static final int RESULT_INIT= 22322;
    public static final String BUNDLE_RESPONSE= "response_param";
    private Callback mCallback;


    public enum Response {
        NO_INTERNET, UNKNOWN_ERROR, SUCCESS
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if(mCallback != null){
            if(resultCode == RESULT_INIT){
                mCallback.onStart();
            }
            if(resultCode == RESULT_OK){
                mCallback.onComplete((Response)resultData.getSerializable(BUNDLE_RESPONSE));
            }else if (resultCode == RESULT_ERROR){
                mCallback.onComplete((Response)resultData.getSerializable(BUNDLE_RESPONSE));
            }
        }
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public interface Callback{
        void onComplete(Response response);
        void onStart();
    }
}
