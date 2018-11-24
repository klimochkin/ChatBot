package vkbot.entity;

import lombok.Data;

@Data
public class DataBot {
    private String prefixes ;
    private String[] vkToken;
    private String wordsBlacklist;
    private String[] blacklist;
    private boolean debug = false;
    private String[] lastMessage;
    private String version;
    private String yandexToken ;
}
