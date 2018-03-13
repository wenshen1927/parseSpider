package com.aioff.spider.parse.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aioff.spider.downloader.Downloader;
import com.aioff.spider.downloader.HttpClientDownloader;
import com.aioff.spider.entity.Page;
import com.aioff.spider.entity.Request;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aioff.spider.parse.nlp.CommonNLP;
import com.aioff.spider.newspider.bid.utils.JudgeUtils;
import com.aioff.spider.parse.ExtractInterface;

public class AmountBlockExtract implements ExtractInterface{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<Float> amounts = null;
		List<String> keywords = (List<String>) params.get("keyword");
		List<String> rules = (List<String>) params.get("rule");
		String tag = (String) params.get("tag");
		if(JudgeUtils.isEmpty(keywords,rules)){
			return amounts; 
		}
		if(StringUtils.isBlank(tag)){
			tag = "p";
		}
		for(String keyword:keywords){
			List<String> names = this.getNames(html, keyword, tag);
			for(String rule:rules){
				amounts = this.getAmounts(names, rule);
				if(!amounts.isEmpty()){
					return amounts;
				}
			}
		}
		return amounts;
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> getNames(String html, String keyword, String tag){
		List<String> names = new ArrayList<String>();
		Document doc= Jsoup.parse(html);
		Elements elements = doc.select(tag+":matches("+keyword+")");
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
	private List<Float> getAmounts(List<String> sentences, String rule){
		List<Float> amounts = new ArrayList<Float>();
		if(sentences.isEmpty()){
			return amounts;
		}
		List<String> names = new ArrayList<String>();
		for(String sentence:sentences){
			names.addAll(CommonNLP.getInstance().extract(sentence, rule));
		}
		if(!names.isEmpty()){
			Float amount = 0.0f;
			for(String name:names){
				amount = this.formatAmount(false,name);
				if(amount>0){
					amounts.add(amount);
				}
			}
		}
		return amounts;
	}
	
	/**
	 * 格式化金额
	 * @param format
	 * @param amountStr
	 * @return 万元
	 */
	private float formatAmount(boolean format, String amountStr){
		float amount = 0.00f;
		if(amountStr.indexOf("万元") != -1){
			amount = picAmount(amountStr);
		}else if(amountStr.indexOf("元") != -1){
			amount = picAmount(amountStr)/10000;
		}else if(format){//表头没有显示万元，需要格式化数据
			amount = picAmount(amountStr)/10000;
		}else{
			amount = picAmount(amountStr);
		}
		return  amount;
	}
	
	/**
	 * 提取文本中的金额，只获取第一个匹配的值
	 * @param amountStr
	 * @return
	 */
	public float picAmount(String amountStr){
		float amount = 0.00f;
		Pattern pattern = Pattern.compile("([1-9]\\d*(\\.\\d*)?|0\\.\\d*[1-9]\\d*)");
		Matcher matcher = pattern.matcher(amountStr);
		if(matcher.find()) {
			try {
				amount = Float.parseFloat(matcher.group(1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return  amount;
	}

	public static void main(String[] args) {
		Downloader downloader = HttpClientDownloader.DEFAULT();
		Page page = downloader.download(new Request("http://ggzy.dongzhi.gov.cn/DongZhifront/InfoDetail/?InfoID=6d0f508c-cc62-4859-99c3-7efb6787b39a&CategoryNum=002002003"));
		//1、构造参数
		Map<String, Object> params = new HashMap<String, Object>();
		//1.1
		List<String> keywords = new ArrayList<String>();
		keywords.add("标段");
		params.put("keyword", keywords);
//		//1.2
//		List<String> rules = new ArrayList<String>();
//		rules.add("/m/qt/m/n/m/b/m/w/m");
//		params.put("rule", rules);
		//2、解析
		ExtractInterface extract = new AmountBlockExtract();
		System.out.println(extract.getResult(page.getRawText(), params));
	}

}
