package com.example.MajorProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserContoller {

    @Autowired
    UserServices userServices;
 @PostMapping
    public ResponseEntity createAccountforUser(@RequestBody()UserRequset userRequset){

     userServices.cratedAccountForUserService(userRequset);

     return new ResponseEntity<>("Success", HttpStatus.OK);
 }
 // get user DetailsBy userNmae
@GetMapping
    public ResponseEntity getUserDetails(@RequestParam("userName")String userName)throws Exception{

     User user =userServices.getUSerDetilesServices(userName);

     return new ResponseEntity<>(user,HttpStatus.FOUND);
}


}
