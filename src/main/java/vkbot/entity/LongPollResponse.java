package vkbot.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class LongPollResponse {

    private Integer ts;
    private List<JsonArray> updates;

    public LongPollResponse(JSONObject json) throws JSONException {
        this.ts = json.getInt("ts");
        List<JSONArray> updates = new ArrayList<>();
        JSONArray updateArr = (JSONArray) json.get("updates");
        for (int i = 0; i < updateArr.length(); i++) {
            updates.add(updates.get(i));
        }
     //   this.updates = updates;
    }

    public LongPollResponse(){
    }
    public Integer getTs() {
        return this.ts;
    }

    public List<JsonArray> getUpdates() {
        return this.updates;
    }


}
