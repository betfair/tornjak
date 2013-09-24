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

package com.betfair.tornjak.monitor.active;

/**
 * I know how to actively query the dependency to see if still works
 */
public interface Check {
    /**
     * Implementing classes should throw an Exception if the dependency is not working. We prefer this
     * option over returning a true/false so the implementing class can/must provide the reason why the
     * check failed as the Exception message.
     *
     * @throws Exception why did the check fail. We will use the exception message as the reason.
     */
    void check() throws Exception;

    /**
     * Gives a description of this check which will be used anywhere the result of this check is exposed
     * (e.g. diagnostic pages).
     */
    String getDescription();

}
