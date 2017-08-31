package com.zmcursor.Utils;

import android.util.SparseArray;

/**
 * Created by ZMcursor on 2017/8/28 0028.
 */

public class EventBus {

    private static EventBus instance = null;

    private SparseArray<LinkedList> eventGroups;

    private EventBus() {
        eventGroups = new SparseArray<>();
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void register(int event, EventListener listener) {
        LinkedList listeners = eventGroups.get(event);
        if (listeners == null) {
            listeners = new LinkedList();
            listeners.add(listener);
            eventGroups.put(event, listeners);
        } else {
            listeners.add(listener);
        }
    }

    public void remove(int event, EventListener listener) {
        LinkedList listeners = eventGroups.get(event);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public boolean sendEvent(int event, int value) {
        LinkedList listeners = eventGroups.get(event);
        return listeners.foreach(value);
    }

    public void removeEvent(int event) {
        eventGroups.delete(event);
    }

    private class LinkedList {

        private Node firstNode = null;

        private void add(EventListener listener) {
            firstNode = new Node(listener, firstNode);
        }

        private boolean foreach(int value) {
            Node node = firstNode;
            if (node == null) return false;
            boolean isConsumed = true;
            do {
                if (!node.listener.run(value)) isConsumed = false;
                node = node.nextNode;
            } while (node != null);
            return isConsumed;
        }

        private void remove(EventListener listener) {
            if (firstNode == null) return;
            if (listener == firstNode.listener) {
                firstNode = firstNode.nextNode;
            } else {
                Node node1 = firstNode, node2 = firstNode.nextNode;
                while (node2 != null) {
                    if (listener == node2.listener) {
                        node1.nextNode = node2.nextNode;
                        return;
                    } else {
                        node1 = node2;
                        node2 = node2.nextNode;
                    }
                }
            }
        }

        private class Node {
            EventListener listener;
            Node nextNode;

            private Node(EventListener listener, Node nextNode) {
                this.listener = listener;
                this.nextNode = nextNode;
            }
        }
    }

    public interface EventListener {
        boolean run(int value);
    }
}
