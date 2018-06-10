package com.nahoonzzang.tobyspring;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoTest {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        ApplicationContext context =
                new AnnotationConfigApplicationContext(DaoFactory.class);

        // getBean() 메소드는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드
        // "userDao"는 ApplicationContext에 등록된 빈의 이름(빈의 이름은 팩토리의 @Bean어노테이선이 붙은 메소드의 이름)
        // 즉, DaoFactory의 userDao() 메소드를 호출해서 그 결과를 가져온다고 생각하면 된다.
        UserDao userDao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("0828namjin");
        user.setName("김나훈");
        user.setPassword("single");

        try{
            userDao.add(user);

            System.out.println(user.getId() + " 등록 성공");

            User user2 = userDao.get(user.getId());

            System.out.println(user2.getName());

            System.out.println(user2.getPassword());

            System.out.println(user2.getId() + " 조회 성공");

        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("실패");
        }

    }
}
