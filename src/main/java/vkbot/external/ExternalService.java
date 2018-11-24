package vkbot.external;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vkbot.access.InitBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

@Service("ExternalService")
public class ExternalService {

    public String getBash() throws IOException {
        Random random = new Random();
        String answer;
        String url = "http://bash.im/byrating/" + random.nextInt(50);
        Document bashDoc = Jsoup.connect(url).get();
        Elements elements = bashDoc.select(".text");

        answer = elements
                .get(random.nextInt(elements.size()))
                .html()
                .replace("<br>", "");

        return answer;
    }

    public String getFixer() throws IOException, ParseException {
        String answer = null;
        String responceEUR = this.readUrl("http://data.fixer.io/api/latest?access_key=f3e85b51f76cbc18e373523ec8149b07&base=EUR");
        JSONObject kursEurUrl = (JSONObject) new JSONParser().parse(responceEUR);
        JSONObject ratesEur = (JSONObject) kursEurUrl.get("rates");
        if (ratesEur != null) {
            String EUR = ratesEur.get("RUB").toString();
            answer = "Евро = " + EUR + "руб." +
                    "\n40 евро = " + Float.valueOf(EUR) * 40;
        }
        return answer;
    }


    public String getWeatherYahoo(String city) throws IOException, ParseException {

        StringBuilder result = new StringBuilder();

        String SQL = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22"
                + this.fixString(city)
                + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        try {
            JSONObject weatherJSON = (JSONObject) new JSONParser().parse(this.readUrl(SQL));
            JSONObject query = (JSONObject) weatherJSON.get("query");
            JSONObject results = (JSONObject) query.get("results");
            JSONObject channel = (JSONObject) results.get("channel");

            //------
            JSONObject location = (JSONObject) channel.get("location");
            String cityResp = location.get("city").toString();
            result.append(cityResp + "\n");
            //------
            JSONObject itemResp = (JSONObject) channel.get("item");
            JSONArray forecast = (JSONArray) itemResp.get("forecast");

            for (int i = 0; i < forecast.size(); i++) {
                JSONObject item = (JSONObject) forecast.get(i);
                // String day
                try {
                    Integer max = (Integer.parseInt(item.get("high").toString()) - 32) * 5 / 9;
                    Integer min = (Integer.parseInt(item.get("low").toString()) - 32) * 5 / 9;

                    result.append(item.get("date").toString().replace("2018", ": "));
                    result.append(min.toString() + " - " + max.toString() + " °C, ");
                    String cod = item.get("code").toString();
                    //String cod2 = InitBot.weatherCods.get(cod);
                    result.append(InitBot.weatherCods.get(item.get("code").toString()) + "\n");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось найти такой населенный пункт!";
        }
        String res = result.toString();
        return result.toString();
    }


    public static  String readUrl(String url) throws IOException {
        URL uri = new URL(url);
        URLConnection conURl = (URLConnection) uri.openConnection();
        conURl.setConnectTimeout(5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(conURl.getInputStream(), "UTF-8"));
        String inputLine = in.readLine();
        in.close();
        return inputLine;
    }


    public static String fixString(String component) {
        String result = null;
        try {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = component;
        }
        return result;
    }
}