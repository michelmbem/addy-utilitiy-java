package org.addy.util;

import java.util.Objects;

public final class Reference<T> {
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

    @Override
    public boolean equals(Object other) {
        return other instanceof Reference && Objects.equals(target, ((Reference<?>) other).target);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(target);
    }

    @Override
    public String toString() {
        return "Reference {target = " + target + "}";
    }
}
