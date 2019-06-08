package com.ge.crawler.main;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.crawler.dto.Page;
import com.ge.crawler.dto.Pages;

/*
 * 
 */
public class Crawler {
	static Map<String, List<String>> pageLinksmap = new LinkedHashMap<String, List<String>>();
	// pagelist is used to add all the address found in the links except the
	// duplicate one's
	static List<String> pageList = new ArrayList<String>();
	static Set<String> success = new LinkedHashSet<String>();
	static Set<String> skipped = new LinkedHashSet<String>();
	static Set<String> error = new LinkedHashSet<String>();
	static Set<String> successSync;
	static Set<String> skippedSync;
	static Set<String> errorSync;
	static int pageListLength = 0;

	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		successSync = Collections.synchronizedSet(success);
		skippedSync = Collections.synchronizedSet(skipped);
		errorSync = Collections.synchronizedSet(error);
		// Using Executor Framework to visit the links in address parallelly
		ExecutorService executor = Executors.newFixedThreadPool(5);
		// Using try with resources to close in object automatically
		try (Scanner in = new Scanner(System.in)) {
			JSONObject crawlerJson = readJsonFile();
			Pages pages = mapper.readValue(crawlerJson.toString(), Pages.class);
			for (Page page : pages.getPages()) {
				// adding all the pages to map
				pageLinksmap.put(page.getAddress(), page.getLinks());
			}
			
			System.out.print("Please enter the address from the list :"
					+ " page-01,\npage-02,\npage-03,\npage-04,\npage-05,\npage-06,\npage-09,\npage-07,\npage-08,\npage-99,\npage-97,\npage-98\n ");

			String address = in.nextLine();
			if (pageLinksmap.containsKey(address)) {
				// adding the first address if found in address list
				pageList.add(address);
				success.add(address);
			} else {
				System.out.println("Please enter the correct address");
				return;
			}
			// length of the address list to be iterated, which changes
			// dynamically as we
			// visit the the links to crawl the pages
			pageListLength = pageList.size();
			int i = 0;
			while (i < pageListLength) {
				int noOfThread = pageListLength - i;
				// creating the CountDownLatch to keep track of the threads
				// being processed
				CountDownLatch latch = new CountDownLatch(noOfThread);
				for (int j = 0; j < noOfThread; j++) {
					PageThread worker = new PageThread(i, latch);
					executor.execute(worker);
					i++;
				}
				// main thread will wait until the execution of Executors thread
				// is not finished
				latch.await();
			}
			executor.shutdown();
			System.out.println("Success :" + successSync);
			System.out.println("Skipped :" + skippedSync);
			System.out.println("Error :" + errorSync);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * This method returns the JsonObject of the Web Crawler File
	 */

	private static JSONObject readJsonFile() {
		JSONParser parser = new JSONParser();
		JSONObject crawlerJson = null;
		try {
			Object obj = parser.parse(new FileReader("./resources/internet_1.json"));
			crawlerJson = (JSONObject) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return crawlerJson;
	}

	/*
	 * PageThread:This is static Inner Class whose object is used by threads to
	 * find skipped, success and error page in parallel
	 */
	public static class PageThread implements Runnable {
		int i;
		CountDownLatch latch;

		PageThread(int i, CountDownLatch latch) {
			this.i = i;
			this.latch = latch;
		}

		@Override
		public void run() {

			// System.out.println("Thread.currentThread().getName()
			// :"+Thread.currentThread().getName());
			if (pageLinksmap.containsKey(pageList.get(i))) {
				// get all the address of the link provided
				List<String> addresses = pageLinksmap.get(pageList.get(i));
				for (String address : addresses) {
					if (pageList.contains(address)) {
							// add all the duplicate address in pagelist to
							// skipped list
							skippedSync.add(address);
						} else {
						if (pageLinksmap.containsKey(address)) {
							// add all the address that are not present in
							// pagelist
							successSync.add(address);
						}
						pageList.add(address);
						pageListLength += 1;
					}

					if (!pageLinksmap.containsKey(address)) {
						// add all the address that are not present in json
						// address field
						errorSync.add(address);
					}
				}
			} else {
				// add all the address that are not present in json address
				// field
				errorSync.add(pageList.get(i));
			}
			latch.countDown();
		}

	}
}
