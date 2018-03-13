package com.aioff.spider.parse.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class CommonNLP {
	
	private static CommonNLP instance = null;
	private Segment segment;
	
	private CommonNLP(){
		this.segment = HanLP.newSegment();
	}
	
    public static CommonNLP getInstance() {
         if (instance == null) {    
        	 instance = new CommonNLP();  
         }
        return instance;
    }
    
    public int countWords(String sentence){
    	return this.segment.seg(sentence).size();
    }
    
    public List<Term> seg(String sentence){
    	return this.segment.seg(sentence);
    }
    
    /**
     * 
     * @param sentence
     * @param rule
     * @return
     */
    public List<String> extract(String sentence,String rule){
	    List<Term> terms = this.segment.seg(sentence);
	    StringBuffer symbols = new StringBuffer();
	    for(Term term:terms){
	    	symbols.append("/");
	    	symbols.append(term.nature.name());
	    }
	    String symbolStr = symbols.toString();
		int startIndex = 0;
		int count = 0;
		List<String> names = new ArrayList<String>();
		while (true) {
			int index = symbolStr.indexOf(rule,startIndex);
			if(index == -1){
				break;
			}
			startIndex = index+rule.length();
			count = appearNumber(symbolStr.substring(0, index), "/");
			names.add(getPhrase(terms.subList(count, count+appearNumber(rule, "/"))));
		}
		return names;
	}
	
    /**
     * 
     * @param terms
     * @return
     */
    private String getPhrase(List<Term> terms){
		StringBuffer phrase = new StringBuffer();
		for(Term term:terms){
			phrase.append(term.word);
		}
		return phrase.toString();
	}
	
    /**
     * 
     * @param srcText
     * @param findText
     * @return
     */
	private int appearNumber(String srcText, String findText) {
	    int count = 0;
	    Pattern p = Pattern.compile(findText);
	    Matcher m = p.matcher(srcText);
	    while (m.find()) {
	        count++;
	    }
	    return count;
	}

	public static void main(String[] args) {
		List<Term> terms =getInstance().segment.seg("六、开标时间：2018年2月28日09时30分");
		for (Term t :
				terms) {
			System.out.print(t.nature.name()+" ");//term.nature.name() 词性
		}
	}

}
