package com.aioff.spider.parse.impl;

import java.util.*;

import com.aioff.spider.downloader.Downloader;
import com.aioff.spider.downloader.HttpClientDownloader;
import com.aioff.spider.entity.Page;
import com.aioff.spider.entity.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aioff.spider.parse.nlp.AgencyNLP;
import com.aioff.spider.parse.nlp.CommonNLP;
import com.aioff.spider.newspider.bid.utils.JudgeUtils;
import com.aioff.spider.parse.ExtractInterface;

public class AgencyExtract implements ExtractInterface{

	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		String name = null;
		List<String> keywords = (List<String>) params.get("keyword");
		String tag = (String) params.get("tag");
		if(JudgeUtils.isEmpty(keywords)){
			return name; 
		}
		if(StringUtils.isBlank(tag)){
			tag = "p";
		}
		for(String keyword:keywords){
			name = getName(html, keyword, tag);
			if(StringUtils.isNotBlank(name)){
				return name;
			}
		}
		return name;
	}
	
	@SuppressWarnings("rawtypes")
	private String getName(String html, String keyword, String tag){
		String name = null;
		Document doc= Jsoup.parse(html);
		Elements elements = doc.select(tag+":contains("+keyword+")");
		Iterator it = elements.iterator();
		int count = 99;
		while (it.hasNext()) {
			Element element = (Element) it.next();
			String sentence = element.text();
			sentence = AgencyNLP.getInstance().extract(sentence);
			int size = CommonNLP.getInstance().countWords(sentence);
			if(size<count){
				name = sentence;
				count = size;
			}
		}
		return name;
	}

	public static void main(String[] args) {
		Downloader downloader = HttpClientDownloader.DEFAULT();
		Page page = downloader.download(new Request("http://www.ahzfcg.gov.cn//portals/bulletinView?bulletinId=bbee801e-1ae2-4219-8999-dc012dda72da"));
		//1、构造参数
		Map<String, Object> params = new HashMap<String, Object>();
		//1.1
		List<String> keywords = new ArrayList<String>();
//		keywords.add("采购单位");
		keywords.add("项目单位");
//		keywords.add("代理机构");
		params.put("keyword", keywords);
//		//1.2
//		List<String> rules = new ArrayList<String>();
//		rules.add("/m/qt/m/n/m/b/m/w/m");
//		params.put("rule", rules);
		//2、解析
		ExtractInterface extract = new AgencyExtract();
		System.out.println(extract.getResult(page.getRawText(), params));
	}

}
