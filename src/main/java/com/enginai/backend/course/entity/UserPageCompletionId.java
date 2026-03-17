package com.enginai.backend.course.entity;

import com.enginai.backend.authn.entity.User;

import java.io.Serializable;
import java.util.Objects;

public class UserPageCompletionId implements Serializable {

    private User user;
    private CoursePage page;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPageCompletionId that)) return false;
        return Objects.equals(user, that.user) && Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, page);
    }
}
