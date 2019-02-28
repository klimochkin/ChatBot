package vkbot.entity;

import com.vk.api.sdk.objects.messages.MessageAttachment;
import lombok.Data;
import vkbot.enums.SourceTypeEnum;

import java.util.List;


@Data
public class Message extends AbstractMessage{
    private Long messageId;
    private Integer flags;
    private Integer peerId;
    private Long ts;
    private String subject;
    private boolean forward;

    public Message(){}

    public Message(com.vk.api.sdk.objects.messages.Message msg){

        super(msg.getBody().toLowerCase().replace("\"", ""), msg.getUserId().longValue(), null, null, SourceTypeEnum.CHAT, msg.getDate());
        this.setMessageId(msg.getId().longValue());
        this.setPeerId(msg.getChatId());

    }

}
