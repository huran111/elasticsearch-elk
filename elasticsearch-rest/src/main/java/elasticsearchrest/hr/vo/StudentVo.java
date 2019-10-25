package elasticsearchrest.hr.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StudentVo {
    private long docId;
    private String name;
    private int age;
    private String course;
    private long studyDate;
    private String mark;
}
