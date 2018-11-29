package vkbot.enums;


public enum CommandEnum implements MessageTypeEnum{

    SHAR        ("шар",     "👉 шар <вопрос> - магический шар, "),
    DVACH       ("двач",    "👉 двач - случпйный мем с двача"),
    MEM         ("мемы",    "👉 мемы - случайный мем"),
    SISKI       ("сиськи",  "👉 сиськи - случайные сиськи"),
    TYAN        ("тян",     "👉 тян - случайное фото"),
    FIND        ("найди",   "👉 найди <текст для поиска> - поиск видео"),
    SAY         ("скажи",   "👉 скажи <текст> - озвучка текста"),
    JOKE        ("пошути",  "👉 пошути - случайная шутка"),
    GIF         ("гиф",     "👉 гиф <текст для поиска> - поиск гифки"),
    WEATHER     ("погода",  "👉 погода <город> - прогноз погоды,"),
    EURO        ("евро",    "👉 евро - курс евро"),
    ONLINE      ("онлайн",  "👉 онлайн - список участников беседы \"онлайн\""),
    COMMANDS    ("команды", "👉 команды - список команд"),
    STAT_MSG    ("топ1",    "👉 статистика по числу сообщений в чате"),
    STAT_CHR    ("топ2",    "👉 статистика по числу символов в сообщениях в чате");


    private String code;
    private String description ;


    CommandEnum(String inputCode, String inputDescription) {
        this.code = inputCode;
        this.description = inputDescription;
    }

    public String getCode(){
        return this.code;
    }

    public String getDescription(){
        return this.description;
    }

    public boolean equalsCode(String liter) {
        return code.equals(liter);
    }
}
