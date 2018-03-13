package com.aioff.spider.parse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aioff.spider.newspider.bid.utils.JudgeUtils;
import com.aioff.spider.parse.ExtractInterface;
import com.aioff.spider.util.JsoupHelper;

public class RegularExtract implements ExtractInterface{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(String html, Map<String, Object> params) {
		List<String> names = null;
		List<String> keywords = (List<String>) params.get("keyword");
		if(JudgeUtils.isEmpty(keywords)){
			return names; 
		}
		String text = JsoupHelper.getText(html);
		for(String keyword:keywords){
			names = getNames(text, keyword);
			if(!names.isEmpty()){
				return names;
			}
		}
		return names;
	}
	
	private List<String> getNames(String text, String keyword){
		text = text.replaceAll(":|：" , " ");
		Pattern pattern = Pattern.compile("("+split(keyword)+"[^\\s]*\\s*)([^\\s]*)(\\s*)");
        Matcher matcher = pattern.matcher(text);
        List<String> names = new ArrayList<String>();
        while(matcher.find()){
        	names.add(matcher.group(2));
        }
		return names;
	}
	
	/**
	 * 
	 * @param keyword
	 * @return
	 */
	private String split(String keyword){
		int length = keyword.length();
		StringBuffer sb = new StringBuffer();
		for(int index=0;index<length-1;index++){
			sb.append(keyword.charAt(index));
			if(!Pattern.matches("\\p{P}", keyword.charAt(index)+"")){
				sb.append("\\s*");
			}
		}
		sb.append(keyword.charAt(length-1));
		return sb.toString();
	}

}
