package vkbot.entity;


import lombok.Data;

import java.util.List;

@Data
public class Topic {

    Integer topicId;
    List<Comment> listTopicComments;
    Integer lastCommentId;


    public Topic(Integer topicId, List<Comment> listTopicComments, Integer lastCommentId){
        this.topicId = topicId;
        this.listTopicComments = listTopicComments;
        this.lastCommentId = lastCommentId;
    }

}
