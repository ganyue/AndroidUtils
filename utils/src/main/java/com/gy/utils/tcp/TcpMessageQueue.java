package com.gy.utils.tcp;

import com.gy.utils.udp.UdpMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ganyu on 2016/5/19.
 *
 */
public class TcpMessageQueue {
    private final int MAX_MESSAGE_COUNT = 64;
    private List<TcpMessage> messages;

    public TcpMessageQueue() {
        messages = new ArrayList<>();
        messages = Collections.synchronizedList(new ArrayList<TcpMessage>());
    }

    public void addMessage (TcpMessage msg) {
        if (messages.size() > MAX_MESSAGE_COUNT) {
            messages.remove(0);
        }

        messages.add(msg);
    }

    public TcpMessage getMessage () {
        TcpMessage msg = null;
        if (messages.size() > 0) {
            msg = messages.remove(0);
        }

        return msg;
    }

    public void clear () {
        messages.clear();
    }
}
