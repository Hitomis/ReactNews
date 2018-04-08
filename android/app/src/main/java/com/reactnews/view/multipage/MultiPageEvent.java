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
    private int centerIndex;

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
        this(viewId, eventType, 0);
    }

    public MultiPageEvent(int viewId, int eventType, @Nullable int centerIndex) {
        super(viewId);
        this.eventType = eventType;
        this.centerIndex = centerIndex;
    }

    @Override
    public String getEventName() {
        return eventNameForType(eventType);
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap eventData = Arguments.createMap();
        eventData.putInt("index", centerIndex);
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), eventData);
    }
}
