package com.reactnews.view.multipage;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import javax.annotation.Nullable;

/**
 * Created by Hitomis on 2018/4/7 0007.
 */

public class MultiPageEvent extends Event<MultiPageEvent> {
    public static final int ON_SLIDE_CENTER = 1;
    public static final int ON_CLICK_CENTER = 1 << 1;

    private int eventType;
    private String eventMsg;

    public static String eventNameForType(int eventType) {
        switch (eventType) {
            case ON_SLIDE_CENTER:
                return "topSlideCenter";
            case ON_CLICK_CENTER:
                return "topClickCenter";
            default:
                throw new IllegalStateException("Invalid multiPage event: " + Integer.toString(eventType));
        }
    }

    public MultiPageEvent(int viewId, int eventType) {
        this(viewId, eventType, null);
    }

    public MultiPageEvent(int viewId, int eventType, @Nullable String eventMsg) {
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
