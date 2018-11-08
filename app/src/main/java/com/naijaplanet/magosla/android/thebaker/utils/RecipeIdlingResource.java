package com.naijaplanet.magosla.android.thebaker.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

public class RecipeIdlingResource implements IdlingResource {
    @Nullable
    private volatile ResourceCallback mCallback;
    private final AtomicBoolean mIsIdleNow = new AtomicBoolean(false);
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }

    public void setIdleState(boolean isIdling){
        mIsIdleNow.set(isIdling);
        if(mCallback!= null && isIdling){
            //noinspection ConstantConditions
            mCallback.onTransitionToIdle();
        }
    }
}
