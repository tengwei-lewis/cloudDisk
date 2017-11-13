package com.cloud.disk.domain.dao;

import com.cloud.disk.domain.entity.TestBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestDao extends JpaRepository<TestBean, Long>{

}
