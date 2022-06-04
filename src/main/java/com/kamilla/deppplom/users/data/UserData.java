package com.kamilla.deppplom.users.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kamilla.deppplom.users.Role;


@JsonPropertyOrder({"name", "login", "password", "role", "groups"})
public class UserData {
    public String name;
    public String login;
    public String password;
    public Role role;
    public String groups;

}
