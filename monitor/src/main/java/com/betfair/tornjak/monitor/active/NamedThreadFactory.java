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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copy of the DefaultThreadFactory but we allow to specify the prefix
 */
class NamedThreadFactory implements ThreadFactory {
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final String namePrefix;
    private ThreadFactory delegate;

    public NamedThreadFactory(String namePrefix) {
        delegate = Executors.defaultThreadFactory();
        this.namePrefix = namePrefix + "-";
    }

    public Thread newThread(Runnable r) {
        Thread thread = delegate.newThread(r);
        thread.setName(namePrefix + threadNumber.getAndIncrement());
        return thread;
    }
}
