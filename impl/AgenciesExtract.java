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
import com.aioff.spider.newspider.bid.utils.JudgeUtils;
import com.aioff.spider.parse.ExtractInterface;

public class AgenciesExtract implements ExtractInterface{

	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<String> names = null;
		List<String> keywords = (List<String>) params.get("keyword");
		String tag = (String) params.get("tag");
		if(JudgeUtils.isEmpty(keywords)){
			return names; 
		}
		if(StringUtils.isBlank(tag)){
			tag = "p";
		}
		for(String keyword:keywords){
			names = getNames(html, keyword, tag);
			if(!names.isEmpty()){
				return names;
			}
		}
		return names;
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> getNames(String html, String keyword, String tag){
		List<String> names = new ArrayList<String>();
		Document doc= Jsoup.parse(html);
		Elements elements = doc.select(tag+":contains("+keyword+")");
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			String sentence = element.text();
			names.add(AgencyNLP.getInstance().extract(sentence));
		}
		return names;
	}


	public static void main(String[] args) {
		Downloader downloader = HttpClientDownloader.DEFAULT();
		Page page = downloader.download(new Request("http://ggzy.dongzhi.gov.cn/DongZhifront/InfoDetail/?InfoID=6d0f508c-cc62-4859-99c3-7efb6787b39a&CategoryNum=002002003"));
		//1、构造参数
		Map<String, Object> params = new HashMap<String, Object>();
		//1.1
		List<String> keywords = new ArrayList<String>();
		keywords.add("中标候选人");
		params.put("keyword", keywords);
//		//1.2
//		List<String> rules = new ArrayList<String>();
//		rules.add("/m/qt/m/n/m/b/m/w/m");
//		params.put("rule", rules);
		//2、解析
		ExtractInterface extract = new AgenciesExtract();
		System.out.println(extract.getResult(page.getRawText(), params));
	}

}
