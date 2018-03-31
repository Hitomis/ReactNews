package com.reactnews;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.reactnews.view.multipage.MultiPage;
import com.reactnews.view.refresh.PullLayout;

import java.util.ArrayList;
import java.util.List;

public class NativeViewPackage implements ReactPackage {

    @Override
    public List<com.facebook.react.bridge.NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<com.facebook.react.bridge.NativeModule> modules = new ArrayList<>();
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> views = new ArrayList<>();
        views.add(new PullLayout());
        views.add(new MultiPage());
        return views;
    }


}
