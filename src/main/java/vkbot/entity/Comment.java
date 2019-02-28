package vkbot.entity;


import com.vk.api.sdk.objects.board.TopicComment;
import lombok.Data;
import org.json.simple.JSONObject;
import vkbot.enums.SourceTypeEnum;

@Data
public class Comment extends AbstractMessage {
    private Long id;
    private int commentId;
    private String userName;
    private Integer groupId;
    private Integer topicId;


    public Comment(TopicComment item, Integer groupId, Integer topicId){

        super(item.getText(), item.getFromId().longValue(), null, null, SourceTypeEnum.GROUP, item.getDate());

        this.commentId = item.getId();
        this.groupId = groupId;
        this.topicId = topicId;
    }


/*
    public Comment(JSONObject item){

        idComment = Integer.parseInt(item.get("id").toString());
     //   idUser = Long.parseLong(item.get("from_id").toString());
        text = item.get("text").toString();
        date = item.get("id").toString();
        attachments = null;
    }
*/
    public Comment(JSONObject item, String userName, Integer groupId, Integer topicId) {
        super(item.get("text").toString().toLowerCase().replace("\"", ""), Long.parseLong(item.get("from_id").toString()), null, null, SourceTypeEnum.GROUP,null);

        commentId = Integer.parseInt(item.get("id").toString());
        this.userName = userName;
        this.groupId = groupId;
        this.topicId = topicId;
        this.id = Long.parseLong(item.get("id").toString());

    }


/*
    public Comment(NewsfeedItem item, UserFull profile, GroupFull group){

        idComment = item.getSourceId();

        idUser = item.
                idUser = Long.parseLong(item.get("from_id").toString());
        text = item.get("text").toString();
        date = item.get("id").toString();
        attachments = null;
    }
*/

}
