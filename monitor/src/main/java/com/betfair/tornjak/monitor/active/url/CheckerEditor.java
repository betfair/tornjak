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

package com.betfair.tornjak.monitor.active.url;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;

/**
 * Convenience editor that translates a String to a {@link CheckCanConnect}
 * if the string is "connect" or to a {@link CheckResponseMatches} otherwise
 */
public class CheckerEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        }

        if ("connect".equalsIgnoreCase(text)) {
            setValue(new CheckCanConnect());
        } else {
            setValue(new CheckResponseMatches(Pattern.compile(text)));
        }
    }
}
