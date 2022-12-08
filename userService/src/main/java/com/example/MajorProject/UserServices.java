package com.example.MajorProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class UserServices {

    @Autowired
    UserRepository userRepositorys;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public final String Redis_prefix_Key="user::";
    public final String create_wallet_topic="create_wallet";

    public void cratedAccountForUserService(UserRequset userRequset){

    // user request class convert to user class
       User user = User.builder().userName(userRequset.getUserName())
               .email(userRequset.getEmail())
               .name(userRequset.getName()).age(userRequset.getAge())
               .mobile(userRequset.getMobile())
               .city(userRequset.getCity()).build();

              // save in database
               userRepositorys.save(user);

               //save In cache
              saveInCache(user);

              //sending a message through kafka
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("userName",user.getUserName());
        jsonObject.put("name",user.getName());

        //convert jsonobject to string bcz message is in string formate
        String message =jsonObject.toString();
        kafkaTemplate.send(create_wallet_topic,message);
        String name ="nikitha";
        kafkaTemplate.send("myname","name");
    }

    private void saveInCache(User user) {

        Map map = objectMapper.convertValue(user,Map.class);
        redisTemplate.opsForHash().putAll(Redis_prefix_Key+user.getUserName(),map);
        redisTemplate.expire(Redis_prefix_Key+user.getUserName(), Duration.ofHours(12));
    }

    public User getUSerDetilesServices(String userName) throws Exception{

        // 1. find in cache
        Map map=redisTemplate.opsForHash().entries(Redis_prefix_Key+userName);


        if(map==null || map.size()==0){
            // find in db

            //User user =userRepositorys.findByUserName(userName);
            User user=userRepositorys.userfindByQuary(userName);
            if (user==null){
                saveInCache(user);
            }else {
                throw  new UserNotFoundEXception();
            }
            return user;
        }else {

            //found in the cache ---> return cache
            User object =objectMapper.convertValue(map,User.class);
            return object;
        }


    }

}
