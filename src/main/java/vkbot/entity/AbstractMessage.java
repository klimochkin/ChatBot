package vkbot.entity;


import lombok.Data;
import vkbot.enums.MessageTypeEnum;
import vkbot.enums.SourceTypeEnum;

@Data
public class AbstractMessage {
    private String text;
    private Long userId;
    private MessageTypeEnum messageType;
    private String attachment;
    private SourceTypeEnum sourceType;

    AbstractMessage(){
    }

    public AbstractMessage(String text, Long userId, MessageTypeEnum messageType, String attachment, SourceTypeEnum sourceType) {
        this.text = text;
        this.userId = userId;
        this.messageType = messageType;
        this.attachment = attachment;
        this.sourceType= sourceType;
    }
}
