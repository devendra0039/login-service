package com.addrs.addrs_user_management_service.repository;

import com.addrs.addrs_user_management_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByEmail(String email);

}
