package com.zmcursor.Utils;

import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by ZMcursor on 2017/8/28 0028.
 */

public class EventBus {

    private static EventBus instance = null;

    private SparseArray<ArrayList<EventListener>> eventGroups;

    private EventBus() {
        eventGroups = new SparseArray<>(8);
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void register(int event, EventListener listener) {
        ArrayList<EventListener> listeners = eventGroups.get(event);
        if (listeners == null) {
            listeners = new ArrayList<>(4);
            listeners.add(listener);
            eventGroups.put(event, listeners);
        } else {
            listeners.add(listener);
        }
    }

    public void remove(int event, EventListener listener) {
        ArrayList<EventListener> listeners = eventGroups.get(event);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public boolean sendEvent(int event, int value) {
        ArrayList<EventListener> listeners = eventGroups.get(event);
        boolean isConsumed = false;
        if (listeners != null && !listeners.isEmpty()) {
            isConsumed = true;
            for (int i = 0; i < listeners.size(); i++) {
                if (!listeners.get(i).run(value)) {
                    isConsumed = false;
                }
            }
        }
        return isConsumed;
    }

    public void removeEvent(int event) {
        eventGroups.delete(event);
    }

    public interface EventListener {
        boolean run(int value);
    }
}
