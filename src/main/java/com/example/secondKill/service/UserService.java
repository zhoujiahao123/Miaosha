package com.example.secondKill.service;

import com.example.secondKill.dao.UserDao;
import com.example.secondKill.domain.MiaoshaUser;
import com.example.secondKill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired(required = false)
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx() {
        User user1 = new User();
        user1.setId(2);
        user1.setName("2222");
        userDao.insert(user1);

        User user2 = new User();
        user2.setId(1);
        user2.setName("1111");
        userDao.insert(user2);
        return true;
    }

}
