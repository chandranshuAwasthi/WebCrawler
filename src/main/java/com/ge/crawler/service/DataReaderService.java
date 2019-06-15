package com.ge.crawler.service;

import com.ge.crawler.dto.Pages;

public interface DataReaderService {

	Pages  getDataFromSource(String sourceName) ;
	
	
}
