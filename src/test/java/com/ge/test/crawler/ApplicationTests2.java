package com.ge.test.crawler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.ge.crawler.crawlExecutor.Crawler;
import com.ge.crawler.crawlExecutorImpl.JSOnCrawlerImpl;
import com.ge.crawler.exception.ContentNotParsableException;
import com.ge.crawler.exception.FileNotFoundException;
import com.ge.crawler.exception.PageNotFoundException;
import com.ge.crawler.service.DataReaderService;
import com.ge.crawler.serviceImpl.JSONfFileReaderImpl;

@RunWith(SpringRunner.class)
public class ApplicationTests2 {
	
	private DataReaderService dataReaderService;
	
	
	private Crawler crawl;
	//@Before
	

	@Before
	public void setUp() {
	
		dataReaderService=new  JSONfFileReaderImpl("JsonFiles");
		crawl=new JSOnCrawlerImpl(dataReaderService);
		 //crawl.crawler("internet_2.json");

	}

	/*
	 * Please pass page-01 as argument in order to pass this test case
	 */
	@Test
	public void contextLoads() {
        String[] success1 = {"page-01", "page-02", "page-03", "page-04", "page-05", "page-06", "page-09", "page-07",
        		"page-08", "page-99"};
        Set<String> successSet = new LinkedHashSet<>(Arrays.asList(success1));
        System.out.println("Please enter page-01 for this Junit test to succeed");
    	Map<String,Set<String>> mapOfValues= crawl.crawler("internet_2.json");
        assertThat(mapOfValues.get("Success") , is(successSet));
	}

		
	
	@After
	public void distroy() {
		dataReaderService=null;
		crawl=null;
		 //crawl.crawler("internet_2.json");

	}

	

}
