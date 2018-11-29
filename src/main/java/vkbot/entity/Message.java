package vkbot.entity;

import lombok.Data;
import vkbot.enums.MessageTypeEnum;
import vkbot.enums.SourceTypeEnum;


@Data
public class Message extends AbstractMessage{
    private Long messageId;
    private Integer flags;
    private Integer peerId;
    private Long ts;
    private String subject;
    private boolean forward;

    public Message(){}

    public Message(String text, Long userId, MessageTypeEnum messageType, String attachment, SourceTypeEnum sourceType, Long messageId, Integer flags, Integer peerId, Long ts, String subject, boolean forward) {
        super(text, userId, messageType, attachment, sourceType);
        this.messageId = messageId;
        this.flags = flags;
        this.peerId = peerId;
        this.ts = ts;
        this.subject = subject;
        this.forward = forward;
    }
}
