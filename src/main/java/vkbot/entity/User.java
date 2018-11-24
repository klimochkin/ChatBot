package vkbot.entity;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class User {

    private Long userId;
    private String firstName;
    private String lastName;
    private boolean online;
    private String city;
    private Boolean isFriend;
    private int sex;
    private String screenName;
    private String photo;

    public User(JSONObject userJson) {

        userId = Long.parseLong(userJson.get("id").toString());
        firstName = userJson.get("first_name").toString();
        lastName = userJson.get("last_name").toString();

        if (userJson.get("online").toString().equals("1"))
            online = true;

        city = null;
        isFriend = null;
    }

}
