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

import java.net.URLConnection;
import java.io.IOException;

/**
 * Checks that it can open a connection to a URL.
 * <p/>
 * Note that it only checks that a connection can be openned, no check is done
 * about the returned HTTP codes or the content
 */
public class CheckCanConnect implements Checker {
    public void check(URLConnection urlConnection) throws Exception {
        urlConnection.connect();
        closeStreamQuietly(urlConnection);
    }

    /**
     * URLConnection.connect may leave an open stream, we clean up here.
     */
    private void closeStreamQuietly(URLConnection urlConnection) {
        try {
            urlConnection.getInputStream().close();
        } catch (IOException ignored) {
            // The service can return a 500 which is translated to a FileNotFoundException
            // As we just want to check that we are ok, we want to ignore this exception
            // Note that we read the stream to close the connection
        }
    }

    @Override
    public String toString() {
        return "CheckCanConnect()";
    }
}
