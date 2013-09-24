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

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockDataSource;

public class DatabaseTest extends BasicJDBCTestCaseAdapter {
    public void testHappyPath() throws Exception {
        MockDataSource dataSource = getJDBCMockObjectFactory().getMockDataSource();
        Database database = new Database(dataSource);
        database.check();

        verifyNumberStatements(1);
        verifySQLStatementExecuted("select 1 from dual");

        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();

    }
    
    public void testExceptionWhenExecuting() throws Exception {
        StatementResultSetHandler resultSetHandler = getStatementResultSetHandler();
        resultSetHandler.prepareThrowsSQLException("select 1 from dual");
        
        MockDataSource dataSource = getJDBCMockObjectFactory().getMockDataSource();
        Database database = new Database(dataSource);
        try {
            database.check();
            fail("Where is my exception?");
        } catch (Exception e) {
            // expected
        }

        verifyAllResultSetsClosed();
        verifyAllStatementsClosed();
        verifyConnectionClosed();

    }
    
}
