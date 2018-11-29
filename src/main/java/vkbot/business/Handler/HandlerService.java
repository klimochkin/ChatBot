package vkbot.business.Handler;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.ChatService;
import vkbot.business.GroupService;
import vkbot.entity.AbstractMessage;
import vkbot.entity.Comment;
import vkbot.entity.Message;
import vkbot.enums.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service("HandlerService")
public class HandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerService.class);

    @Autowired
    ChatService chatService;

    @Autowired
    AnswerToCommandService answerToCommandService;

    @Autowired
    GroupService groupService;

    public void handleMessages(Message msg) throws ClientException, ApiException, IOException, ParseException {

        int flag = msg.getFlags();
        boolean outbox = (flag & 2) != 0;

        if (!outbox) {
            msg.setMessageType(typologyMsg(msg.getMessageType(), msg.getUserId(), msg.getText()));
            AbstractMessage abstractMsg = msg;//new AbstractMessage(msg.getText(), msg.getUserId(), msg.getMessageType(), null);
            abstractMsg = answerToCommandService.splitter(abstractMsg);

            LOG.debug("==========================================================");
            LOG.debug("Беседа: " + msg.getSubject());
            LOG.debug("Получено сообщение: " + msg.getText());
            if (abstractMsg != null) {
                msg = (Message) abstractMsg;
                chatService.sendMessage(msg);
            } else
                LOG.debug("Не удалось типологизировать сообщение. Ответ не был сформирован.");
        }
    }

    public void handleComments(Comment com) throws IOException, ApiException, ParseException, ClientException {

        com.setMessageType(typologyMsg(com.getMessageType(), com.getUserId(), com.getText()));
        AbstractMessage abstractMsg = com;
        abstractMsg = answerToCommandService.splitter(abstractMsg);

        LOG.debug("==========================================================");
        LOG.debug("Топик: " + com.getTopicId());
        LOG.debug("Получено сообщение: " + com.getText());
        if (abstractMsg != null) {
            com = (Comment) abstractMsg;
            groupService.sendComment(com);
        } else
            LOG.debug("Не удалось типологизировать сообщение. Ответ не был сформирован.");

    }

    private MessageTypeEnum typologyMsg(MessageTypeEnum messageType, Long userId, String text) {
        MessageTypeEnum newMessageType = messageType;

        if (newMessageType == null)
            newMessageType = checkUser(userId.toString());

        if (newMessageType == null)
            newMessageType = checkCommand(text);

        if (newMessageType == null)
            newMessageType = checkNoPrefix(text);

        return newMessageType;
    }

    private MessageTypeEnum checkCommand(String text) {

        String[] lastMessage = text.split(" ");
        String prefixMsg = lastMessage[0].replace(",", "");

        boolean a = InitBot.prefix.contains(prefixMsg);
        boolean b = lastMessage.length >= 2;
        boolean c = prefixMsg.contains(InitBot.prefix.get(0));

        if ((InitBot.prefix.contains(prefixMsg) || prefixMsg.contains(InitBot.prefix.get(0))) && lastMessage.length >= 2) {
            String liter = lastMessage[1];
            for (CommandEnum commandEnum : CommandEnum.values()) {
                if (commandEnum.equalsCode(liter))
                    return commandEnum;
            }
        }
        return null;
    }

    private MessageTypeEnum checkUser(String userId) {

        if (Arrays.stream(InitBot.usersIgnore.toArray()).anyMatch(s -> s.equals(userId))) {
            return UserEnum.USER_IGNORE;
        }

        if (Arrays.stream(InitBot.usersNegative.toArray()).anyMatch(s -> s.equals(userId))) {
            return UserEnum.USER_NEGATIVE;
        }

        if (Arrays.stream(InitBot.usersPositive.toArray()).anyMatch(s -> s.equals(userId))) {
            return UserEnum.USER_POSITIVE;
        }
        return null;
    }

    private MessageTypeEnum checkNoPrefix(String text) {

        List<String> literals = new ArrayList<>();

        if (text.length() > 250) {
            text = text.substring(0, 250);
        }

        if (checkFullMessage(text, InitBot.prefix)) {
            return OtherEnum.NAME_BOT;
        } else {
            literals = Arrays.asList("нахуй", " на хуй ");
            if (checkFullMessage(text, literals))
                return OtherEnum.NAH;
        }
        return null;
    }

    public boolean checkFullMessage(String message, List<String> words) {
        for (String word : words) {
            if (message.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин HandlerService");
    }

}