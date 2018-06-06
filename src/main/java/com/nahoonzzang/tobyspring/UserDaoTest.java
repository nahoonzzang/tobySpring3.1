package com.nahoonzzang.tobyspring;

import org.springframework.boot.SpringApplication;

public class UserDaoTest {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        UserDao userDao = new DaoFactory().userDao();

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
