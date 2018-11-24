package vkbot.business.impl;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.board.TopicComment;
import com.vk.api.sdk.objects.newsfeed.NewsfeedItem;
import com.vk.api.sdk.queries.board.BoardGetCommentsSort;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetCommentsFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.entity.Comment;
import vkbot.entity.User;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("GroupBusinessServiceImpl")
public class GroupBusinessServiceImpl {

    private final Logger LOG = LoggerFactory.getLogger(LongPollBusinessServiceImpl.class);

    private Map<Integer, Integer> topicList;

    public GroupBusinessServiceImpl() {
        init();
    }

    //todo: Сделать цикл получения сообщений с групп
    public void receivingComments() {
        while (true) {
            for (Integer key : topicList.keySet()) {
               // List<Comment> list = getComments(key, topicList.get(key));
            }


        }
    }

    public void sendComment(Comment comment) throws ClientException, ApiException {
        InitBot.vk.board().createComment(InitBot.actor, comment.getGroupId(), comment.getTopicId())
                .message(comment.getText())
                .attachments()
                .execute();
    }

    public List<Comment> getComments(Integer groupId, Integer topicId, int startCommentId) throws IOException, ParseException, ClientException, ApiException {
        List commentList = new ArrayList<Comment>();

        List<TopicComment> topicCommentList = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId).sort(BoardGetCommentsSort.ASC).startCommentId(startCommentId).execute().getItems();
        if (topicCommentList.size() < 1) {
            for (TopicComment item : topicCommentList) {
                commentList.add(new Comment(item, groupId, topicId));
            }
            return commentList;
        } else
            return null;
    }

    public List<User> getUserTopic(Integer groupId, Integer topicId) throws ClientException, ParseException {
        List users = new ArrayList<User>();
        Integer commenId = null;
        int N = 0;
        while (N == 2) {
            String jsonStr = null;
            if (N == 0)
                jsonStr = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId).count(100).unsafeParam("extended", "1").sort(BoardGetCommentsSort.DESC).executeAsString();

            if (N == 1)
                jsonStr = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId).count(100).unsafeParam("extended", "1").sort(BoardGetCommentsSort.DESC).startCommentId(commenId).offset(-100).executeAsString();

            JSONObject commentsJSON = (JSONObject) new JSONParser().parse(jsonStr);
            JSONObject response = (JSONObject) commentsJSON.get("response");
            if (response != null) {
                commenId = Integer.parseInt(((JSONObject) ((JSONArray) response.get("items")).get(0)).get("id").toString());

                JSONArray usersJson = (JSONArray) response.get("profiles");
                for (int i = 0; i < usersJson.size(); i++) {
                    JSONObject userJson = (JSONObject) usersJson.get(i);
                    User user = new User(userJson);
                    users.add(user);
                }
            } else
                throw new RuntimeException("Неудалось получить список юзеров");
            N++;
        }

        return users;
    }

    public List<Comment> newsFeedComments() {// throws IOException, ParseException, ClientException, ApiException {

        int count = 5;
        List commentList = new ArrayList<Comment>();
        try {
            List<NewsfeedItem> topicCommentList = InitBot.vk.newsfeed().getComments(InitBot.actor).count(5).filters(NewsfeedGetCommentsFilter.TOPIC).unsafeParam("last_comments_count", "1").execute().getItems();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    private void init() {
        List<Comment> commentList = newsFeedComments();
        for (Comment comment : commentList) {
            this.topicList.put(comment.getGroupId(), comment.getTopicId());
        }
    }


    public static StringBuilder bildPrefixName(String userId, String groupId, Comment comment) {
        StringBuilder strBild = new StringBuilder();
        strBild.append("[id").append(userId)
                .append(":bp-").append(groupId.replace("-", ""))
                .append("_").append(comment.getCommentId())
                .append("|").append(comment.getUserName()).append("], ");
        return strBild;
    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин GroupBusinessServiceImpl");
    }
}
