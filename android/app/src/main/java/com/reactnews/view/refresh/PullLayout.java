package com.reactnews.view.refresh;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class PullLayout extends ViewGroupManager<SmartRefreshLayout> {

    private static final String TAG = "PullLayout";
    private static final int FinishRefresh = 1;
    private static final int FinishLoadMore = 1 << 1;

    private Header header;
    private Map<SmartRefreshLayout, String> keyMap = new HashMap<>();
    private boolean canRefresh = true;
    private boolean canLoad = true;
    private int requestTimeOut = 8000;

    @Override
    public String getName() {
        return "PullLayout";
    }

    @Override
    protected SmartRefreshLayout createViewInstance(ThemedReactContext reactContext) {
        header = new Header(reactContext);
        SmartRefreshLayout refreshLayout = new SmartRefreshLayout(reactContext);
        refreshLayout.setBackgroundColor(Color.parseColor("#444444"));
        refreshLayout.setRefreshHeader(header);
        refreshLayout.setEnableLoadmoreWhenContentNotFull(true);
        refreshLayout.setEnableLoadmore(false);//是否启用上拉加载功能
        refreshLayout.setRefreshFooter(new ClassicsFooter(reactContext));
        refreshLayout.setReboundDuration(400);//回弹动画时长（毫秒）
        refreshLayout.setHeaderTriggerRate(1.2f);//触发刷新距离 与 HeaderHieght 的比率1.0.4
        return refreshLayout;
    }

    @Override
    public void onDropViewInstance(SmartRefreshLayout view) {
        view.finishRefresh();
        super.onDropViewInstance(view);
    }

    @Override
    public void addView(SmartRefreshLayout parent, View child, int index) {
        super.addView(parent, child, index);
        parent.onFinishInflate();//在这个方法里面添加子布局 这里要主动调用否则无法显示下拉刷新内容
    }

    @Override
    protected void addEventEmitters(final ThemedReactContext reactContext, final SmartRefreshLayout view) {
        super.addEventEmitters(reactContext, view);
        view.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh(reactContext, view);
            }
        });
        view.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore(reactContext, view);
            }
        });
    }

    //发送给RN刷新事件 加载数据
    private void refresh(ThemedReactContext reactContext, SmartRefreshLayout refreshLayout) {
        if (reactContext != null) {
            WritableMap params = Arguments.createMap();
            params.putString("from", "native");
            Log.i(TAG, "开始刷新");
            if (canRefresh) {
                canRefresh = false;
                this.dispatchEvent(reactContext, refreshLayout, "onRefreshReleased", params, false);
            }
        }
    }

    private void loadMore(ThemedReactContext reactContext, SmartRefreshLayout refreshLayout) {
        if (reactContext != null) {
            WritableMap params = Arguments.createMap();
            params.putString("from", "native");
            Log.i(TAG, "开始加载");
            if (canLoad) {
                canLoad = false;
                this.dispatchEvent(reactContext, refreshLayout, "onLoadMoreReleased", params, true);
            }
        }
    }

    private void dispatchEvent(final ReactContext reactContext,
                               final SmartRefreshLayout refreshlayout,
                               final String eventName,
                               @android.support.annotation.Nullable final WritableMap params,
                               boolean refresh) {
        if (reactContext == null) {
            Log.i(TAG, "reactContext==null");
            if (refresh)
                refreshlayout.finishRefresh();
            else
                refreshlayout.finishLoadmore();
        } else {
            String key = keyMap.get(refreshlayout);
            Log.i(TAG, "发送消息事件 " + "id: " + refreshlayout.getId() + " | key: " + key + eventName);
            // 原生模块发送事件
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(key + eventName, params);
            new Handler().postDelayed(new FinishRequestRunnable(refreshlayout), requestTimeOut);
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        //第一个Login 注册的名字  第二个registrationName不可以改变 第三个js回调方法
        return MapBuilder.<String, Object>builder()
                .put("onRefreshReleased", MapBuilder.of("registrationName", "onRefreshReleased"))
                .put("onLoadMoreReleased", MapBuilder.of("registrationName", "onLoadMoreReleased"))
                .build();
    }


    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> cmdMap = MapBuilder.of("FinishRefresh", FinishRefresh);
        cmdMap.put("FinishLoadMore", FinishLoadMore);
        return cmdMap;
    }

    @Override
    public void receiveCommand(SmartRefreshLayout root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        String key = args.getString(0);
        String viewKey = keyMap.get(root);
        switch (commandId) {
            case FinishRefresh:
                if (viewKey.equals(key)) {
                    Log.i(TAG, "结束刷新");
                    root.finishRefresh();
                    canRefresh = true;
                }
                break;
            case FinishLoadMore:
                if (viewKey.equals(key)) {
                    Log.i(TAG, key + "结束加载");
                    root.finishLoadmore();
                    canLoad = true;
                }
                break;
        }
    }

    @ReactProp(name = "Key")
    public void setKey(final SmartRefreshLayout refreshLayout, final String Key) {
        keyMap.put(refreshLayout, Key);
    }

    @ReactProp(name = "HeaderText")
    public void setHeaderText(final SmartRefreshLayout refreshLayout, final String HeaderText) {
        this.header.getmHeaderText().setText(HeaderText);
    }

    /**
     * 设置是否启用越界回弹
     */
    @ReactProp(name = "EnableOverScrollBounce")
    public void enableOverScrollBounce(final SmartRefreshLayout refreshLayout, final boolean EnableOverScrollBounce) {
        refreshLayout.setEnableOverScrollBounce(EnableOverScrollBounce);
    }

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     */
    @ReactProp(name = "EnableOverScrollDrag")
    public void enableOverScrollDrag(final SmartRefreshLayout refreshLayout, final boolean EnableOverScrollDrag) {
        refreshLayout.setEnableOverScrollDrag(EnableOverScrollDrag);
    }

    /**
     * 显示拖动高度/真实拖动高度（默认0.5，阻尼效果）
     */
    @ReactProp(name = "DragRate")
    public void setDragRate(final SmartRefreshLayout refreshLayout, final float DragRate) {
        refreshLayout.setDragRate(DragRate);
    }

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    @ReactProp(name = "HeaderMaxDragRate")
    public void setHeaderMaxDragRate(final SmartRefreshLayout refreshLayout, final float HeaderMaxDragRate) {
        refreshLayout.setHeaderMaxDragRate(HeaderMaxDragRate);
    }

    /**
     * 设置 触发刷新距离 与 HeaderHieght 的比率
     */
    @ReactProp(name = "HeaderTriggerRate")
    public void setHeaderTriggerRate(final SmartRefreshLayout refreshLayout, final float HeaderTriggerRate) {
        refreshLayout.setHeaderTriggerRate(HeaderTriggerRate);
    }

    /**
     * 设置回弹动画时长
     */
    @ReactProp(name = "ReboundDuration")
    public void setReboundDuration(final SmartRefreshLayout refreshLayout, final int ReboundDuration) {
        refreshLayout.setReboundDuration(ReboundDuration);
    }

    /**
     * 是否启用下拉刷新（默认启用）
     */
    @ReactProp(name = "EnableRefresh")
    public void enableRefresh(final SmartRefreshLayout refreshLayout, final boolean EnableRefresh) {
        refreshLayout.setEnableRefresh(EnableRefresh);
    }

    /**
     * 是否启用上拉加载（默认关闭）
     */
    @ReactProp(name = "EnableLoadMore")
    public void enableLoadMore(final SmartRefreshLayout refreshLayout, final boolean EnableLoadMore) {
        refreshLayout.setEnableLoadmore(EnableLoadMore);
    }

    /**
     * 设置是否启在下拉Header的同时下拉内容
     */
    @ReactProp(name = "EnableHeaderTranslationContent")
    public void enableHeaderTranslationContent(final SmartRefreshLayout refreshLayout, final boolean EnableHeaderTranslationContent) {
        refreshLayout.setEnableHeaderTranslationContent(EnableHeaderTranslationContent);
    }

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    @ReactProp(name = "DisableContentWhenRefresh")
    public void disableContentWhenRefresh(final SmartRefreshLayout refreshLayout, final boolean DisableContentWhenRefresh) {
        refreshLayout.setDisableContentWhenRefresh(DisableContentWhenRefresh);
    }

    /**
     * 设置是否开启纯滚动模式
     */
    @ReactProp(name = "EnablePureScrollMode")
    public void enablePureScrollMode(final SmartRefreshLayout refreshLayout, final boolean EnablePureScrollMode) {
        refreshLayout.setEnablePureScrollMode(EnablePureScrollMode);
    }

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     */
    @ReactProp(name = "EnableNestedScroll")
    public void enableNestedScroll(final SmartRefreshLayout refreshLayout, final boolean EnableNestedScroll) {
        refreshLayout.setEnableNestedScroll(EnableNestedScroll);
    }

    /**
     * 设置请求超时时间
     */
    @ReactProp(name = "RequestTimeOut")
    public void setRequestTimeOut(final SmartRefreshLayout refreshLayout, final int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    private class FinishRequestRunnable implements Runnable {

        private SmartRefreshLayout refreshLayout;

        public FinishRequestRunnable(SmartRefreshLayout refreshlayout) {
            this.refreshLayout = refreshlayout;
        }

        @Override
        public void run() {
            if (refreshLayout.isRefreshing()) {
                Log.i(TAG, "请求超时，自动结束下拉刷新");
                refreshLayout.finishRefresh();
            }
            if (refreshLayout.isLoading()) {
                Log.i(TAG, "请求超时，自动结束上拉加载");
                refreshLayout.finishLoadmore();
            }
        }
    }
}
