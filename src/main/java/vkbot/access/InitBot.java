package vkbot.access;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import vkbot.entity.DataBot;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static com.sun.javafx.scene.control.skin.Utils.getResource;

@Service("InitBot")
public class InitBot {

    private static final Logger LOG = LoggerFactory.getLogger(InitBot.class);
    public static UserActor actor;
    public static DataBot dataBot;
    public static VkApiClient vk;

    public static List<String> usersPositive;
    public static List<String> usersNegative;
    public static List<String> usersIgnore;
    public static List<String> prefix;
    public static List<String> answerNoPrefix;

    public static Map<String, String> weatherCods;
    public static Map<Integer, Integer> topicListLastComment;

    public InitBot() {
        this.dataBot = LoaderConfig();
        actor = new UserActor(512877743, dataBot.getVkToken()[0]);

        usersPositive = LoadListFromFile("users/user_positive.txt");
        usersNegative = LoadListFromFile("users/user_negative.txt");
        prefix = LoadListFromFile("literals.txt");
        weatherCods = LoadMapFromFile("yahoo/weather_cods.txt");
        answerNoPrefix = LoaderFile("answer/no_prefix.txt");
        usersIgnore = LoadListFromFile("users/user_ignore.txt");
        topicListLastComment = convert(LoadMapFromFile("topic_cache.txt"));

        vk = new VkApiClient(HttpTransportClient.getInstance());
    }

    public DataBot LoaderConfig() {
        DataBot dataBot = new DataBot();
        Properties botConfig = new Properties();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.properties"), "UTF8"))) {
            botConfig.load(buff);
            dataBot.setVkToken(botConfig.getProperty("vkToken").split(";"));
            dataBot.setVersion(botConfig.getProperty("version"));
            dataBot.setYandexToken(botConfig.getProperty("yandexWeatherToken"));
        } catch (IOException e) {
            LOG.error("Не найден файл конфигурации.");
        }
        return dataBot;
    }

    private Map<Integer, Integer> convert(Map<String, String> map){

        Map<Integer, Integer> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            Integer newKey = Integer.parseInt(key);
            Integer newValue = Integer.parseInt(map.get(key));
            newMap.put(newKey, newValue);
        }
        return newMap;
    }

/*    public void writerFile(Map<Integer, Integer> topicList, String filePath) {

        String location = "classpath:public/" + filePath;

        try (FileWriter writer = new FileWriter(location, false)) {

            for (Integer key : topicList.keySet()) {
                writer.write(key);
                writer.append('E');
                writer.write(topicList.get(key));
                writer.append('\n');
            }
            writer.flush();

        } catch (IOException ex) {}
    }*/

    public static void writerFile(Map<Integer, Integer> topicList, String filePath) throws URISyntaxException {

       // String location = "classpath:public/" + filePath;

        String location = "public/" + filePath;
        //   File file = new File(location);
        URL resourceUrl = getResource(location);

        File file = new File(resourceUrl.toURI());
        //OutputStream output = new FileOutputStream(file);

        try (DataOutputStream outstream = new DataOutputStream(new FileOutputStream(file, false))) {

            StringBuilder body = new StringBuilder();

            for (Integer key : topicList.keySet()) {
                body.append(key);
                body.append('=');
                body.append(topicList.get(key));
                body.append('\n');
            }
            outstream.write(body.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> LoaderFile(String filePath) {

        String location = "classpath:public/" + filePath;
        Resource resource = new DefaultResourceLoader().getResource(location);

        List<String> list = new ArrayList<>();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = buff.readLine()) != null) {
                list.add(line);
            }
            LOG.debug("Файл " + filePath + " загружен");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<String> LoadListFromFile(String filePath) {
        List<String> list = new ArrayList<>();

        for (String item : LoaderFile(filePath)) {
            list.add(item.split("=")[0]);
        }
        return list;
    }

    public Map<String, String> LoadMapFromFile(String filePath) {
        Map<String, String> map = new HashMap();

        String[] twain;
        for (String item : LoaderFile(filePath)) {
            twain = item.split("=");
            map.put(twain[0], twain[1]);
        }
        return map;
    }


    @PostConstruct
    public void test() {
        LOG.debug("Создан бин InitBot");
    }
}