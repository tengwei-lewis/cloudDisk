package com.cloud.disk.service.impl;

import com.cloud.disk.domain.dao.TestDao;
import com.cloud.disk.domain.entity.TestBean;
import com.cloud.disk.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class TestServiceImpl implements TestService
{
	@Autowired
	private TestDao testDao;

	@Override
	@Transactional(readOnly=true)
	public List<TestBean> findTestAll()
	{
		return testDao.findAll();
	}

	@Override
	@Transactional
	public void addTest(final TestBean test) 
	{
		testDao.save(test);
	}

	@Override
	@Transactional
	public void updateTest(final TestBean test) 
	{
		testDao.save(test);
	}

	@Override
	@Transactional
	public void deleteTestByTestId(final Long testId) 
	{
		testDao.delete(testId);
	}

}
