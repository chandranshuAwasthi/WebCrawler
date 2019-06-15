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
public class ApplicationTests {
	
	private DataReaderService dataReaderService;
	
	
	private Crawler crawl;
	//@Before
	

	@Before
	public void setUp() {
	
		dataReaderService=new  JSONfFileReaderImpl("JsonFiles");
		crawl=new JSOnCrawlerImpl(dataReaderService);
		 //crawl.crawler("internet_2.json");

	}

	
	
	@Test(expected=FileNotFoundException.class)
	public void test_fileNotFoundException() {
		System.out.println("@Test");
		crawl.crawler("internet_3.json");
		
	}
	@Test(expected=ContentNotParsableException.class)
	public void test_contentNotParsableException() {
		System.out.println("@Test");
		 crawl.crawler("internet_4.json");

	}
	
	
	@After
	public void distroy() {
		dataReaderService=null;
		crawl=null;
		 //crawl.crawler("internet_2.json");

	}

	

}
