/*
 * Copyright 2013, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.betfair.tornjak.monitor.active.db;

import com.betfair.tornjak.monitor.active.Check;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Check that checks the health status of a {@link javax.sql.DataSource}
 * <p/>
 * The implementation tries to execute a configurable SQL, if any exception is thrown then the monitor
 * considers that the Database is not working. Note that no results from the SQL are inspected and no
 * ResultSets traversed.
 * <p/>
 * The default SQL to be executed is "select 1 from dual"
 */
public class Database implements Check {
    DataSource datasource;
    private String sql = "select 1 from dual";

    public Database(DataSource datasource) {
        this.datasource = datasource;
    }

    public Database(DataSource datasource, String sql) {
        this.datasource = datasource;
        this.sql = sql;
    }

    public void check() throws Exception {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = datasource.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // 
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    //
                }
            }
        }
    }

    public String getDescription() {
        return "Database query check, running '"+sql+"'";
    }

}
