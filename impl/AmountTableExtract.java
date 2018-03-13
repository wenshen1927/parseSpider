package com.aioff.spider.parse.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.aioff.spider.parse.ExtractInterface;

public class AmountTableExtract implements ExtractInterface{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<Float> amounts = null;
		List<String> keywords = (List<String>) params.get("keyword");
		if(keywords==null || keywords.isEmpty()){
			return amounts; 
		}
		for(String keyword:keywords){
			amounts = getAmounts(html, (String)keyword);
			if(!amounts.isEmpty()){
				return amounts;
			}
		}
		return amounts;
	}
	
	/**
	 * 
	 * @param html
	 * @param keyword
	 * @return
	 */
	private List<Float> getAmounts(String html, String keyword){
		List<Float> amounts = new ArrayList<Float>();
		Document doc= Jsoup.parse(html);
		List<Element> elements = this.getChildElement(doc.select("table:contains("+keyword+")"));
		if(!elements.isEmpty()){
			for(Element element:elements){
				amounts.addAll(getAmountsByElement(element, keyword));
			}
		}
		return amounts;
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
	
	private List<Float> getAmountsByElement(Element element, String keyword){
		List<Float> amounts = new ArrayList<Float>();
		Elements trElements = element.select("tr");
		Element trElement = element.select("tr:contains("+keyword+")").first();
		Element tdElement = element.select("td:contains("+keyword+")").first();
		if(tdElement == null){
			return amounts;
		}
		int rowSize = trElements.size();
		int colSize = trElement.select("td").size();
		int rowIndex = trElements.indexOf(trElement);
		int colIndex = trElement.select("td").indexOf(tdElement);
		String text = tdElement.text();
		boolean format = (text.indexOf("元") != -1 && text.indexOf("万元") == -1);
		float amount = 0.00f;
		for(int index=rowIndex+1;index<rowSize;index++){
			Element nextElement = trElements.get(index);
			Elements nextElements = nextElement.select("td");
			int size = nextElements.size();
			if(colSize != size){
				return amounts;
			}
			String name = nextElements.get(colIndex).text();
			if(StringUtils.isEmpty(name) || name.indexOf("：") != -1){
				return amounts;
			}
			amount = formatAmount(format, name);
			if(amount > 0){
				amounts.add(amount);
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

}
