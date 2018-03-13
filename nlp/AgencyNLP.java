package com.aioff.spider.parse.nlp;

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

public class AgencyNLP {
	
	private static AgencyNLP instance = null;
	private Segment segment;
	
	private AgencyNLP(){
		this.segment = HanLP.newSegment().enableOrganizationRecognize(true);
	}
	
    public static AgencyNLP getInstance() {  
         if (instance == null) {    
        	 instance = new AgencyNLP();  
         }
        return instance;  
    }
    
    public String extract(String sentence){
    	List<Term> terms = segment.seg(sentence);
    	Term term = null;
    	String agency = null;
    	boolean isPlace = false;
    	for(int i=terms.size()-1;i>-1;i--){
    		term = terms.get(i);
    		if(isPlace){
    			if(term.nature.name().startsWith("nt")
    					|| "ns".equals(term.nature.name())){
    				agency = terms.get(i).word+agency;
    			}else{
    				return agency;
    			}
    		}else{
    			if(term.nature.name().startsWith("nt")){
        			agency = terms.get(i).word;
        			isPlace = true;
        		}
    		}
    	}
    	return agency;
    }
    
    public static void main(String[] args) {
    	String name1 = "深圳天源迪科信息技术股份有限公司";
    	String name2 = "安徽农业大学";
		System.out.println(AgencyNLP.getInstance().extract(name1));
	}

}
