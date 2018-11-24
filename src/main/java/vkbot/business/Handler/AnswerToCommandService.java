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
import vkbot.business.impl.ChatBusinessServiceImpl;
import vkbot.entity.AbstractMessage;
import vkbot.entity.Message;
import vkbot.entity.User;
import vkbot.enums.CommandEnum;
import vkbot.enums.MessageTypeEnum;
import vkbot.enums.OtherEnum;
import vkbot.enums.SourceTypeEnum;
import vkbot.external.ExternalService;
import vkbot.external.MultipartUtility;
import vkbot.external.YandexIntegration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service("AnswerToCommandService")
public class AnswerToCommandService {

    private static final Logger LOG = LoggerFactory.getLogger(AnswerToCommandService.class);

    @Autowired
    private YandexIntegration yandex;

    @Autowired
    private ExternalService externServ;

    @Autowired
    private ChatBusinessServiceImpl chatBusinessService;

    @Autowired
    private AnswerToNoPrefixService answerToNoPrefixService;



    public AbstractMessage splitter(AbstractMessage msg) throws ClientException, ApiException, IOException, ParseException {

        MessageTypeEnum type = msg.getMessageType();

        if (CommandEnum.SHAR.equals(type)) {
            return getAnswerCommandSHAR(msg);
        } else if (CommandEnum.DVACH.equals(type)) {
            return getAnswerCommandDVACH(msg);
        } else if (CommandEnum.MEM.equals(type)) {
            return getAnswerCommandMEM(msg);
        } else if (CommandEnum.SISKI.equals(type)) {
            return getAnswerCommandSISKI(msg);
        } else if (CommandEnum.TYAN.equals(type)) {
            return getAnswerCommandTYAN(msg);
        } else if (CommandEnum.FIND.equals(type)) {
            return getAnswerCommandFIND(msg);
        } else if (CommandEnum.SAY.equals(type)) {
            return getAnswerCommandSAY(msg);
        } else if (CommandEnum.JOKE.equals(type)) {
            return getAnswerCommandJOKE(msg);
        } else if (CommandEnum.GIF.equals(type)) {
            return getAnswerCommandGIF(msg);
        } else if (CommandEnum.WEATHER.equals(type)) {
            return getAnswerCommandWEATHER(msg);
        } else if (CommandEnum.EURO.equals(type)) {
            return getAnswerCommandEURO(msg);
        } else if (CommandEnum.ONLINE.equals(type)) {
            return getAnswerCommandONLINE(msg);
        } else if (CommandEnum.COMMANDS.equals(type)) {
            return getAnswerCommandCOMMANDS(msg);
        } else if (OtherEnum.NAME_BOT.equals(type)) {
            return answerToNoPrefixService.getAnswerCommandSHAR(msg);
        }
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
        String[] strList = msg.getText().split("найди ");

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

        String answer = externServ.getBash();

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

            answer = externServ.getWeatherYahoo(text);
        } else
            answer = "фиаско потерпел я";
        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandEURO(AbstractMessage msg) throws IOException, ParseException {
        String answer = externServ.getFixer();

        msg.setText(answer);
        msg.setAttachment(null);
        return msg;
    }

    private AbstractMessage getAnswerCommandONLINE(AbstractMessage msg) throws ApiException, ClientException, ParseException, IOException {
        Message chatMessage;
        String answer = "Модуль не подключен";
        if (SourceTypeEnum.CHAT.equals(msg.getSourceType())) {
            chatMessage = (Message) msg;
            boolean flag = true;
            List<User> users = chatBusinessService.getChatUser(chatMessage.getPeer_id().toString());
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
        }
        if (SourceTypeEnum.GROUP.equals(msg.getSourceType())) {
            answer = "Не реализовано";
        }
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

        return new AbstractMessage(text, null, null, attach);
    }


    @PostConstruct
    public void test() {
        LOG.debug("Создан бин AnswerToCommandService");
    }

}
