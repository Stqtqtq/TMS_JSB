package com.tms.assignment_jsb.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tms.assignment_jsb.entity.Accounts;
import com.tms.assignment_jsb.repository.AccountsRepository;
import com.tms.assignment_jsb.repository.UserGroupsRepository;

@Service
public class AppUserDetailsService implements UserDetailsService{

  @Autowired
  private AccountsRepository accountsRepository;

  @Autowired
  private UserGroupsRepository userGroupsRepository;

  public AppUserDetailsService(AccountsRepository accountsRepository, UserGroupsRepository userGroupsRepository) {
    this.accountsRepository = accountsRepository;
    this.userGroupsRepository = userGroupsRepository;
  }
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Accounts account = accountsRepository.findByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("User not found with username " + username);
    }

    List<String> groups = userGroupsRepository.findGroupsByUsername(username);

    List<GrantedAuthority> authorities = groups.stream()
                                                .map(SimpleGrantedAuthority::new)
                                                .collect(Collectors.toList());    

    return new User(account.getUsername(), account.getPassword(), authorities);
  }
  
}
