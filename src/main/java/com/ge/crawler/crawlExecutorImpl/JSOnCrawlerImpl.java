package com.ge.crawler.crawlExecutorImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ge.crawler.crawlExecutor.Crawler;
import com.ge.crawler.dto.Pages;
import com.ge.crawler.exception.PageNotFoundException;
import com.ge.crawler.service.DataReaderService;

@Component
@Scope("prototype")
public class JSOnCrawlerImpl implements Crawler,BeanPostProcessor, BeanFactoryAware, DisposableBean { 
	
	private BeanFactory beanFactory;

    private final List<Object> prototypeBeans = new LinkedList<>();

  //	@Value("${threadCount}")
	private int threadCount=5;

	@Autowired
	private DataReaderService dataReader;

	private Map<String, List<String>> pageLinksmap = new LinkedHashMap<String, List<String>>();
	// pagelist is used to add all the address found in the links except the
	// duplicate one's
	private List<String> pageList = new ArrayList<String>();

	private Set<String> successSync;
	private Set<String> skippedSync;
	private Set<String> errorSync;
	private int pageListLength = 0;

	public JSOnCrawlerImpl(DataReaderService dataReaderService) {
		// TODO Auto-generated constructor stub
		this.dataReader=dataReaderService;
	}

	public Map<String, List<String>> getPageLinksmap() {
		return pageLinksmap;
	}

	public void setPageLinksmap(Map<String, List<String>> pageLinksmap) {
		this.pageLinksmap = pageLinksmap;
	}

	public List<String> getPageList() {
		return pageList;
	}

	public void setPageList(List<String> pageList) {
		this.pageList = pageList;
	}

	public Set<String> getSuccessSync() {
		return successSync;
	}

	public void setSuccessSync(Set<String> successSync) {
		this.successSync = successSync;
	}

	public Set<String> getSkippedSync() {
		return skippedSync;
	}

	public void setSkippedSync(Set<String> skippedSync) {
		this.skippedSync = skippedSync;
	}

	public Set<String> getErrorSync() {
		return errorSync;
	}

	public void setErrorSync(Set<String> errorSync) {
		this.errorSync = errorSync;
	}

	public int getPageListLength() {
		return pageListLength;
	}

	public void setPageListLength(int pageListLength) {
		this.pageListLength = pageListLength;
	}

	public Map<String, Set<String>> crawler(final String fileName )  {
		Map<String, Set<String>> mapOfstatus = new HashMap<String, Set<String>>();
		successSync = Collections.synchronizedSet(new LinkedHashSet<String>());
		skippedSync = Collections.synchronizedSet(new LinkedHashSet<String>());
		errorSync = Collections.synchronizedSet(new LinkedHashSet<String>());
		// Using Executor Framework to visit the links in address parallelly
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		// Using try with resources to close in object automatically
		try (Scanner in = new Scanner(System.in)) {

			Pages pages = dataReader.getDataFromSource(fileName);
			pageLinksmap = pages.getPages().stream().collect(Collectors.toMap(x -> x.getAddress(), x -> x.getLinks()));
			StringBuilder stringBuilder = new StringBuilder();
			pageLinksmap.keySet().stream().forEach(x -> {
				stringBuilder.append(x + ",\n");
			});
			System.out.print("Please enter the address from the list :" + stringBuilder.toString());
            
			String address = in.nextLine();
			if (pageLinksmap.containsKey(address)) {
				// adding the first address if found in address list
				pageList.add(address);
				successSync.add(address);
			} else {
				throw new PageNotFoundException("Requested page is not available");
			}
			// length of the address list to be iterated, which changes
			// dynamically as we
			// visit the the links to crawl the pages
			pageListLength = pageList.size();
			int i = 0;
			while (i < pageListLength) {
				int numberOfTasks = pageListLength - i;
				// creating the CountDownLatch to keep track of the threads
				// being processed
				CountDownLatch latch = new CountDownLatch(numberOfTasks);
				for (int j = 0; j < numberOfTasks; j++) {
					PageThreadTask worker = new PageThreadTask(i, latch, this);
					executor.execute(worker);
					i++;
				}
				// main thread will wait until the execution of Executors thread
				// is not finished
				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			executor.shutdown();
			mapOfstatus.put("Success", successSync);
			mapOfstatus.put("Skipped", skippedSync);
			mapOfstatus.put("Error", errorSync);
			return mapOfstatus;
		}

	}
       // Distroying All the Prototypes After one call completed
	  @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	        return bean;
	    }

	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	        if (beanFactory.isPrototype(beanName)) {
	            synchronized (prototypeBeans) {
	                prototypeBeans.add(bean);
	            }
	        }
	        return bean;
	    }

	    @Override
	    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
	        this.beanFactory = beanFactory;
	    }

	    @Override
	    public void destroy() throws Exception {
	        synchronized (prototypeBeans) {
	            for (Object bean : prototypeBeans) {
	                if (bean instanceof DisposableBean) {
	                    DisposableBean disposable = (DisposableBean)bean;
	                    System.out.println("disposable "+disposable);
	                    try {
	                        disposable.destroy();
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	            prototypeBeans.clear();
	        }
	    }
		


	
	
	/*
	 * PageThread:This is static Inner Class whose object is used by threads to
	 * find skipped, success and error page in parallel
	 */
	public static class PageThreadTask implements Runnable {
		int i;
		CountDownLatch latch;
		JSOnCrawlerImpl crawl;

		PageThreadTask(int i, CountDownLatch latch, JSOnCrawlerImpl crawl) {
			this.i = i;
			this.latch = latch;
			this.crawl = crawl;
		}

		@Override
		public void run() {

			// System.out.println("Thread.currentThread().getName()
			// :"+Thread.currentThread().getName());
			if (crawl.pageLinksmap.containsKey(crawl.pageList.get(i))) {
				// get all the address of the link provided
				List<String> addresses = crawl.pageLinksmap.get(crawl.pageList.get(i));
				for (String address : addresses) {
					if (crawl.pageList.contains(address)) {
						
							// add all the duplicate address in pagelist to
							// skipped list
							crawl.skippedSync.add(address);
						
					} else {
						if (crawl.pageLinksmap.containsKey(address)) {
							// add all the address that are not present in
							// pagelist
							crawl.successSync.add(address);
						}
						crawl.pageList.add(address);
						crawl.pageListLength += 1;
					}

					if (!crawl.pageLinksmap.containsKey(address)) {
						// add all the address that are not present in json
						// address field
						crawl.errorSync.add(address);
					}
				}
			} else {
				// add all the address that are not present in json address
				// field
				crawl.errorSync.add(crawl.pageList.get(i));
			}
			latch.countDown();
		}

	}
}
