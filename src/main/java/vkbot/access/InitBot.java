package vkbot.access;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import vkbot.entity.DataBot;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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


    public InitBot() {

        this.dataBot = LoaderConfig();
        actor = new UserActor(512877743, dataBot.getVkToken()[0]);

        usersPositive = LoadListFromFile("users/user_positive.txt");
        usersNegative = LoadListFromFile("users/user_negative.txt");
        prefix = LoadListFromFile("config.txt");
        usersIgnore = Arrays.asList(dataBot.getBlacklist());
        weatherCods = LoadMapFromFile("yahoo/weather_cods.txt");
        answerNoPrefix = LoaderFile("answer/no_prefix.txt");

        vk = new VkApiClient(HttpTransportClient.getInstance());
    }


    public DataBot LoaderConfig() {
        DataBot dataBot = new DataBot();
        Properties botConfig = new Properties();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.properties"), "UTF8"))) {
            botConfig.load(buff);
            dataBot.setPrefixes(botConfig.getProperty("prefixes"));
            dataBot.setBlacklist(botConfig.getProperty("blacklist").split(","));
            dataBot.setWordsBlacklist(botConfig.getProperty("wordsBlacklist"));
            dataBot.setDebug(Boolean.valueOf(botConfig.getProperty("debug")));
            dataBot.setVkToken(botConfig.getProperty("vkToken").split(";"));
            dataBot.setVersion(botConfig.getProperty("version"));
            dataBot.setYandexToken(botConfig.getProperty("yandexWeatherToken"));
        } catch (IOException e) {
            LOG.error("Не найден файл конфигурации.");
        }
        return dataBot;
    }


    public List<String> LoadListFromFile(String filePath) {
        List<String> list = new ArrayList<>();
        ;

        for (String item : LoaderFile(filePath)) {
            list.add(item.split("=")[0]);
        }
        return list;
    }

    public List<String> LoaderFile(String filePath) {

        String location = "classpath:public/" + filePath;
        Resource resource = new DefaultResourceLoader().getResource(location);

        List<String> list = new ArrayList<>();
        try (BufferedReader buff = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = buff.readLine()) != null) {
                //  list.add(user.split("=")[0]);
                list.add(line);
            }
            LOG.debug("Файл " + filePath + " загружен");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, String> LoadMapFromFile(String filePath) {
        Map<String, String> map = new HashMap();

        String[] twain;
        for (String item : LoaderFile(filePath)) {
            //list.add(item.split("=")[0]);
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