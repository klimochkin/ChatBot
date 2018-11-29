package vkbot.business;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service("ManagerService")
public class ManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    GroupService groupService;

    @Autowired
    ChatService chatService;


    public void startBot(){

        Runnable chatListenerStart = () -> {
            chatService.startCycleForChat();
        };

        Runnable groupListenerStart = () -> {
            groupService.startCycleForGroups();
        };

        new Thread(chatListenerStart).start();

        new Thread(groupListenerStart).start();
    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин ManagerService");
    }
}
