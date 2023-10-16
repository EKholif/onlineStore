package com.eee.common.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(name= "email", length = 85, nullable = false)
    private String email;
    @Column(name= "password", length = 85, nullable = false)

    private String password;
    @Column(name= "fristName", length = 85, nullable = false)

    private String fristName;
    @Column(name= "lastName", length = 85, nullable = false)

    private String lastName;

    @Column (name= "user_bio")
    private byte[] user_bio;
    @ManyToMany
    @JoinTable( name= "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns =@JoinColumn(name="role_id"))
    @JsonIgnore
    private Set<Role> roles =new HashSet<>();

    public byte[] getUser_bio() {
        return user_bio;
    }

    public void setUser_bio(byte[] user_bio) {
        this.user_bio = user_bio;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole( Role role){
        this.roles.add(role);
    }
    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private boolean enable ;
    public User() {
    }

    public User(String email, String password, String fristName, String lastName) {
        this.email = email;
        this.password = password;
        this.fristName = fristName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFristName() {
        return fristName;
    }

    public void setFristName(String fristName) {
        this.fristName = fristName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


}
