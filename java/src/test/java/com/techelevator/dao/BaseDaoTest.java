package com.techelevator.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestingDatabaseConfig.class)
public abstract class BaseDaoTest {

    @Autowired
    protected DataSource dataSource;

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

}
