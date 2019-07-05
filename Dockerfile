FROM java:8

ADD ./target/liugeng-bigdata-spider.jar /liugeng-bigdata-spider.jar

EXPOSE 8888

ENV params="-Dxxl.job.adminAddresses=http://127.0.0.1:8080/xxl-job-admin"

CMD java -jar $params ./liugeng-bigdata-spider.jar