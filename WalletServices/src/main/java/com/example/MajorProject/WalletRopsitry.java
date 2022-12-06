package com.example.MajorProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRopsitry extends JpaRepository <Wallet,Integer>{
     Wallet findByUserName(String userName);

}
