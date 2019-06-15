package com.ge.crawler.crawlExecutor;

import java.util.Map;
import java.util.Set;

import org.json.simple.parser.ParseException;

import com.ge.crawler.exception.PageNotFoundException;

/*
 * 
 */


public interface Crawler {
	
	
	 public Map<String,Set<String>> crawler(String fileName) ;	
	
}
