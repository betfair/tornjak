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

/**
 *
 */
public class StatusChangeEvent {
    private StatusSource source;
    private Status oldStatus;
    private Status newStatus;

    public StatusChangeEvent(StatusSource source, Status oldStatus, Status newStatus) {
        this.source = source;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public StatusSource getSource() {
        return source;
    }

    public Status getOldStatus() {
        return oldStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    @Override
    public String toString() {
        return "StatusChangeEvent[source="+source+", oldStatus="+oldStatus+", newStatus="+newStatus+"]";
    }
}
