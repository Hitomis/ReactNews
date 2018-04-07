package com.reactnews.view.refresh;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.reactnews.view.image.GlideImageEvent;

import javax.annotation.Nullable;

/**
 * Created by Hitomis on 2018/4/7 0007.
 */

public class PullLayoutEvent extends Event {
    public static final int ON_REFRESH_RELEASED = 1;
    public static final int ON_LOADMORE_RELEASED = 1 << 1;

    private int eventType;
    private String eventMsg;

    public static String eventNameForType(int eventType) {
        switch (eventType) {
            case ON_REFRESH_RELEASED:
                return "topRefreshReleased";
            case ON_LOADMORE_RELEASED:
                return "topLoadmoreReleased";
            default:
                throw new IllegalStateException("Invalid pullLayou event: " + Integer.toString(eventType));
        }
    }

    public PullLayoutEvent(int viewId, int eventType) {
        this(viewId, eventType, null);
    }

    public PullLayoutEvent(int viewId, int eventType, @Nullable String eventMsg) {
        super(viewId);
        this.eventType = eventType;
        this.eventMsg = eventMsg;
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
