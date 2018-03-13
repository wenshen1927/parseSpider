package com.aioff.spider.parse.nlp;

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 
 * @author Think pad
 *
 */
public class HanLPTest {
	
	public static void main(String[] args) {
		/**
		 * 参考官方文档
		 * http://hanlp.linrunsoft.com/doc/_build/html/index.html
		 */
		List<Term> terms = HanLP.newSegment().seg("2018年3月2日");
		StringBuffer symbols = new StringBuffer();
		for(Term term:terms){
	    	symbols.append("/");
	    	symbols.append(term.nature.name());
	    }
		System.out.println(symbols);
	}

}
