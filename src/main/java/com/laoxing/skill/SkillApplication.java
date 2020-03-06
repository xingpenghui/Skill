package com.laoxing.skill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.laoxing.skill.dao")
@EnableTransactionManagement
@EnableScheduling //启用Spring Task
public class SkillApplication {

	public static void main(String[] args) {

		//SpringFactoriesLoader loader;
		SpringApplication.run(SkillApplication.class, args);
	}

}
