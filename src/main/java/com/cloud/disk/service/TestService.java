package com.cloud.disk.service;

import java.util.List;

import com.cloud.disk.domain.entity.TestBean;

public interface TestService 
{
	void addTest(final TestBean test);
	void updateTest(final TestBean test);
	void deleteTestByTestId(final Long testId);
	
	List<TestBean> findTestAll();
}
