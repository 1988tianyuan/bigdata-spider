FROM java:8

ADD ./target/liugeng-bigdata-spider.jar /liugeng-bigdata-spider.jar

EXPOSE 8081

CMD java -jar ./liugeng-bigdata-spider.jar