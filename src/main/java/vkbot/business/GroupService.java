package vkbot.business;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.board.TopicComment;
import com.vk.api.sdk.queries.board.BoardGetCommentsSort;
import com.vk.api.sdk.queries.newsfeed.NewsfeedGetCommentsFilter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.Handler.HandlerService;
import vkbot.entity.Comment;
import vkbot.entity.Topic;
import vkbot.entity.User;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("GroupService")
public class GroupService {

    private final Logger LOG = LoggerFactory.getLogger(LongPollService.class);

    @Autowired
    private HandlerService handlerService;

    private Map<Integer, Integer> topicCache;

    public GroupService() {
    }

    public void startCycleForGroups() {
        int i = 0;
        while (true) {
            try {
                List<Topic> topics = getNewsFeedComments();
                hendlerCommentsList(topics);
                TimeUnit.SECONDS.sleep(3);
            } catch (IOException | ParseException | ApiException | ClientException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void hendlerCommentsList(List<Topic> topics) throws ClientException, ApiException, ParseException, IOException, InterruptedException {

        for (Topic topic : topics) {
            Integer newLastCommentId = topic.getLastCommentId();
            Integer oldLastCommentId = topicCache.get(topic.getTopicId());
            List<Comment> comments = topic.getListTopicComments();

            if (newLastCommentId > oldLastCommentId) {
                comments.removeIf(p -> p.getId() <= oldLastCommentId);
                topicCache.put(topic.getTopicId(), newLastCommentId);

                for (Comment comment : comments) {
                    handlerService.handleComments(comment);
                }
            }
        }
    }

/*
    //TODO: Сделать цикл получения сообщений с групп
    public void receivingComments() {
        while (true) {
            for (Integer key : topicList.keySet()) {
                // List<Comment> list = getComments(key, topicList.get(key));
            }
        }
    }
*/

    public void sendComment(Comment comment) throws ClientException, ApiException {
        String answer = buildPrefixName(comment);

        InitBot.vk.board().createComment(InitBot.actor, comment.getGroupId() * -1, comment.getTopicId())
                .message(answer)
                .attachments(comment.getAttachment())
                .execute();
    }

/*    public List<Comment> getCommentsOld(Integer groupId, Integer topicId, int startCommentId) throws IOException, ParseException, ClientException, ApiException {
        List commentList = new ArrayList<Comment>();

        List<TopicComment> topicCommentList = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId)
                .sort(BoardGetCommentsSort.ASC)
                .startCommentId(startCommentId)
                .execute()
                .getItems();

        if (topicCommentList.size() < 1) {
            for (TopicComment item : topicCommentList) {
                commentList.add(new Comment(item, groupId, topicId));
            }
            return commentList;
        } else
            return null;
    }*/

    public List<User> getUserTopic(Integer groupId, Integer topicId) {
        List users = new ArrayList<User>();
        Set<User> usersSet = new HashSet<>();
        Integer commenId = null;
        int N = 0;
        try {
            int countComm = 0;
            while (N < 30) {
                String jsonStr = null;
                if (N == 0)
                    jsonStr = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId)
                            .count(100)
                            .unsafeParam("extended", "1")
                            .sort(BoardGetCommentsSort.DESC)
                            .executeAsString();

                if (N > 0)
                    jsonStr = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId)
                            .count(100)
                            .unsafeParam("extended", "1")
                            .sort(BoardGetCommentsSort.DESC)
                            .startCommentId(commenId)
                            .offset(100)
                            .executeAsString();

                JSONObject commentsJSON = (JSONObject) new JSONParser().parse(jsonStr);
                JSONObject response = (JSONObject) commentsJSON.get("response");
                if (response != null) {
                    commenId = Integer.parseInt(((JSONObject) ((JSONArray) response.get("items")).get(0)).get("id").toString());
                    JSONArray itemsJson = (JSONArray) response.get("items");
                    JSONArray usersJson = (JSONArray) response.get("profiles");
                    for (int i = 0; i < usersJson.size(); i++) {
                        JSONObject userJson = (JSONObject) usersJson.get(i);
                        User user = new User(userJson);
                        usersSet.add(user);
                    }
                    countComm += itemsJson.size();
                    LOG.debug("Найдено сообщений: " + countComm);
                    LOG.debug("Найдено юзеров: " + usersSet.size());
                    if (itemsJson.size() < 100) {
                        break;
                    }
                } else
                    throw new RuntimeException("Неудалось получить список юзеров");
                TimeUnit.MILLISECONDS.sleep(200);
                N++;
            }
            users.addAll(usersSet);
        } catch (InterruptedException | ParseException | ClientException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Comment> getComments(Integer groupId, Integer topicId, Integer limit) {
        Integer commentId = null;
        List commentList = new ArrayList<Comment>();
        int N = 0;
        if (limit == null) {
            limit = 40;
        } else {
            limit = limit / 100;
        }
        try {
            while (N < limit) {
                List<TopicComment> topicCommentList;
                if (N == 0) {
                    topicCommentList = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId)
                            .count(100)
                            .sort(BoardGetCommentsSort.DESC)
                            .execute()
                            .getItems();
                } else {
                    topicCommentList = InitBot.vk.board().getComments(InitBot.actor, groupId, topicId)
                            .count(100)
                            .sort(BoardGetCommentsSort.DESC)
                            .startCommentId(commentId)
                            .offset(100)
                            .execute()
                            .getItems();
                }
                if (topicCommentList.size() > 1) {
                    commentId = topicCommentList.get(0).getId();
                    for (TopicComment item : topicCommentList) {
                        commentList.add(new Comment(item, groupId, topicId));
                    }
                } else {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(200);
                N++;
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    public List<Topic> getNewsFeedComments() {

        boolean firstIter = false;
        if (topicCache == null) {
            topicCache = new HashMap();
            firstIter = true;
        }
        List<Topic> topics = new ArrayList<>();
        Integer lastCommentId = 0;
        try {
            String strRespons = InitBot.vk.newsfeed().getComments(InitBot.actor)
                    .count(5)
                    .filters(NewsfeedGetCommentsFilter.TOPIC)
                    .unsafeParam("last_comments_count", "5")
                    .executeAsString();

            JSONObject commentsJSON = (JSONObject) new JSONParser().parse(strRespons);
            JSONObject response = (JSONObject) commentsJSON.get("response");
            if (response != null) {
                JSONArray profiles = (JSONArray) response.get("profiles");
                JSONArray items = (JSONArray) response.get("items");
                String userName = "";
                String userId = "";
                String userFromId = "";
                Integer groupId = 0;
                Integer topicId = 0;
                for (int i = 0; i < items.size(); i++) {
                    List commentList = new ArrayList<Comment>();
                    JSONObject item = (JSONObject) items.get(i);
                    JSONObject topicCommentsJSON = (JSONObject) item.get("comments");
                    JSONArray listCommentsJSON = (JSONArray) topicCommentsJSON.get("list");

                    int size = listCommentsJSON.size();
                    for (int n = 0; n < size; n++) {
                        JSONObject commentJSON = (JSONObject) listCommentsJSON.get(n);
                        userFromId = commentJSON.get("from_id").toString();
                        if (n == size - 1)
                            lastCommentId = Integer.parseInt(commentJSON.get("id").toString());

                        for (int j = 0; j < profiles.size(); j++) {
                            JSONObject profile = (JSONObject) profiles.get(j);
                            userId = profile.get("id").toString();
                            if (userId.equals(userFromId)) {
                                userName = profile.get("first_name").toString();
                                break;
                            }
                        }
                        groupId = Integer.parseInt(item.get("source_id").toString());
                        topicId = Integer.parseInt(item.get("post_id").toString());

                        Comment comment = new Comment(commentJSON, userName, groupId, topicId);
                        commentList.add(comment);
                    }
                    if (firstIter)
                        topicCache.put(topicId, lastCommentId);

                    topics.add(new Topic(topicId, commentList, lastCommentId));
                }
            }
        } catch (ClientException | ParseException e) {
            e.printStackTrace();
        }
        return topics;
    }

    public String buildPrefixName(Comment comment) {
        StringBuilder strBild = new StringBuilder();
        strBild.append("[id").append(comment.getUserId())
                .append(":bp-").append(comment.getGroupId().toString().replace("-", ""))
                .append("_").append(comment.getCommentId())
                .append("|").append(comment.getUserName()).append("], ");
        strBild.append(comment.getText());
        return strBild.toString();
    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин GroupService");
    }
}
