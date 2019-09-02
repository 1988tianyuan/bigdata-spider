package com.liugeng.bigdata.spider.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Preconditions;
import com.liugeng.bigdata.spider.output.OutputType;
import com.liugeng.bigdata.spider.task.TaskType;
import com.liugeng.bigdata.spider.task.ZhihuSearchTask;
import com.liugeng.bigdata.spider.web.request.ZhihuSpiderTaskRequest;
import com.liugeng.bigdata.spider.web.response.CommonResponse;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2019/9/2 10:08
 */
@RestController
@RequestMapping("/zhihu/trigger")
public class ZhihuSpiderTriggerController {
	
	@Autowired
	private ZhihuSearchTask zhihuSearchTask;

	@PostMapping("/begin-task")
	public CommonResponse<String> beginSpiderTaskEs(@ModelAttribute ZhihuSpiderTaskRequest request) {
		checkTypeValid(request);
		boolean success = zhihuSearchTask.innerExecute(request);
		return CommonResponse.of(success ? "Success" : "Fail");
	}
	
	private void checkTypeValid(ZhihuSpiderTaskRequest request) {
		Preconditions.checkNotNull(request.getTaskType(), "taskType should not be null.");
		Preconditions.checkNotNull(request.getOutputType(), "outputType should not be null.");
		if (request.getTaskType().equals(TaskType.IMAGE)) {
			Preconditions.checkArgument(request.getOutputType().equals(OutputType.LOCAL), "OutputType should be 'local' when "
				+ "taskType is 'image'");
			Preconditions.checkNotNull(request.getFileBasePath(), "FileBasePath should be set when outputType is 'local'");
		}
	}
}
