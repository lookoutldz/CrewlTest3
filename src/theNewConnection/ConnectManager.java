package theNewConnection;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * looko
 * 2018-04-10
 * 新的获取steam游戏大图的类
 * 测试网络下：
 * 平均网络占用降低50%以上
 * 平均获取速率快约20%以上
 */
public class ConnectManager {

    /**
     * 逻辑上请求仍有4类
     * 1.可直接获取图片的游戏，如412830
     * 2.有第一类302重定向的游戏，如637650
     * 3.有第二类302重定向的游戏，如292030
     * 4.其它类（已经失效或者错误的游戏id，如000000，或者还未发现处理方法的）
     * @param appid 游戏应用的id
     */
    public static void sentGet(int appid){

        String url_str = "http://store.steampowered.com/app/" + appid;
        String url_current;
        try
        {
            String regex = "(?=https://steamcdn).*";
            Pattern pattern = Pattern.compile(regex);

            //获取连接的response
            Connection con = Jsoup.connect(url_str).header("Accept-Language","zh-CN,zh;q=0.9").method(Connection.Method.GET);
            Connection.Response response = con.execute();
            url_current = response.url().toString();

            //判断类型
            if (url_current != null && url_current.equals(url_str)){
                //the way1，直接获取
                Document doc = response.parse();
                findLinks(doc);
            }
            else if (url_current != null && url_current.equals("http://store.steampowered.com/agecheck/app/"+appid+"/")){
                //the way2，获取所需的参数，加到新请求的cookie或参数中post发送
                //String steamCountry = response.cookie("steamCountry");
                String browserid = response.cookie("browserid");
                String sessionid = response.cookie("sessionid");
                String birthtime = "725817601";
                String lastagecheckage = "1-January-1993";

                con = Jsoup.connect(url_current).header("Accept-Language","zh-CN,zh;q=0.9");
                con.cookie("browserid",browserid).cookie("sessionid",sessionid).cookie("birthtime",birthtime).cookie("lastagecheckage",lastagecheckage);
                con.data("snr","1_agecheck_agecheck__age-gate").data("sessionid",sessionid).data("ageDay","1").data("ageMonth","1").data("ageYear","1993");
                Document doc = con.post();
                findLinks(doc);
            }
            else if (url_current != null && url_current.equals(url_str+"/agecheck")){
                //the way3，获取所需参数，加到新请求的cookie中get发送
                //String steamCountry = response.cookie("steamCountry");
                String browserid = response.cookie("browserid");
                String sessionid = response.cookie("sessionid");
                String mature_content = "1";

                con = Jsoup.connect(url_current).header("Accept-Language","zh-CN,zh;q=0.9");
                con.cookie("browserid",browserid).cookie("sessionid",sessionid).cookie("mature_content",mature_content);
                Document doc = con.get();
                findLinks(doc);
            }
            else {
                //solve the problem
                System.out.printf("what? the way4?\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从文档中找出所需链接的方法
     * @param doc Jsoup文档类型的变量
     */
    public static void findLinks(Document doc){

        String regex = "(?=https://steamcdn).*";
        Pattern pattern = Pattern.compile(regex);

        Elements elements = doc.getElementsByClass("highlight_screenshot_link");
        if (elements.size() > 0){
            int count = 0;
            for (Element e : elements) {
                Matcher matcher = pattern.matcher(e.attr("href"));
                if (matcher.find()) {
                    System.out.printf(matcher.group(0)+"\n");
                    count++;
                }
                if (count > 14)
                    break;
            }
        }
    }

}
