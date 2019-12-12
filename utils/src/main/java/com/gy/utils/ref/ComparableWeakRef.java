package com.gy.utils.ref;

import android.support.annotation.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class ComparableWeakRef<T> extends WeakReference<T> {

    public ComparableWeakRef(T referent) {
        super(referent);
    }

    public ComparableWeakRef(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Object src = get();

        if (obj == null) {
            return src == null;
        }

        Object dst;
        if (obj instanceof Reference) {
            Reference ref = (Reference) obj;
            dst = ref.get();
        } else {
            dst = obj;
        }

        if (src == null) {
            return dst == null;
        } else {
            return src.equals(dst);
        }
    }
}
