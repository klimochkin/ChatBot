package vkbot.entity;

import lombok.Data;
import vkbot.enums.MessageTypeEnum;


@Data
public class Message extends AbstractMessage{
    private Long messageId;
    private Integer flags;
    private Integer peerId;
    private Long ts;
    private String subject;
    private boolean forward;
}
