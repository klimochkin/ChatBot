package vkbot.business;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;

import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.entity.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserService")
public class UserService {


    public Map<String, String> getMapUsers(List<String> userIds) {
        Map<String, String> userNames = new HashMap<>();
        try {
            List<UserXtrCounters> userList = InitBot.vk.users().get(InitBot.actor).userIds(userIds).execute();
            for(UserXtrCounters item : userList){
                userNames.put(item.getId().toString(), item.getFirstName() + " " + item.getLastName());
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return userNames;
    }

    public List<User> getUsers(List<String> userIds) {
        List<User> users = new ArrayList<>();
        try {
            List<UserXtrCounters> userList = InitBot.vk.users().get(InitBot.actor).userIds(userIds).execute();
            for(UserXtrCounters item : userList){
                users.add(new User(item));
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return users;
    }
}
