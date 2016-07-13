package com.gy.utils.udp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ganyu on 2016/5/19.
 *
 */
public class UdpMessageQueue {
    private final int MAX_MESSAGE_COUNT = 64;
    private List<UdpMessage> messages;

    public UdpMessageQueue() {
        messages = new ArrayList<>();
        messages = Collections.synchronizedList(new ArrayList<UdpMessage>());
    }

    public void addMessage (UdpMessage msg) {
        if (messages.size() > MAX_MESSAGE_COUNT) {
            messages.remove(0);
        }

        messages.add(msg);
    }

    public UdpMessage getMessage () {
        UdpMessage msg = null;
        if (messages.size() > 0) {
            msg = messages.remove(0);
        }

        return msg;
    }
}
