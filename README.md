# check out the code -
build by giving comand = > mvn package
Import in eclipse :
  1) Import as a maven project
  2) Run as Spring Boot application By providing file Name(For example=internet_1.json)
  3) Provide Page Number(Example = page-01) to start with. 
  
Running Junit Test Case
   1) Go to test package Open file and Run as a Junit Test case.
   
   
Fuctionalities Build in the project.
1) In case we need to support other formats like CSV,XMl we can easily enhance and modify our application.
2) Used the SpringBoot to take advantage of many inbuit fuctionalities like -AutoConfiguration,@Autowired
2  Used the Interfaces for  future  posiblilities  of other Implementation's(For Example Need to write XMLCrawler or CSV Crawler).  
3)  Introduced the user defined  exception's for negative Scenarios.
4)  Trying to capture all the possibilities in Junit test cases.
5) you can put as many files you want to crawl in resource folder just while running application you need to pass the fileName as  argument.
   
    
