package com.example.MajorProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    //method 1:find userDetails by findBy userNmae

    //User findByUserName(String userName);

    //method 2 :finding user details with quary
    @Query("select u from User u where u.userName =:userName")
    User userfindByQuary(String userName);


}
