package com.liugeng.bigdata.spider.web.request;

import com.liugeng.bigdata.spider.output.OutputType;
import com.liugeng.bigdata.spider.task.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/9/2 10:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ZhihuSpiderTaskRequest {
	
	private String keyWord;
	
	private boolean includeAll;
	
	private OutputType outputType;
	
	private TaskType taskType;
	
	private String fileBasePath;
}
