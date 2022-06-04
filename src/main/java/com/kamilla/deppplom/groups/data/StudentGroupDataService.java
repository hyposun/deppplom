package com.kamilla.deppplom.groups.data;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentGroupDataService {
    @Autowired
    StudentGroupService service;

    private final CsvMapper mapper = new CsvMapper();

    public List<StudentGroup> getGroupsFromCsv(InputStream inputStream) throws IOException {

        byte[] bytes = inputStream.readAllBytes();
        String data = new String(bytes);

        MappingIterator<StudentGroupData> it = mapper
                .readerFor(StudentGroupData.class)
                .with(getSchema())
                .readValues(data);

        return it.readAll()
                .stream()
                .map(item -> {
                    StudentGroup group = new StudentGroup();
                    group.setTitle(item.title);
                    return group;
                })
                .map(item -> service.update(item))
                .collect(Collectors.toList());
    }
    private CsvSchema getSchema() {
        CsvSchema schema = CsvSchema.builder()
                .setColumnSeparator(';')
                .addColumn("title")
                .build();

        return schema;

    }
}
