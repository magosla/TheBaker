package com.naijaplanet.magosla.android.thebaker.ui.activities;

public interface ToolbarManager {
    default void displayWindowToolbar(boolean display){}
    void setWindowTitle(CharSequence title);
    default void setWindowSubTitle(CharSequence title){}
}

