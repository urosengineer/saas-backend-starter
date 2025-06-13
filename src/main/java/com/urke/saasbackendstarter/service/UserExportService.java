package com.urke.saasbackendstarter.service;

import com.urke.saasbackendstarter.domain.Organization;
import com.urke.saasbackendstarter.domain.User;

import java.util.List;

public interface UserExportService {
    List<User> findExportUsers(Organization org, String email);
    byte[] exportToExcel(List<User> users);
    byte[] exportToPdf(List<User> users);
}
