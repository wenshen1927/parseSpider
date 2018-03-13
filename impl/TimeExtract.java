package com.aioff.spider.parse.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aioff.spider.parse.nlp.CommonNLP;
import com.aioff.spider.downloader.Downloader;
import com.aioff.spider.downloader.HttpClientDownloader;
import com.aioff.spider.entity.Page;
import com.aioff.spider.entity.Request;
import com.aioff.spider.newspider.bid.utils.JudgeUtils;
import com.aioff.spider.parse.ExtractInterface;
import com.hankcs.hanlp.seg.common.Term;

public class TimeExtract implements ExtractInterface{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<String> times = null;
		List<String> keywords = (List<String>) params.get("keyword");
		List<String> rules = (List<String>) params.get("rule");
		String tag = (String) params.get("tag");
		if(JudgeUtils.isEmpty(keywords, rules)){
			return times; 
		}
		if(StringUtils.isBlank(tag)){
			tag = "p";
		}
		for(String keyword:keywords){
			List<String> names = this.getNames(html, keyword, tag);
			if(names!=null&&!names.isEmpty()){
				for(String name:names){
					times = this.getTimes(name, rules);
					if(!times.isEmpty()){
						return times;
					}
				}
			}
		}
		return times;
	}
	
	/**
	 * 
	 * @param html
	 * @param keyword
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<String> getNames(String html, String keyword, String tag){
		List<String> names = new ArrayList<String>();
		Document doc= Jsoup.parse(html);
		Elements elements = doc.select(tag+":contains("+keyword+")");
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			String sentence = element.text();
			names.add(sentence);
		}
		return names;
	}
	
	/**
	 * 
	 * @param sentence
	 * @param rules
	 * @return
	 */
	private List<String> getTimes(String sentence, List<String> rules){
		List<String> times = new ArrayList<String>();
		if(StringUtils.isBlank(sentence)){
			return times;
		}
		for(String rule:rules){
			List<String> labels = CommonNLP.getInstance().extract(sentence, rule);
			if(!labels.isEmpty()){
				for(String label:labels){
					times.add(this.formatTime(label, rule));
				}
				return times;
			}
		}
		return times;
	}
	
	private String formatTime(String label, String rule){
		if(StringUtils.isBlank(label) 
				|| StringUtils.isBlank(rule)){
			return null;
		}
		String time = "##-##-## ##:##:##";
		List<Term> terms = CommonNLP.getInstance().seg(label);
		for(Term term:terms){
			if("m".equals(term.nature.name())){
				time = time.replaceFirst("##",term.word);
			}
		}
		time = time.replaceAll("##", "00");
		return  time;
	}
	
	/**
	 * demo
	 * @param args
	 */
	public static void main(String[] args) {
		Downloader downloader = HttpClientDownloader.DEFAULT();
		Page page = downloader.download(new Request("http://ggzy.dongzhi.gov.cn/DongZhifront/InfoDetail/?InfoID=6d0f508c-cc62-4859-99c3-7efb6787b39a&CategoryNum=002002003"));
		//1、构造参数
		Map<String, Object> params = new HashMap<String, Object>();
		//1.1
		List<String> keywords = new ArrayList<String>();
//		keywords.add("开标时间");
		keywords.add("公示开始时间");
		keywords.add("报名时间");
		params.put("keyword", keywords);
		//1.2
		List<String> rules = new ArrayList<String>();
		rules.add("/m/qt/m/n/m/b");
		params.put("rule", rules);
		//2、解析
		ExtractInterface extract = new TimeExtract();
		System.out.println(extract.getResult(page.getRawText(), params));
	}

}
