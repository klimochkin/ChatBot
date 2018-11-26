package vkbot.entity;

import lombok.Data;
import vkbot.enums.MessageTypeEnum;


@Data
public class Message extends AbstractMessage{
    Long messageId;
    Integer flags;
    Integer peerId;
    Long ts;
    String subject;
}
