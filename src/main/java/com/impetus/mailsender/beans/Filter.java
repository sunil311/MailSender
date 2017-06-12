package com.impetus.mailsender.beans;

import java.util.Date;

public class Filter {
    public Filter(String client, String project, Date dob, Date doj, Date doa, String location) {
        super();
        this.client = client;
        this.project = project;
        this.dob = dob;
        this.doj = doj;
        this.doa = doa;
        this.location = location;
    }

    public String client;
    public String project;
    public Date dob;
    public Date doj;
    public Date doa;
    public String location;
}
