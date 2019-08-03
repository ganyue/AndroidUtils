package com.gy.utils.weakreference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Created by yue.gan. 2019/8/3
 */
public class EWeakReference<T> extends WeakReference<T> {
    public EWeakReference(T referent) {
        super(referent);
    }

    public EWeakReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return get() == null;
        if (obj instanceof WeakReference) {
            WeakReference wObj = ((WeakReference)obj);
            return wObj.get() == null? get() == null: wObj.get().equals(get());
        } else {
            return obj.equals(get());
        }
    }
}
