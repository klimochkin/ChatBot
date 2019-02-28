package vkbot.business.Handler;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.video.Video;
import com.vk.api.sdk.objects.video.responses.SearchResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.video.VideoSearchSort;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.ChatService;
import vkbot.business.GroupService;
import vkbot.business.UserService;
import vkbot.business.utils.MapComparator;
import vkbot.entity.AbstractMessage;
import vkbot.entity.Comment;
import vkbot.entity.Message;
import vkbot.entity.User;
import vkbot.enums.*;
import vkbot.external.ExternalService;
import vkbot.external.MultipartUtility;
import vkbot.external.YandexIntegration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

@Service("AnswerToCommandService")
public class AnswerToCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(AnswerToCommandService.class);

    @Autowired
    YandexIntegration yandex;

    @Autowired
    private UserService userService;

    @Autowired
    private ExternalService externalService;

    @Autowired
    private ChatService chatBusinessService;

    @Autowired
    private AnswerToNoPrefixService answerToNoPrefixService;

    @Autowired
    private GroupService groupBusinessService;


    public AbstractMessage splitter(AbstractMessage msg) throws ClientException, ApiException, IOException, ParseException, InterruptedException {

        MessageTypeEnum type = msg.getMessageType();

        if (UserEnum.USER_NEGATIVE.equals(type)) {
            return answerToNoPrefixService.getAnswerNegativeUser(msg);
        }

        if (CommandEnum.SHAR.equals(type))
            return getAnswerCommandSHAR(msg);
        if (CommandEnum.DVACH.equals(type))
            return getAnswerCommandDVACH(msg);
        if (CommandEnum.MEM.equals(type))
            return getAnswerCommandMEM(msg);
        if (CommandEnum.SISKI.equals(type))
            return getAnswerCommandSISKI(msg);
        if (CommandEnum.TYAN.equals(type))
            return getAnswerCommandTYAN(msg);
        if (CommandEnum.FIND.equals(type))
            return getAnswerCommandFIND(msg);
        if (CommandEnum.SAY.equals(type))
            return getAnswerCommandSAY(msg);
        if (CommandEnum.JOKE.equals(type))
            return getAnswerCommandJOKE(msg);
        if (CommandEnum.GIF.equals(type))
            return getAnswerCommandGIF(msg);
        if (CommandEnum.WEATHER.equals(type))
            return getAnswerCommandWEATHER(msg);
        if (CommandEnum.EURO.equals(type))
            return getAnswerCommandEURO(msg);
        if (CommandEnum.ONLINE.equals(type))
            return getAnswerCommandONLINE(msg);
        if (CommandEnum.COMMANDS.equals(type))
            return getAnswerCommandCOMMANDS(msg);
        if (OtherEnum.NAME_BOT.equals(type))
            return answerToNoPrefixService.getAnswerCommandSHAR(msg);
        if (OtherEnum.NAH.equals(type))
            return answerToNoPrefixService.getAnswerCommandNAH(msg);
        if (CommandEnum.STAT_MSG.equals(type))
            return getAnswerCommandSTATMSG(msg);
        if (CommandEnum.DETECTOR.equals(type))
            return getAnswerCommandDETECTOR(msg);
        if (CommandEnum.SEARCH.equals(type))
            return getAnswerCommandSEARCH(msg);

        return null;
    }

    private AbstractMessage getAnswerCommandSHAR(AbstractMessage msg) {
        String answer = this.getRandomItem("Да", "Конечно", "Не думаю",
                "Нет", "Знаки говорят - да", "Несомненно!",
                "Скорее да, чем нет", "Не могу решить",
                "Мой ответ - нет", "Да, но только если ты не смотришь аниме");
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandDVACH(AbstractMessage msg) throws ClientException, ApiException {
        Integer groupId = -22751485;
        Integer limit = 1985;
        AbstractMessage amsg = getPicture(groupId, limit);
        msg.setText(amsg.getText());
        msg.setAttachment(amsg.getAttachment());
        return msg;
    }

    private AbstractMessage getAnswerCommandMEM(AbstractMessage msg) throws ClientException, ApiException {
        Integer groupId = -87960594;
        Integer limit = 1985;
        AbstractMessage amsg = getPicture(groupId, limit);
        msg.setText(amsg.getText());
        msg.setAttachment(amsg.getAttachment());
        return msg;
    }

    private AbstractMessage getAnswerCommandSISKI(AbstractMessage msg) throws ClientException, ApiException {
        Integer groupId = -80461135;
        Integer limit = 1985;
        AbstractMessage amsg = getPicture(groupId, limit);
        msg.setText(amsg.getText());
        msg.setAttachment(amsg.getAttachment());
        return msg;
    }

    private AbstractMessage getAnswerCommandTYAN(AbstractMessage msg) throws ClientException, ApiException {
        Integer groupId = -83971955;
        Integer limit = 1985;
        AbstractMessage amsg = getPicture(groupId, limit);
        msg.setText(amsg.getText());
        msg.setAttachment(amsg.getAttachment());
        return msg;
    }

    private AbstractMessage getAnswerCommandFIND(AbstractMessage msg) throws ClientException, ApiException {
        String answer;
        String attach = null;
        String[] strList = msg.getText().split("видео ");

        if (strList.length == 2) {
            String text = strList[1].trim();
            SearchResponse response = InitBot.vk.videos().search(InitBot.actor, text).sort(VideoSearchSort.BY_RELEVANCE).adult(false).execute();
            if (response != null) {
                int size = response.getItems().size();
                if (size != 0) {
                    int index = new Random().nextInt(size);
                    Video video = response.getItems().get(index);
                    answer = "Вот, что я нашел: ";
                    attach = "video" + video.getOwnerId() + "_" + video.getId();
                    //      LOG.debug("Видео: " + video.toString());
                } else
                    answer = "Не смог ничего найти &#128546;";
            } else
                answer = "Ошибка поиска";
        } else
            answer = "Что мне искать?";

        msg.setText(answer);
        msg.setAttachment(attach);
        return msg;
    }

    private AbstractMessage getAnswerCommandSAY(AbstractMessage msg) throws ClientException, ApiException, IOException, ParseException {
        String answer;
        String attach = null;
        String[] strList = msg.getText().split("скажи ");
        if (strList.length == 2) {
            String text = strList[1].trim();
            String uploadUrl = InitBot.vk.docs().getUploadServer(InitBot.actor).unsafeParam("type", "audio_message").execute().getUploadUrl();
            MultipartUtility multipart = new MultipartUtility(uploadUrl, "UTF-8");
            multipart.addBytesPart("file", yandex.speech(text));
            List<String> multi = multipart.finish();
            String fileUpl = "";
            for (String line : multi) {
                JSONObject obj = (JSONObject) new JSONParser().parse(line.toString());
                fileUpl = (String) obj.get("file");
            }

            Doc doc = InitBot.vk.docs().save(InitBot.actor, fileUpl).execute().get(0);
            attach = "doc" + doc.getOwnerId() + "_" + doc.getId();
            answer = "";
        } else
            answer = "Что мне сказать?";
        msg.setText(answer);
        msg.setAttachment(attach);
        return msg;
    }

    private AbstractMessage getAnswerCommandJOKE(AbstractMessage msg) throws IOException {

        String answer = externalService.getBash();

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandGIF(AbstractMessage msg) throws ClientException, ApiException {
        String answer;
        String attach = null;
        String[] strList = msg.getText().split("гиф ");

        if (strList.length == 2) {
            String text = strList[1].trim();

            com.vk.api.sdk.objects.docs.responses.SearchResponse response = InitBot.vk.docs().search(InitBot.actor, text).count(10).offset(new Random().nextInt(300)).execute();
            int size = response.getItems().size();
            if (size > 0) {
                Doc doc = response.getItems().get(new Random().nextInt(size));
                answer = "Вот, что я нашел";
                attach = "doc" + doc.getOwnerId() + "_" + doc.getId();
            } else
                answer = "Не смог ничего найти &#128546;";
        } else
            answer = "Что мне искать?";
        msg.setText(answer);
        msg.setAttachment(attach);
        return msg;
    }

    private AbstractMessage getAnswerCommandWEATHER(AbstractMessage msg) throws IOException, ParseException {
        String answer;
        String[] strList = msg.getText().split("погода ");

        if (strList.length == 2) {
            String text = strList[1].trim();

            answer = externalService.getWeatherYahoo(text);
        } else
            answer = "фиаско потерпел я";
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandEURO(AbstractMessage msg) throws IOException, ParseException {
        String answer = externalService.getFixer();

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandONLINE(AbstractMessage msg) throws ApiException, ClientException, ParseException, IOException, InterruptedException {
        boolean flag = true;
        String answer = "Модуль не подключен";
        List<User> users = null;
        if (SourceTypeEnum.CHAT.equals(msg.getSourceType())) {
            Message chatMessage = (Message) msg;
            users = chatBusinessService.getChatUser(chatMessage.getPeerId().toString());
        }
        if (SourceTypeEnum.GROUP.equals(msg.getSourceType())) {
            Comment groupMessage = (Comment) msg;
            users = groupBusinessService.getUserTopic(groupMessage.getGroupId() * -1, groupMessage.getTopicId());
        }
        StringBuilder strAnswer = new StringBuilder();
        strAnswer.append("Сейчас в онлайне: \n");
        for (User user : users) {
            if (user.isOnline()) {
                strAnswer.append("[id" + user.getUserId());
                strAnswer.append("|" + user.getFirstName());
                strAnswer.append(" " + user.getLastName() + "]\n");
                flag = false;
            }
        }
        if (flag)
            answer = "Нет никого!";
        else
            answer = strAnswer.toString();

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandCOMMANDS(AbstractMessage msg) {
        StringBuilder strAnswer = new StringBuilder();
        strAnswer.append("Список команд: \n");
        for (CommandEnum commandEnum : CommandEnum.values()) {
            strAnswer.append(commandEnum.getDescription() + "\n");
        }
        String answer = strAnswer.toString();
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandSTATMSG(AbstractMessage msg) {
        if (SourceTypeEnum.GROUP.equals(msg.getSourceType())) {
            msg.setText("Команда доступна только для групповых бесед");
            msg.setAttachment(null);
            return msg;
        }

        StringBuilder answer = new StringBuilder("Статистика сообщений:  \n");
        List<String> userIds = new ArrayList<>();
        Message message = (Message) msg;

        Map<String, Integer> respMap = chatBusinessService.statisticChatMessage(message.getPeerId());

        Map<String, Integer> sortedMap = respMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e2, e1) -> e2, LinkedHashMap::new));

        userIds.addAll(sortedMap.keySet());
        Map<String, String> users = userService.getMapUsers(userIds);

        for (String key : sortedMap.keySet()) {
            answer.append(users.get(key)).append(" - ").append(sortedMap.get(key).toString()).append("\n");
        }

        msg.setText(answer.toString());
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandDETECTOR(AbstractMessage msg) {
        StringBuilder answer = new StringBuilder("Уровень ЧСВ: \n");

        Integer countLiter;
        Integer countMsg;
        Pattern p = Pattern.compile("\\bя\\b|\\bменя\\b|\\bмне\\b|\\bмой\\b|\\bмоя\\b|\\bмоё\\b|\\bмою\\b|\\bмоего\\b", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Map<String, Integer> mapCountLiter = new HashMap<>();
        Map<String, Integer> mapCountMsg = new HashMap<>();
        List<AbstractMessage> listAbsMsg = new ArrayList<>();
        String text;
        if (SourceTypeEnum.CHAT.equals(msg.getSourceType())) {
            Message message = (Message) msg;
            List<Message> listMsg = chatBusinessService.getChatMessages(message.getPeerId());
            listAbsMsg.addAll(listMsg);
        }

        if (SourceTypeEnum.GROUP.equals(msg.getSourceType())) {
            Comment comment = (Comment) msg;
            Integer limit = null;
            String[] strList = msg.getText().split("чсв ");
            try {
                if (strList.length == 2) {
                    limit = Integer.parseInt(strList[1].trim());
                }
            } catch (Exception e) {
                limit = null;
            }
            List<Comment> listComm = groupBusinessService.getComments(comment.getGroupId() * -1, comment.getTopicId(), limit);
            listAbsMsg.addAll(listComm);
        }
        for (AbstractMessage item : listAbsMsg) {
            countLiter = mapCountLiter.get(item.getUserId().toString());
            countMsg = mapCountMsg.get(item.getUserId().toString());
            text = item.getText();

            int count = 0;
            Matcher m = p.matcher(text);
            while (m.find()) count++;

            if (countLiter == null && countMsg == null) {
                countMsg = 1;
                countLiter = count;
                mapCountMsg.put(item.getUserId().toString(), countMsg);
                mapCountLiter.put(item.getUserId().toString(), countLiter);
            } else {
                countMsg = countMsg + 1;
                countLiter = countLiter + count;
                mapCountMsg.put(item.getUserId().toString(), countMsg);
                mapCountLiter.put(item.getUserId().toString(), countLiter);
            }
        }

        for (String key : mapCountLiter.keySet()) {
            countMsg = mapCountMsg.get(key);
            if (countMsg > 100) {
                countLiter = mapCountLiter.get(key);
                Integer value = countLiter * 100 / countMsg;
                mapCountLiter.put(key, value);
            } else {
                mapCountLiter.put(key, 0);
            }
        }
        Map<String, Integer> sortedMap = mapCountLiter.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e2, e1) -> e2, LinkedHashMap::new));

        List<String> userIds = new ArrayList<>();
        userIds.addAll(sortedMap.keySet());
        Map<String, String> users = userService.getMapUsers(userIds);

        for (String key : sortedMap.keySet()) {
            countMsg = sortedMap.get(key);
            if (countMsg != 0) {
                answer.append(users.get(key)).append(" - ").append(sortedMap.get(key).toString()).append(" (Сообщений: " + mapCountMsg.get(key) + ")\n");
            }
        }
        answer.append("\nВсего сообщений обработано: " + listAbsMsg.size() + " \n");

        msg.setText(answer.toString());
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandSEARCH(AbstractMessage msg) {
        String answer = "Ошибка поиска!";
        String[] strList = msg.getText().split("найди ");

        if (strList.length == 2) {
            try {
                answer = externalService.googleSearch(strList[1].trim());
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        } else
            answer = "Что мне искать?";

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }



    private String getRandomItem(String... words) {
        return words[new Random().nextInt(words.length)];
    }

    private AbstractMessage getPicture(Integer groupId, Integer limit) throws ClientException, ApiException {

        GetResponse response = InitBot.vk.wall().get(InitBot.actor).ownerId(groupId).offset(new Random().nextInt(limit)).count(1).execute();
        Photo photo = response.getItems().get(0).getAttachments().get(0).getPhoto();
        String text = response.getItems().get(0).getText();
        if (text == null || text.isEmpty())
            text = this.getRandomItem("Ну держи!", "Не баян (баян)", "Каеф", "&#127770;");

        String attach = "photo" + photo.getOwnerId() + "_" + photo.getId() + "_" + photo.getAccessKey();

        return new AbstractMessage(text, null, null, attach, null, null);
    }


    @PostConstruct
    public void test() {
        LOG.debug("Создан бин AnswerToCommandService");
    }

}
