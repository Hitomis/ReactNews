package com.reactnews.view.image;

import android.support.annotation.IntDef;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.annotation.Nullable;

/**
 * Created by hitomi on 2018/4/2.
 */

public class GlideImageEvent extends Event<GlideImageEvent> {
    public static final int ON_ERROR = 1;
    public static final int ON_LOAD_END = 2;
    public static final int ON_LOAD_START = 3;

    private final int eventType;
    private final String eventMsg;

    @IntDef({ON_ERROR, ON_LOAD_END, ON_LOAD_START})
    @Retention(RetentionPolicy.SOURCE)
    @interface ImageEventType {
    }

    public GlideImageEvent(int viewId, @ImageEventType int eventType) {
        this(viewId, eventType, null);
    }

    public GlideImageEvent(int viewId, @ImageEventType int eventType, @Nullable String eventMsg) {
        super(viewId);
        this.eventType = eventType;
        this.eventMsg = eventMsg;
    }

    public static String eventNameForType(@ImageEventType int eventType) {
        switch (eventType) {
            case ON_ERROR:
                return "topError";
            case ON_LOAD_END:
                return "topLoadEnd";
            case ON_LOAD_START:
                return "topLoadStart";
            default:
                throw new IllegalStateException("Invalid image event: " + Integer.toString(eventType));
        }
    }

    @Override
    public short getCoalescingKey() {
        return (short) eventType;
    }

    @Override
    public String getEventName() {
        return eventNameForType(eventType);
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = null;
        if (eventMsg != null) {
            eventData = Arguments.createMap();
            eventData.putString("msg", eventMsg);
        }
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}