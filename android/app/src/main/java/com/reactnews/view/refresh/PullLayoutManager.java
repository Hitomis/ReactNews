package com.reactnews.view.refresh;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Map;

import javax.annotation.Nullable;

public class PullLayoutManager extends ViewGroupManager<SmartRefreshLayout> {

    private static final String TAG = "PullLayout";
    private static final int FinishRefresh = 1;
    private static final int FinishLoadMore = 1 << 1;

    private Header header;
    private int requestTimeOut = 8000;

    @Override
    public String getName() {
        return TAG;
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
                refresh(view);
            }
        });
        view.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore(view);
            }
        });
    }

    //发送给RN刷新事件 加载数据
    private void refresh(SmartRefreshLayout refreshLayout) {
        if (refreshLayout != null) {
            Log.i(TAG, refreshLayout + "开始刷新");
            sendEvent(refreshLayout, PullLayoutEvent.ON_REFRESH_RELEASED, null);
        }
    }

    private void loadMore(SmartRefreshLayout refreshLayout) {
        if (refreshLayout != null) {
            Log.i(TAG, refreshLayout + "开始加载");
            sendEvent(refreshLayout, PullLayoutEvent.ON_LOADMORE_RELEASED, null);
        }
    }

    private void sendEvent(final SmartRefreshLayout smartRefreshLayout, int eventType, String eventMsg) {
        ReactContext context = (ReactContext) smartRefreshLayout.getContext();
        UIManagerModule uiManagerModule = context.getNativeModule(UIManagerModule.class);
        uiManagerModule.getEventDispatcher().dispatchEvent(
                new PullLayoutEvent(smartRefreshLayout.getId(), eventType, eventMsg));
    }

    @Override
    public @Nullable Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                PullLayoutEvent.eventNameForType(PullLayoutEvent.ON_REFRESH_RELEASED),
                MapBuilder.of("registrationName", "onRefresh"),
                PullLayoutEvent.eventNameForType(PullLayoutEvent.ON_LOADMORE_RELEASED),
                MapBuilder.of("registrationName", "onLoadmore"));
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
        switch (commandId) {
            case FinishRefresh:
                Log.i(TAG, root + "结束刷新");
                root.finishRefresh();
                break;
            case FinishLoadMore:
                Log.i(TAG, root + "结束加载");
                root.finishLoadmore();
                break;
        }
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
}
