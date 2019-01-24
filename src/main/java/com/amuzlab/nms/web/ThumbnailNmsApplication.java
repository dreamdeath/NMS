package com.amuzlab.nms.web;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.snmp4j.smi.OID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amuzlab.nms.snmp.SimpleSnmpClient;

@SpringBootApplication
@MapperScan("com.amuzlab.nms.web.sqlmappers")
@EnableTransactionManagement
public class ThumbnailNmsApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ThumbnailNmsApplication.class, args);
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFatory(DataSource datasource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(datasource);
		sqlSessionFactory.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
		sqlSessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources("classpath:META-INF/sqlmaps/**/*.xml"));
		return (SqlSessionFactory) sqlSessionFactory.getObject();
		
		
	}

	@Bean
	public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
