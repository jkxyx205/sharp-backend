package com.rick.backend.module.demo;

import com.rick.backend.module.demo.model.CourseDTO;
import com.rick.backend.module.demo.model.CourseDTO2;
import com.rick.db.repository.EntityDAOSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SelectTest {

    @Autowired
    EntityDAOSupport entityDAOSupport;

    @Test
    public void testSelect() {
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setUserId(1L);
        CourseDTO select = entityDAOSupport.select(courseDTO);
        System.out.println(select);

        CourseDTO2 courseDTO2 = new CourseDTO2();
        courseDTO2.setUserId(1113884208805187584L);
        entityDAOSupport.select(courseDTO2);
        System.out.println(courseDTO2);
    }
}
