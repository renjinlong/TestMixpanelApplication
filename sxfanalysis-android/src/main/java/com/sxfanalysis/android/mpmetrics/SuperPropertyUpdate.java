package com.sxfanalysis.android.mpmetrics;

import org.json.JSONObject;

public interface SuperPropertyUpdate {
    /**
     * update should take a JSONObject and return a JSON object. The returned
     * object will replace the given object as all of the super properties stored
     * for the current user. update should not return null.
     *
     * @param oldValues the existing super properties
     * @return a new set of super properties that will be sent with every event.
     */
    public JSONObject update(JSONObject oldValues);
}
