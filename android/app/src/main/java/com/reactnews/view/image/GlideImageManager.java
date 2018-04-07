package com.reactnews.view.image;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.imagehelper.ImageSource;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by hitomi on 2018/4/2.
 */
public class GlideImageManager extends SimpleViewManager<GlideImageLayout> {
    private static final String TAG = "GlideImage";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_ERROR_IMG = "errorImg";
    private static final String KEY_PLACE_HOLDER = "placeholder";
    private static final String KEY_SCALE_TYPE = "scaleType";
    private static final String KEY_TARGET_SIZE = "targetSize";

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    protected GlideImageLayout createViewInstance(ThemedReactContext reactContext) {
        return new GlideImageLayout(reactContext);
    }

    private ImageView.ScaleType getScaleType(int type) {
        switch (type) {
            case 1:
                return ImageView.ScaleType.CENTER;
            case 2:
                return ImageView.ScaleType.CENTER_CROP;
            case 3:
                return ImageView.ScaleType.CENTER_INSIDE;
            case 4:
                return ImageView.ScaleType.FIT_CENTER;
            case 5:
                return ImageView.ScaleType.FIT_END;
            case 6:
                return ImageView.ScaleType.FIT_START;
            case 7:
                return ImageView.ScaleType.FIT_XY;
            case 8:
                return ImageView.ScaleType.MATRIX;
            default:
                return ImageView.ScaleType.CENTER_CROP;
        }
    }

    private int dip2Px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void sendEvent(final GlideImageLayout imageLayout, int eventType, String eventMsg) {
        ReactContext context = (ReactContext) imageLayout.getContext();
        UIManagerModule uiManagerModule = context.getNativeModule(UIManagerModule.class);
        uiManagerModule.getEventDispatcher().dispatchEvent(
                new GlideImageEvent(imageLayout.getId(), eventType, eventMsg));
    }

    private void assignTargetSize(Context context, ReadableMap sourceMap, DrawableRequestBuilder requestBuilder) {
        if (sourceMap.hasKey(KEY_WIDTH) && sourceMap.hasKey(KEY_HEIGHT)) {
            // 静态资源，按静态图片默认大小指定图片尺寸
            requestBuilder.override(
                    dip2Px(context, sourceMap.getInt(KEY_WIDTH)),
                    dip2Px(context, sourceMap.getInt(KEY_HEIGHT))
            );
        }
    }

    private void processPlaceholder(GlideImageLayout imageLayout, ReadableMap glideConfig) {
        if (glideConfig.hasKey(KEY_PLACE_HOLDER)) {
            ImageView outerImg = imageLayout.preShowPlaceHolder();
            ReadableMap placeholderMap = glideConfig.getMap(KEY_PLACE_HOLDER);
            Context context = outerImg.getContext();

            DrawableRequestBuilder requestBuilder =
                    Glide.with(context)
                            .load(placeholderMap.getString("uri"))
                            .dontAnimate();
            assignTargetSize(context, placeholderMap, requestBuilder);
            requestBuilder.into(outerImg);
        }
    }

    private void processErrorImg(GlideImageLayout imageLayout, ReadableMap glideConfig) {
        if (glideConfig.hasKey(KEY_ERROR_IMG)) {
            ImageView outerImg = imageLayout.showOuterImg();
            ReadableMap errorImgMap = glideConfig.getMap(KEY_ERROR_IMG);
            Context context = outerImg.getContext();

            DrawableRequestBuilder requestBuilder =
                    Glide.with(context)
                            .load(errorImgMap.getString("uri"))
                            .dontAnimate();
            assignTargetSize(context, errorImgMap, requestBuilder);
            requestBuilder.into(outerImg);
        }
    }

    @ReactProp(name = "glideConfig")
    public void setGlideConfig(final GlideImageLayout imageLayout, final ReadableMap glideConfig) {
        ImageView innerImg = imageLayout.getInnerImg();
        processPlaceholder(imageLayout, glideConfig);

        if (glideConfig.hasKey(KEY_SCALE_TYPE)) { // 指定缩放模式
            innerImg.setScaleType(getScaleType(glideConfig.getInt(KEY_SCALE_TYPE)));
        }

        if (glideConfig.hasKey(KEY_SOURCE)) {
            Context context = innerImg.getContext();
            ReadableMap sourceMap = glideConfig.getMap(KEY_SOURCE);
            String uri = sourceMap.getString("uri");

            DrawableRequestBuilder requestBuilder =
                    Glide.with(context)
                            .load(new ImageSource(context, uri).getUri())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .crossFade()
                            .listener(new ImageLoaderListener(imageLayout, glideConfig));

            assignTargetSize(context, sourceMap, requestBuilder);

            if (glideConfig.hasKey(KEY_TARGET_SIZE)) { // 显示指定图片大小
                ReadableArray sizeArray = glideConfig.getArray(KEY_TARGET_SIZE);
                requestBuilder.override(
                        dip2Px(context, sizeArray.getInt(0)),
                        dip2Px(context, sizeArray.getInt(1)));
            }

            requestBuilder.into(innerImg);
            sendEvent(imageLayout, GlideImageEvent.ON_LOAD_START, uri);
        }
    }

    @Override
    public @Nullable Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_LOAD_START),
                MapBuilder.of("registrationName", "onLoadStart"),
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_ERROR),
                MapBuilder.of("registrationName", "onError"),
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_LOAD_END),
                MapBuilder.of("registrationName", "onLoadEnd"));
    }

    private class ImageLoaderListener implements RequestListener<Uri, GlideDrawable> {

        private GlideImageLayout imageLayout;
        private ReadableMap glideConfig;

        public ImageLoaderListener(GlideImageLayout imageLayout, ReadableMap glideConfig) {
            this.imageLayout = imageLayout;
            this.glideConfig = glideConfig;
        }

        @Override
        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
            processErrorImg(imageLayout, glideConfig);
            sendEvent(imageLayout, GlideImageEvent.ON_ERROR, e.getMessage());
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            imageLayout.showInnerImg();
            sendEvent(imageLayout, GlideImageEvent.ON_LOAD_END, null);
            return false;
        }
    }
}
