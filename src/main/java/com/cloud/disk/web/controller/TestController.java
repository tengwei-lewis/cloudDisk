package com.cloud.disk.web.controller;

import com.cloud.disk.domain.entity.TestBean;
import com.cloud.disk.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest/test")
public class TestController 
{
	@Autowired
	private TestService testService;
	
	@RequestMapping(value = "find-test-all", method = RequestMethod.POST)
	public List<TestBean> findTestAll()
	{
		return testService.findTestAll();
	}
	
	@RequestMapping(value = "add-test", method = RequestMethod.POST)
	public Map<String,String> addTest()
	{
		final TestBean t = new TestBean();
		t.setTest(new Date().toString());
		testService.addTest(t);
		return new HashMap<>();
	}
	@RequestMapping(value = "delete-test-by-test-id", method = RequestMethod.POST)
	public Map<String,String> deleteTestByTestId(@RequestParam(value = "testId", required = true) final Long testId )
	{
		testService.deleteTestByTestId(testId);
		return new HashMap<>();
	}
}
