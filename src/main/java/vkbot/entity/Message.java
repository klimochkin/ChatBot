package vkbot.entity;

import lombok.Data;
import vkbot.enums.MessageTypeEnum;


@Data
public class Message extends AbstractMessage{
    Long message_id ;
    Integer flags;
    Integer peer_id;
    Long ts;
    String subject;
}
