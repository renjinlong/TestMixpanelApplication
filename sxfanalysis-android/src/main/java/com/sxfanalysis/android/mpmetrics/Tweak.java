package com.sxfanalysis.android.mpmetrics;

public interface Tweak<T> {
    /**
     * @return a value for this tweak, either the default value or a value set as part of a Mixpanel A/B test.
     */
    T get();
}
