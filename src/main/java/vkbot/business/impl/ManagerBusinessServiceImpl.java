package vkbot.business.impl;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.LongPollBusinessService;
import vkbot.business.ManagerBusinessService;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Service("ManagerBusinessServiceImpl")
public class ManagerBusinessServiceImpl implements ManagerBusinessService {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerBusinessServiceImpl.class);

    @Autowired
    private LongPollBusinessService longPollBusinessService;// = new LongPollBusinessServiceImpl();

    @Override
    public void startBot() throws ClientException, ApiException, ParseException, JSONException, IOException {
        longPollBusinessService.cycle();

    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин ManagerBusinessServiceImpl");
    }
}
