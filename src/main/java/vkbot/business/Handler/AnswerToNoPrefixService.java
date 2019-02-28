package vkbot.business.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.UserService;
import vkbot.entity.AbstractMessage;
import vkbot.entity.Message;
import vkbot.entity.User;
import vkbot.enums.SourceTypeEnum;

import javax.annotation.PostConstruct;
import java.util.*;


@Service("AnswerToNoPrefixService")
public class AnswerToNoPrefixService {

    private static final Logger LOG = LoggerFactory.getLogger(AnswerToNoPrefixService.class);

    Map usersPatam;

    @Autowired
    private UserService userService;

    public AbstractMessage getAnswerNegativeUser(AbstractMessage msg) {

        List<String> userIds = new ArrayList<>();
        userIds.add(msg.getUserId().toString());
        List<User> userd = userService.getUsers(userIds);
        User user = userService.getUsers(userIds).get(0);
        String answer = "хм";

        if(user.getSex() == 1) {
            answer = InitBot.answersNegativeWoman.get(new Random().nextInt(InitBot.answersNegativeWoman.size()));
        }

        if(user.getSex() == 2) {
            answer = InitBot.answersNegativeMan.get(new Random().nextInt(InitBot.answersNegativeMan.size()));
        }

        if (msg.getSourceType().equals(SourceTypeEnum.CHAT)) {
            Message msgChat = (Message) msg;
            msgChat.setForward(true);
        }

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    public AbstractMessage getAnswerCommandSHAR(AbstractMessage msg) {

        String answer = InitBot.answerNoPrefix.get(new Random().nextInt(InitBot.answerNoPrefix.size()));
        if (msg.getSourceType().equals(SourceTypeEnum.CHAT)) {
            Message msgChat = (Message) msg;
            msgChat.setForward(true);
        }
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    public AbstractMessage getAnswerCommandNAH(AbstractMessage msg) {

        String answer = "Нахуй твоя жопа хороша!";
        if (msg.getSourceType().equals(SourceTypeEnum.CHAT)) {
            Message msgChat = (Message) msg;
            msgChat.setForward(true);
        }
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    @PostConstruct
    public void test() {
        usersPatam = new HashMap<String, Integer> ();
        usersPatam.put("483868102", 5);

        LOG.debug("Создан бин AnswerToNoPrefixService");
    }
}
