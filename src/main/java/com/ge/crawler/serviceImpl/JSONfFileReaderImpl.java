package com.ge.crawler.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.crawler.dto.Pages;
import com.ge.crawler.exception.FileNotFoundException;
import com.ge.crawler.helper.ServiceHelper;
import com.ge.crawler.service.DataReaderService;

@Service
@Scope("prototype")
public class JSONfFileReaderImpl implements DataReaderService {

	@Value("${pathOfFile}")
	private String path;

	public JSONfFileReaderImpl() {

	}

	public JSONfFileReaderImpl(String path) {
		this.path=path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {

		this.path = path;
	}

	@Override
	public Pages getDataFromSource(final String SourceName)  {
		if (StringUtils.isEmpty(SourceName)) {
			throw new FileNotFoundException("File path can not be Null or Empty");
		}
		JSONObject crawlerJson = ServiceHelper.readJsonFile(getPath().trim() + "/" + SourceName.trim());
		Pages pages = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			pages = mapper.readValue(crawlerJson.toString(), Pages.class);// readValue(crawlerJson.toString(),
																			// //																// //
																			// Pages.class);
		} catch (IOException exe) {
			exe.printStackTrace();
		}
		return pages;
	}
}
