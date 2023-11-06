package com.namekart.amp2.UserEntities;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    public User() {
    }

    public Set<String> getRoles() {
        return roles;
    }

    boolean telegram;

    String tg_username;

    public boolean getTelegram() {
        return telegram;
    }

    public void setTelegram(boolean telegram) {
        this.telegram = telegram;
    }

    public String getTg_username() {
        return tg_username;
    }

    public void setTg_username(String tg_username) {
        this.tg_username = tg_username;
    }

    public Long getTgUserId() {
        return tgUserId;
    }

    public void setTgUserId(Long tg_userid) {
        this.tgUserId = tg_userid;
    }

    Long tgUserId,tglivychatid;

    public Long getTglivychatid() {
        return tglivychatid;
    }

    public void setTglivychatid(Long tglivychatid) {
        this.tglivychatid = tglivychatid;
    }

    int otp;

    public User(int otp, String firstName, String lastName, String email) {
        this.otp = otp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles=new HashSet<>();
    }

    public User(String firstName, String lastName, String email, Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer id;

    String firstName; String lastName;

    @Column(unique=true)
    String email;

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @OneToMany(mappedBy="user")
    List<Action> actions=new ArrayList<>();

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    Set<String> roles=new HashSet<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
