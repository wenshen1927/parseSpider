package com.aioff.spider.parse.special;

import com.aioff.spider.downloader.Downloader;
import com.aioff.spider.downloader.HttpClientDownloader;
import com.aioff.spider.entity.Page;
import com.aioff.spider.entity.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AgencyExt {
    public static void main(String[] args) {
        Downloader downloader = HttpClientDownloader.DEFAULT();
        Page page = downloader.download(new Request("http://ggzy.dongzhi.gov.cn/DongZhifront/InfoDetail/?InfoID=6d0f508c-cc62-4859-99c3-7efb6787b39a&CategoryNum=002002003"));
        Document doc = Jsoup.parse(page.getRawText());
        Elements elements = doc.select("p:contains(" + "(\\u6807\\u6bb5)\\u5143$" + ")");

//        String chinese = "标段元";
//        for(int i = 0;i<chinese.length();i++){
//            System.out.print("\\u" + Integer.toHexString(chinese.charAt(i)));
//            //System.out.print(chinese.getBytes("Unicode")[i]);
//
//        }
        System.out.println(elements.text());
        System.out.println();
    }
}
