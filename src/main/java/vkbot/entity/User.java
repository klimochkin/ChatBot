package vkbot.entity;

import com.vk.api.sdk.objects.users.UserXtrCounters;
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

    public User(UserXtrCounters item) {

        this.userId = new Long(item.getId());
        this.firstName = item.getFirstName();
        this.lastName = item.getLastName();
        this.online = item.isOnline();
        this.city = item.getCity().toString();
        this.isFriend = false;
        this.sex = item.getSex().getValue();
        this.screenName = item.getScreenName();
        this.photo = item.getPhoto200();
    }
}
