package vkbot.entity;

import lombok.Data;

@Data
public class DataBot {
    private String[] vkToken;
    private String wordsBlacklist;
    private String version;
    private String yandexToken ;
}
