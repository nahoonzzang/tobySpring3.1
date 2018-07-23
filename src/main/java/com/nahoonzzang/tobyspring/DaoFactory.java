package com.nahoonzzang.tobyspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration // 애프릴케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {
    @Bean // 오브젝트 생성을 담당하는 IoC용 메소드라는 표시, 이 메소드의 이름이 빈의 이름이 됨
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setDataSource(dataSource());

        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/testdb");
        dataSource.setUsername("root");
        dataSource.setPassword("a10234");

        return dataSource;
    }
}
