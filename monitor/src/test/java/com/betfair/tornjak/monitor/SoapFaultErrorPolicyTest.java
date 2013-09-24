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

package com.betfair.tornjak.monitor;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

public class SoapFaultErrorPolicyTest {
    private static final String AN_ERROR_CODE = "AN_ERROR_CODE";
    private static final boolean YES = true;
    private static final boolean NO = false;
    private static final String OTHER_ERROR_CODE = "OTHER_ERROR_CODE";

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = NoSuchMethodException.class)
    public void invalidClass() throws Exception {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(getClass());
    }

    @Test
    public void getErrorCodeDoesNotReturnAString() throws Exception {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(ErrorCodeIsNotString.class);
        assertThat(namingStrategy.countsAsError(new ErrorCodeIsNotString()), is(YES));
    }

    @Test
    public void criticalError() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class, AN_ERROR_CODE);
        assertThat(namingStrategy.countsAsError(new SoapFault(OTHER_ERROR_CODE)), is(YES));
    }

    @Test
    public void nonCriticalError() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class, AN_ERROR_CODE);
        assertThat(namingStrategy.countsAsError(new SoapFault(AN_ERROR_CODE)), is(NO));
    }

    @Test
    public void otherExceptionsUseTheDefaultErrorPolicy() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class);
        assertThat(namingStrategy.countsAsError(new NumberFormatException()), is(YES));
    }
    
    @Test
    public void errorUsingReflectionUseDefaultNamingPolicy() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class);
        assertThat(namingStrategy.countsAsError(new ExceptionalSoapFault()), is(YES));
    }

    @Test
    public void subclasses() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class, "SUBCLASS_ERROR_CODE");
        assertThat(namingStrategy.countsAsError(new SoapFaultSubClass()), is(NO));
    }
    
    @Test
    public void anyResultIsOk() throws NoSuchMethodException {
        SoapFaultErrorPolicy namingStrategy = new SoapFaultErrorPolicy(SoapFault.class);
        assertThat(namingStrategy.countsAsError(new Object()), is(NO));
    }

    private static class SoapFault extends Throwable {

        private Data data;

        public SoapFault(String errorCode) {
            data = new Data(errorCode);
        }


        public Data getFaultInfo() {
            return data;
        }

        public static class Data {
            private String errorCode;

            public Data(String errorCode) {
                this.errorCode = errorCode;
            }

            public String getErrorCode() {
                return errorCode;
            }
        }
    }

    private static class ErrorCodeIsNotString extends Throwable {
        public Data getFaultInfo() {
            return new Data();
        }

        private class Data {
            public boolean getErrorCode() {
                return true;
            }
        }
    }
    
    private static class ExceptionalSoapFault extends SoapFault {
        private ExceptionalSoapFault() {
            super(AN_ERROR_CODE);
        }

        public Data getFaultInfo() {
            throw new RuntimeException("Really broken and unexpected");
        }
    }

    private static class SoapFaultSubClass extends SoapFault {
        private SoapFaultSubClass() {
            super(AN_ERROR_CODE);
        }

        public Data getFaultInfo() {
            return new DataSubClass();
        }

        public static class DataSubClass extends Data {
            public DataSubClass() {
                super(AN_ERROR_CODE);
            }

            @Override
            public String getErrorCode() {
                return "SUBCLASS_ERROR_CODE";
            }
        }
    }
}
    
