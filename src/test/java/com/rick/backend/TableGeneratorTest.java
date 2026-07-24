package com.rick.backend;

import com.rick.backend.module.demo.entity.Course;
import com.rick.db.plugin.generator.TableGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TableGeneratorTest {

    @Autowired
    private TableGenerator tableGenerator;

    @Test
    public void generateTable() {
        tableGenerator.createTable(Course.class);
    }

}
