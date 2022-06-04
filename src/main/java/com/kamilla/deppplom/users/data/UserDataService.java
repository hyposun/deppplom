package com.kamilla.deppplom.users.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDataService {
    @Autowired
    private UserService service;

    private final CsvMapper mapper = new CsvMapper();

    public List<User> getUsersFromCsv(InputStream inputStream) throws IOException {

        byte[] bytes = inputStream.readAllBytes();
        String data = new String(bytes);

        MappingIterator<UserData> it = mapper
                .readerFor(UserData.class)
                .with(getSchema())
                .readValues(data);


        return it.readAll()
                .stream()
                .map(item -> {
                    User user = new User();
                    user.setRole(item.role);
                    user.setGroups(getGroups(item.groups));
                    user.setPassword(item.password);
                    user.setLogin(item.login);
                    user.setName(item.name);
                    return user;
                })
                .map(item -> service.update(item))
                .collect(Collectors.toList());

    }

    private Set<StudentGroup> getGroups(String groups) {

        if (groups != null && !StringUtils.isBlank(groups)) {
            return Arrays.stream(groups.split(","))
                    .map(NumberUtils::toInt)
                    .filter(it -> it != 0)
                    .map(it -> {
                        StudentGroup group = new StudentGroup();
                        group.setId(it);
                        return group;
                    })
                    .collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }

    private CsvSchema getSchema() {
        CsvSchema schema = CsvSchema.builder()
                .setColumnSeparator(';')
                .addColumn("name")
                .addColumn("login")
                .addColumn("password")
                .addColumn("role")
                .addColumn("groups")
                .build();

        return schema;

    }
}
