package com.aioff.spider.parse.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aioff.spider.parse.ExtractInterface;

public class TableExtract implements ExtractInterface{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<String> names = null;
		List<String> keywords = (List<String>) params.get("keyword");
		if(keywords==null || keywords.isEmpty()){
			return names; 
		}
		for(String keyword:keywords){
			names = getNames(html, keyword);
			if(!names.isEmpty()){
				break;
			}
		}
		return names;
	}
	
	private List<String> getNames(String html, String keyword){
		List<String> names = new ArrayList<String>();
		Document doc= Jsoup.parse(html);
		List<Element> elements = getChildElement(doc.select("table:contains("+keyword+")"));
		if(!elements.isEmpty()){
			for(Element element:elements){
				names.addAll(getNamesByElement(element, keyword));
			}
		}
		return names;
	}
	
	/**
	 * 获取最后一层table
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private List<Element> getChildElement(Elements elements) {
		List<Element> childs = new ArrayList<Element>();
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Element element = (Element) it.next();
			if (element.child(0).select("table").first() == null) {
				childs.add(element);
			}
		}
		return childs;
	}
	
	private static List<String> getNamesByElement(Element element, String keyword){
		List<String> names = new ArrayList<String>();
		Elements trElements = element.select("tr");
		Element trElement = element.select("tr:contains("+keyword+")").first();
		Element tdElement = element.select("td:contains("+keyword+")").first();
		if(tdElement == null){
			return names;
		}
		int rowSize = trElements.size();
		int colSize = trElement.select("td").size();
		int rowIndex = trElements.indexOf(trElement);
		int colIndex = trElement.select("td").indexOf(tdElement);
		for(int index=rowIndex+1;index<rowSize;index++){
			Element nextElement = trElements.get(index);
			Elements nextElements = nextElement.select("td");
			int size = nextElements.size();
			if(colSize != size){
				return names;
			}
			String name = nextElements.get(colIndex).text();
			if(StringUtils.isEmpty(name) || name.indexOf("：") != -1){
				return names;
			}
			names.add(name);
		}
		return names;
	}

}
