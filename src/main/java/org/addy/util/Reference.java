package org.addy.util;

public class Reference<T> {
    private T target;

    public Reference(T target) {
        this.target = target;
    }

    public Reference() {
        this(null);
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }
}
