package com;

import lombok.Data;

@Data
public class StudentVo {
    private long docId;
    private String name;
    private int age;
    private String course;
    private long studyDate;
    private String mark;
}
