package com.reactnews.view.multipage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.reactnews.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by hitomi on 2018/3/22.
 */

public class MultiPage extends SimpleViewManager<MultiRecyclerView> {
    public static final String TAG = "MultiPage";
    public static final int LOAD_MORE_DATA = 100;

    private String key;
    private int contentSize;
    private List<Object> contentList;

    private List<Integer> imgList;
    private ImageAdapter adapter;


    {
        imgList = new ArrayList<>();
        imgList.add(R.drawable.p1);
        imgList.add(R.drawable.p2);
        imgList.add(R.drawable.p3);
        imgList.add(R.drawable.p4);
        imgList.add(R.drawable.p5);
        imgList.add(R.drawable.p6);
        imgList.add(R.drawable.p7);
        imgList.add(R.drawable.p8);
    }

    private MultiRecyclerView recyclerView;

    @Override
    public String getName() {
        return "MultiPage";
    }

    @Override
    protected MultiRecyclerView createViewInstance(ThemedReactContext reactContext) {
        recyclerView = new MultiRecyclerView(reactContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                reactContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setViewMode(new ScaleCenterMode());
        recyclerView.setNeedFirstCenter(true);
        recyclerView.setNeedCenterForce(true);
        adapter = new ImageAdapter(reactContext);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    class ImageAdapter extends CommonAdapter<Integer> {


        public ImageAdapter(Context context) {
            super(context, R.layout.item_multi_recycler, imgList);
        }

        @Override
        protected void convert(ViewHolder holder, Integer imgRes, int position) {
            holder.getConvertView().setTag(position);
            ImageView ivImg = holder.getView(R.id.iv_img);
            ivImg.setLayoutParams(new LinearLayout.LayoutParams(contentSize, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(mContext).load(imgRes).into(ivImg);
        }
    }

    @Override
    protected void addEventEmitters(final ThemedReactContext reactContext, final MultiRecyclerView view) {
        super.addEventEmitters(reactContext, view);
        view.setOnCenterItemClickListener(new MultiRecyclerView.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClick(View v) {
                dispatchEvent(reactContext, "onCenterItemClick", v.getTag().toString());
            }
        });
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        //第一个Login 注册的名字  第二个registrationName不可以改变 第三个js回调方法
        return MapBuilder.<String, Object>builder()
                .put("onCenterItemClick", MapBuilder.of("registrationName", "onCenterItemClick"))
                .build();
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        Map<String, Integer> cmdMap = MapBuilder.of("LoadMore", LOAD_MORE_DATA);
        return cmdMap;
    }

    @Override
    public void receiveCommand(MultiRecyclerView root, int commandId, @Nullable ReadableArray args) {
        super.receiveCommand(root, commandId, args);
        Log.i(TAG, args.getString(0));
        String key = args.getString(0);
        switch (commandId) {
            case LOAD_MORE_DATA:
                if (this.key.equals(key)) {

                }
                break;
        }
    }

    private void dispatchEvent(ThemedReactContext reactContext, String eventName, String index) {
        if (reactContext != null) {
//            // native 发送事件到 js
            WritableMap event = Arguments.createMap();
            event.putString("from", "native");
            event.putString("index", index);
//            reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                    clickView.getId(),
//                    "onCenterItemClick",
//                    event);
//            Log.i(TAG, "发送点击中间的 Item event");

            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(this.key + eventName, event);
            Log.d(TAG, this.key + "click center item");
        }
    }

    @ReactProp(name = "Key")
    public void setKey(final MultiRecyclerView refreshLayout, final String key) {
        this.key = key;
    }

    @ReactProp(name = "ContentSize")
    public void setContentSize(final MultiRecyclerView refreshLayout, final int contentSize) {
        this.contentSize = dip2Px(refreshLayout.getContext(), contentSize);
        adapter.notifyDataSetChanged();
        refreshLayout.scrollFirstPosition();
    }

    private int dip2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @ReactProp(name = "ContentList")
    public void setContentList(final MultiRecyclerView refreshLayout, final ReadableArray contentList) {
        this.contentList = contentList.toArrayList();
    }

}
