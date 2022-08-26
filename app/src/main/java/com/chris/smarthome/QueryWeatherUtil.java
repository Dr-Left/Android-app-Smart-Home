package com.chris.smarthome;

import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueryWeatherUtil {

    public static final int UPDATE_TODAY_WEATHER = 1;

    private static TodayWeather parseXML(String xmldata) {
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        TodayWeather todayWeather = new TodayWeather();
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "city:    " + xmlPullParser.getText());
                            todayWeather.setCity(xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                            Log.d("myWeather", "updatetime:    " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                            Log.d("myWeather", "shidu:    " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                            Log.d("myWeather", "wendu:    " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                            Log.d("myWeather", "pm25:    " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                            Log.d("myWeather", "quality:    " + xmlPullParser.getText());
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            Log.d("myWeather", "fengxiang:    " + xmlPullParser.getText());
                            fengxiangCount++;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            Log.d("myWeather", "fengli:    " + xmlPullParser.getText());
                            fengliCount++;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            Log.d("myWeather", "date:    " + xmlPullParser.getText());
                            dateCount++;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText());
                            Log.d("myWeather", "high:    " + xmlPullParser.getText());
                            highCount++;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText());
                            Log.d("myWeather", "low:    " + xmlPullParser.getText());
                            lowCount++;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            Log.d("myWeather", "type:    " + xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                // 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }


    public static void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        final String pm25url = "https://v0.yiketianqi.com/api?unescape=1&version=v62&appid=81545516&appsecret=Be29IyAp&cityid=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        //Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);


                    //需要对parseXML函数进行改造
                    todayWeather = parseXML(responseStr);

                    if (todayWeather != null) {
                        //获取pm2.5
                        StringBuilder jsonbuilder = new StringBuilder();
                        url = new URL(pm25url);
                        con = (HttpURLConnection) url.openConnection();
                        BufferedReader reader_pm25 = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        String inputLine;
                        while ((inputLine = reader_pm25.readLine()) != null) {
                            jsonbuilder.append(inputLine);
                        }
                        in.close();
                        String json = jsonbuilder.toString();

                        //String json="{\"cityid\":\"101010100\",\"date\":\"2021-09-07\",\"week\":\"星期二\",\"update_time\":\"11:16\",\"city\":\"北京\",\"cityEn\":\"beijing\",\"country\":\"中国\",\"countryEn\":\"China\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"24\",\"tem1\":\"27\",\"tem2\":\"17\",\"win\":\"南风\",\"win_speed\":\"2级\",\"win_meter\":\"6km\\/h\",\"humidity\":\"65%\",\"visibility\":\"19km\",\"pressure\":\"1010\",\"air\":\"17\",\"air_pm25\":\"7\",\"air_level\":\"优\",\"air_tips\":\"空气很好，可以外出活动，呼吸新鲜空气，拥抱大自然！\",\"alarm\":{\"alarm_type\":\"null\",\"alarm_level\":\"\",\"alarm_content\":\"\"},\"wea_day\":\"晴\",\"wea_day_img\":\"qing\",\"wea_night\":\"多云\",\"wea_night_img\":\"yun\",\"sunrise\":\"05:46\",\"sunset\":\"18:38\",\"hours\":[{\"hours\":\"现在\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"21\",\"win\":\"东北风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"16\"},{\"hours\":\"10:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"24\",\"win\":\"南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"17\"},{\"hours\":\"11:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"25\",\"win\":\"南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"20\"},{\"hours\":\"12:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"25\",\"win\":\"西南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"21\"},{\"hours\":\"13:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"26\",\"win\":\"西南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"24\"},{\"hours\":\"14:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"26\",\"win\":\"西南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"26\"},{\"hours\":\"15:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"26\",\"win\":\"西南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"27\"},{\"hours\":\"16:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"26\",\"win\":\"西南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"29\"},{\"hours\":\"17:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"26\",\"win\":\"西南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"31\"},{\"hours\":\"18:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"22\",\"win\":\"南风\",\"win_speed\":\"3级\",\"aqi\":\"优\",\"aqinum\":\"33\"},{\"hours\":\"19:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"22\",\"win\":\"南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"34\"},{\"hours\":\"20:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"24\",\"win\":\"东南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"37\"},{\"hours\":\"21:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"22\",\"win\":\"东南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"39\"},{\"hours\":\"22:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"21\",\"win\":\"东南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"40\"},{\"hours\":\"23:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"20\",\"win\":\"南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"43\"},{\"hours\":\"09\\/08\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"19\",\"win\":\"西南风\",\"win_speed\":\"2级\",\"aqi\":\"优\",\"aqinum\":\"44\"},{\"hours\":\"01:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"18\",\"win\":\"南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"46\"},{\"hours\":\"02:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"18\",\"win\":\"南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"47\"},{\"hours\":\"03:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"18\",\"win\":\"南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"50\"},{\"hours\":\"04:00\",\"wea\":\"多云\",\"wea_img\":\"yun\",\"tem\":\"18\",\"win\":\"东南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"51\"},{\"hours\":\"05:00\",\"wea\":\"多云\",\"wea_img\":\"yun\",\"tem\":\"18\",\"win\":\"东南风\",\"win_speed\":\"1级\",\"aqi\":\"优\",\"aqinum\":\"52\"},{\"hours\":\"06:00\",\"wea\":\"多云\",\"wea_img\":\"yun\",\"tem\":\"18\",\"win\":\"东北风\",\"win_speed\":\"1级\",\"aqi\":\"良\",\"aqinum\":\"54\"},{\"hours\":\"07:00\",\"wea\":\"多云\",\"wea_img\":\"yun\",\"tem\":\"19\",\"win\":\"东北风\",\"win_speed\":\"1级\",\"aqi\":\"良\",\"aqinum\":\"55\"},{\"hours\":\"08:00\",\"wea\":\"晴\",\"wea_img\":\"qing\",\"tem\":\"20\",\"win\":\"东北风\",\"win_speed\":\"1级\",\"aqi\":\"良\",\"aqinum\":\"56\"}],\"aqi\":{\"update_time\":\"09:26\",\"air\":\"17\",\"air_level\":\"优\",\"air_tips\":\"空气很好，可以外出活动，呼吸新鲜空气，拥抱大自然！\",\"pm25\":\"7\",\"pm25_desc\":\"优\",\"pm10\":\"17\",\"pm10_desc\":\"优\",\"o3\":\"-\",\"o3_desc\":\"-\",\"no2\":\"31\",\"no2_desc\":\"优\",\"so2\":\"2\",\"so2_desc\":\"优\",\"co\":\"-\",\"co_desc\":\"-\",\"kouzhao\":\"不用佩戴口罩\",\"yundong\":\"非常适宜运动\",\"waichu\":\"适宜外出\",\"kaichuang\":\"适宜开窗\",\"jinghuaqi\":\"关闭净化器\"},\"zhishu\":{\"chuanyi\":{\"level\":\"舒适\",\"tips\":\"建议穿长袖衬衫单裤等服装。\"},\"daisan\":{\"level\":\"不带伞\",\"tips\":\"天气较好，不用带雨伞。\"},\"ganmao\":{\"level\":\"易发\",\"tips\":\"大幅度降温，适当增加衣服。\"},\"chenlian\":{\"level\":\"适宜\",\"tips\":\"天气不错，空气清新。\"},\"ziwaixian\":{\"level\":\"很强\",\"tips\":\"涂擦SPF20以上，PA++护肤品，避强光。\"},\"liangshai\":{\"level\":\"适宜\",\"tips\":\"天气不错，抓紧时机让衣物晒太阳吧。\"},\"kaiche\":{\"level\":\"\",\"tips\":\"\"},\"xiche\":{\"level\":\"适宜\",\"tips\":\"天气较好，适合擦洗汽车。\"},\"lvyou\":{\"level\":\"适宜\",\"tips\":\"天气较好，可尽情地享受大自然的风光。\"},\"diaoyu\":{\"level\":\"较适宜\",\"tips\":\"风稍大会对垂钓产生一定影响。\"}}}";
                        JSONObject jsonObject = new JSONObject(json);
//                        String pm25 = jsonObject.getString("air_pm25");
//                        Log.d("pm25", jsonObject.getString("air_pm25"));
//                        todayWeather.setPm25(jsonObject.getString("air_pm25"));
//                        JSONObject alarm = jsonObject.getJSONObject("alarm");
//                        Log.d("aqi", alarm.getString("alarm_type") + " " + alarm.getString("alarm_level"));

//                        JSONArray jsonArray = jsonObject.getJSONArray("hours");
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
//                            Log.d("aqi", jsonObject1.getString("hours") + " " + jsonObject1.getString("aqinum"));
//
//                        }


                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        MainActivity.mHandler.sendMessage(msg);

                	   /*
                	   city_name_Tv.setText(todayWeather.getCity()+"天气");
                       cityTv.setText(todayWeather.getCity());
                       timeTv.setText(todayWeather.getUpdatetime()+ "发布");
                	   */
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    Log.d("myWeather", "Thread ending");
                }
            }
        }).start();
    }
}
