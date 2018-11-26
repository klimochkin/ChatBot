package vkbot.business.Handler;

import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.entity.AbstractMessage;
import vkbot.entity.Message;
import vkbot.enums.SourceTypeEnum;

import java.util.List;
import java.util.Random;


@Service("AnswerToNoPrefixService")
public class AnswerToNoPrefixService {

    public AbstractMessage getAnswerCommandSHAR(AbstractMessage msg) {

        String answer = InitBot.answerNoPrefix.get(new Random().nextInt(InitBot.answerNoPrefix.size()));

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    public AbstractMessage getAnswerCommandNAH(AbstractMessage msg) {

        String answer = "Нахуй твоя жопа хороша!";
        if(msg.getSourceType().equals(SourceTypeEnum.CHAT)) {
            Message msgChat = (Message) msg;
            msgChat.setForward(true);
        }
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }
}
