package com.reactnews;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.reactnews.view.image.GlideImageManager;
import com.reactnews.view.multipage.MultiPageManager;
import com.reactnews.view.refresh.PullLayoutManager;

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
        views.add(new PullLayoutManager());
        views.add(new MultiPageManager());
        views.add(new GlideImageManager());
        return views;
    }


}
