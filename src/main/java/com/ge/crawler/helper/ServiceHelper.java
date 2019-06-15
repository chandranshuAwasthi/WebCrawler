package com.ge.crawler.helper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.ResourceUtils;

import com.ge.crawler.exception.ContentNotParsableException;
import com.ge.crawler.exception.FileNotFoundException;

public class ServiceHelper {

	
	
	
	
//	 * This method returns the JsonObject of the Web Crawler File
	 


	public static JSONObject readJsonFile(String fileNamewithPath) {
		JSONParser parser = new JSONParser();
		JSONObject crawlerJson = null;
		Object obj;
		try {
	        //ClassLoader classLoader = ServiceHelper.class.getClassLoader();
	        File file = ResourceUtils.getFile("classpath:"+fileNamewithPath);
	       // File file = new File(classLoader.getResource(fileNamewithPath).getFile());
	        if(!file.exists()){
	        	throw new FileNotFoundException("File with the given name does not exsist in the folder");
	        }			
			obj = parser.parse(new FileReader(file));
			crawlerJson = (JSONObject) obj;
			}	
		catch(java.io.FileNotFoundException ex){
		 	throw new FileNotFoundException("File with the given name does not exsist in the folder");
	        
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (ParseException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			throw new ContentNotParsableException("Content Provided in file can not be parsed",e);
		}catch (Exception e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
		}		
		return crawlerJson;
	}
	
	
	
	
}
