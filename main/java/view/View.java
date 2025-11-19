package main.java.view;

import main.java.utils.callbacks.MessageCallback;

public abstract class View implements MessageCallback{

    public abstract void send(String msg);
    public MessageCallback getCallback() {
        return this::send;
    }
}
