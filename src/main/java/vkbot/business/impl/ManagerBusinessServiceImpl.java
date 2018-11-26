package vkbot.business.impl;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.ChatBusinessService;
import vkbot.business.GroupBusinessService;
import vkbot.business.LongPollBusinessService;
import vkbot.business.ManagerBusinessService;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Service("ManagerBusinessServiceImpl")
public class ManagerBusinessServiceImpl implements ManagerBusinessService {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerBusinessServiceImpl.class);

    @Autowired
    GroupBusinessService groupBusinessService;

    @Autowired
    ChatBusinessService chatBusinessService;


    @Override
    public void startBot(){

        Runnable chatListenerStart = () -> {
            chatBusinessService.startCycleForChat();
        };

        Runnable groupListenerStart = () -> {
            groupBusinessService.startCycleForGroups();
        };

        new Thread(chatListenerStart).start();

        new Thread(groupListenerStart).start();
    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин ManagerBusinessServiceImpl");
    }
}
