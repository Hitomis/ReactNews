package com.reactnews.view.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
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
    private static final String KEY_PLACE_HOLDER_SCALE_TYPE = "placeholderScaleType";
    private static final String KEY_ERROR_IMG_SCALE_TYPE = "errorImgScaleType";
    private static final String KEY_TARGET_SIZE = "targetSize";
    private static final String KEY_CIRCLE_CROP = "circleCrop";

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
    private void assignTargetSize(Context context, ReadableMap sourceMap, RequestOptions options) {
        if (sourceMap.hasKey(KEY_WIDTH) && sourceMap.hasKey(KEY_HEIGHT)) {
            // 静态资源，按静态图片默认大小指定图片尺寸
            options.override(
                    dip2Px(context, sourceMap.getInt(KEY_WIDTH)),
                    dip2Px(context, sourceMap.getInt(KEY_HEIGHT))
            );
        }
    }

    private Object getGlideModel(Context context, String source) {
        ImageSource imageSource = new ImageSource(context, source);
        Object model;
        if (imageSource.isResource()) { // 本地资源
            try {
                model = Integer.parseInt(imageSource.getUri().getPath().substring(1));
            } catch (Exception e) {
                model = "";
            }
        } else {
            model = imageSource.getUri();
        }
        return model;
    }

    private void processOuterImage(ImageView outerImg, ReadableMap glideConfig, String sourceKey, String scaleTypeKey) {
        if (glideConfig.hasKey(scaleTypeKey)) { // 指定缩放模式
            outerImg.setScaleType(getScaleType(glideConfig.getInt(scaleTypeKey)));
        }

        ReadableMap placeholderMap = glideConfig.getMap(sourceKey);
        Context context = outerImg.getContext();

        RequestOptions options = new RequestOptions();
        assignTargetSize(context, placeholderMap, options);

        Glide.with(context)
                .applyDefaultRequestOptions( new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565))
                .load(getGlideModel(context, placeholderMap.getString("uri")))
                .apply(options)
                .into(outerImg);
    }

    private void processPlaceholder(GlideImageLayout imageLayout, ReadableMap glideConfig) {
        if (glideConfig.hasKey(KEY_PLACE_HOLDER)) {
            processOuterImage(imageLayout.preShowPlaceHolder(), glideConfig, KEY_PLACE_HOLDER, KEY_PLACE_HOLDER_SCALE_TYPE);
        }
    }

    private void processErrorImage(GlideImageLayout imageLayout, ReadableMap glideConfig) {
        if (glideConfig.hasKey(KEY_ERROR_IMG)) {
            processOuterImage(imageLayout.showOuterImg(), glideConfig, KEY_ERROR_IMG, KEY_ERROR_IMG_SCALE_TYPE);
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
            String source = sourceMap.getString("uri");

            RequestOptions options = new RequestOptions();
            assignTargetSize(context, sourceMap, options);

            if (glideConfig.hasKey(KEY_CIRCLE_CROP) && glideConfig.getBoolean(KEY_CIRCLE_CROP)) { // 裁剪为圆形图片
                options.circleCrop();
            }

            if (glideConfig.hasKey(KEY_TARGET_SIZE)) { // 显示指定图片大小
                ReadableArray sizeArray = glideConfig.getArray(KEY_TARGET_SIZE);
                options.override(
                        dip2Px(context, sizeArray.getInt(0)),
                        dip2Px(context, sizeArray.getInt(1)));
            }

            Glide.with(context)
                    .applyDefaultRequestOptions( new RequestOptions()
                            .format(DecodeFormat.PREFER_RGB_565))
                    .load(getGlideModel(context, source))
                    .apply(options)
                    .listener(new ImageLoaderListener(imageLayout, glideConfig))
                    .into(innerImg);
            sendEvent(imageLayout, GlideImageEvent.ON_LOAD_START, source);
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

    private class ImageLoaderListener implements RequestListener<Drawable> {

        private GlideImageLayout imageLayout;
        private ReadableMap glideConfig;

        public ImageLoaderListener(GlideImageLayout imageLayout, ReadableMap glideConfig) {
            this.imageLayout = imageLayout;
            this.glideConfig = glideConfig;
        }

        @Override
        public boolean onLoadFailed(@android.support.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            processErrorImage(imageLayout, glideConfig);
            sendEvent(imageLayout, GlideImageEvent.ON_ERROR, e.getMessage());
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            imageLayout.showInnerImg();
            sendEvent(imageLayout, GlideImageEvent.ON_LOAD_END, null);
            return false;
        }
    }
}
