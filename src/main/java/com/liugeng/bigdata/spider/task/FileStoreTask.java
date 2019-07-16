package com.liugeng.bigdata.spider.task;

import com.liugeng.bigdata.spider.output.FileOutput;

import java.util.Map;

public abstract class FileStoreTask extends SpiderTask {

    public abstract FileOutput chooseOutput(Map<String, String> paramMap);
}
