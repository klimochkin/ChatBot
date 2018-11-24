package vkbot.entity;


import com.vk.api.sdk.objects.board.TopicComment;
import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class Comment extends AbstractMessage {
    private int commentId;
    private Integer date;
    private String userName;
    private Integer groupId;
    private Integer topicId;

    public Comment(TopicComment item, Integer groupId, Integer topicId){

        super(item.getText(), item.getFromId(), null, null);

        this.commentId = item.getId();
        this.date = item.getDate();
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

    public Comment(JSONObject item, String userName, String groupId, String topicId) {

        idComment = Integer.parseInt(item.get("id").toString());
      //  idUser = Long.parseLong(item.get("from_id").toString());
        text = item.get("text").toString();
        date = item.get("id").toString();
        attachments = null;
        this.userName = userName;
        this.groupId = groupId;
        this.topicId = topicId;
    }
*/

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
