package com.addrs.addrs_user_management_service.utility;

import com.addrs.addrs_user_management_service.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetail implements UserDetails {

    private User user;
    public CustomUserDetail(User user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub
        return List.of(() -> user.getRole());
    }


    public String getFullname() {
        return user.getFirstName() + " " + user.getLastName();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return user.getActive();
    }
}
