package com.reactnews.view.image;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
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
public class GlideImage extends SimpleViewManager<ImageView> {
    private static final String TAG = "GlideImage";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_ERROR_IMG = "errorImg";
    private static final String KEY_PLACE_HOLDER = "placeHolder";
    private static final String KEY_SCALE_TYPE = "scaleType";
    private static final String KEY_TARGET_SIZE = "targetSize";


    private SparseArray<String> keyMap = new SparseArray<>();

    @Override
    public String getName() {
        return "GlideImage";
    }

    @Override
    protected ImageView createViewInstance(ThemedReactContext reactContext) {
        ImageView imageView = new ImageView(reactContext);
        return imageView;
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

    private void sendEvent(final ImageView imageView, int eventType) {
        ReactContext context = (ReactContext) imageView.getContext();
        UIManagerModule uiManagerModule = context.getNativeModule(UIManagerModule.class);
        uiManagerModule.getEventDispatcher().dispatchEvent(
                new GlideImageEvent(imageView.getId(), eventType));
    }

    @ReactProp(name = "key")
    public void setKey(final ImageView imageView, final String Key) {
        keyMap.put(imageView.getId(), Key);
    }

    @ReactProp(name = "glideConfig")
    public void setGlideConfig(final ImageView imageView, final ReadableMap glideConfig) {
        if (glideConfig.hasKey(KEY_SCALE_TYPE)) {
            imageView.setScaleType(getScaleType(glideConfig.getInt(KEY_SCALE_TYPE)));
        }

        if (glideConfig.hasKey(KEY_SOURCE)) {
            Context context = imageView.getContext();
            ReadableMap sourceMap = glideConfig.getMap(KEY_SOURCE);
            String uri = sourceMap.getString("uri");

            DrawableRequestBuilder requestBuilder =
                    Glide.with(context)
                            .load(new ImageSource(context, uri).getUri())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .crossFade()
                            .listener(new RequestListener<Uri, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    sendEvent(imageView, GlideImageEvent.ON_ERROR);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    sendEvent(imageView, GlideImageEvent.ON_LOAD_END);
                                    return false;
                                }
                            });

            if (sourceMap.hasKey("width") && sourceMap.hasKey("height")) { // 静态资源，按静态图片默认大小指定
                requestBuilder.override(
                        dip2Px(context, sourceMap.getInt("width")),
                        dip2Px(context, sourceMap.getInt("height"))
                );
            }

            if (glideConfig.hasKey(KEY_TARGET_SIZE)) { // 显示指定图片大小
                ReadableArray sizeArray = glideConfig.getArray(KEY_TARGET_SIZE);
                requestBuilder.override(
                        dip2Px(context, sizeArray.getInt(0)),
                        dip2Px(context, sizeArray.getInt(1)));
            }

            requestBuilder.into(imageView);
            sendEvent(imageView, GlideImageEvent.ON_LOAD_START);
        }
    }

    @Override
    public @Nullable
    Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_LOAD_START),
                MapBuilder.of("registrationName", "onLoadStart"),
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_ERROR),
                MapBuilder.of("registrationName", "onError"),
                GlideImageEvent.eventNameForType(GlideImageEvent.ON_LOAD_END),
                MapBuilder.of("registrationName", "onLoadEnd"));
    }
}
