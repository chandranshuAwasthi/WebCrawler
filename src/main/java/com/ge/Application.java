package com.ge;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.ge.crawler.crawlExecutor.Crawler;
import com.ge.crawler.exception.ContentNotParsableException;
import com.ge.crawler.exception.FileNotFoundException;
import com.ge.crawler.exception.PageNotFoundException;

@SpringBootApplication(scanBasePackages = "com.ge")
public class Application implements CommandLineRunner {

	@Autowired
	private Crawler crawl;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
		System.out.println("Finished ");

	}

	@Override
	public void run(String... args) {
		System.out.println("args.length :" + args.length);
		if (args.length == 1) {
			String extension = "";
			int i = args[0].toString().lastIndexOf('.');
			if (i > 0) {
				extension = args[0].toString().substring(i + 1);
			}
			if (0 == extension.compareTo("json")) {
				try {

					Map<String, Set<String>> mapOFcatogrizedvalues = crawl.crawler(args[0]);
					mapOFcatogrizedvalues.entrySet().stream()
							.forEach(x -> System.out.println(x.getKey() + " Value : " + x.getValue()));

				} catch (PageNotFoundException iae) {
					System.out.println("Error message :" + iae.getMessage());

				} catch (FileNotFoundException fnfe) {
					System.out.println("Error message :" + fnfe.getMessage());

				}catch (ContentNotParsableException fnfe) {
					fnfe.printStackTrace();
					System.out.println("Error message :" + fnfe.getMessage());

				} catch (Exception iae) {
					iae.printStackTrace();

				}

			} else {

				System.err.println("Implementation for this format is not available");
				return;
			}

		} else if (args.length > 1) {

			System.err.println("Too many arguments.Inorder to process please provide exactly one argument");
			return;
		} else {
			System.err.println("Please provide the fileName with proper format");
			return;
		}

		// TODO Auto-generated method stub

	}

}
