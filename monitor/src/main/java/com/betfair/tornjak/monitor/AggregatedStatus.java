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
 * A utility class for aggregating a group of Status object to return a 
 * derived Status.
 *  
 * @author scarrottr
 */
public class AggregatedStatus {

	private int okCount;
	private int warnCount;
	private int failCount;

	public int getOkCount() {
		return okCount;
	}

	public int getWarnCount() {
		return warnCount;
	}

	public int getFailCount() {
		return failCount;
	}
	
	public void accountFor(Status s, Status max) {
		// If the max posible is 'OK', we return 'OK' regardless of the status.
		if(max.equals(Status.OK)){
			okCount++;
			return;
		}

		// If the max posible is 'FAIL', and the service has indeed failed we 'FAIL'.
		if(max.equals(Status.FAIL) && s.equals(Status.FAIL)){
			failCount++;
			return;
		}
		
		//Now, either the service is 'OK' or we must 'WARN'
		if(s.equals(Status.OK)){
			okCount++;
		}else{
			warnCount++;
		}

	}

	public Status getStatus() {
		if (getFailCount() > 0) {
			return Status.FAIL;
		}
		if (getWarnCount() > 0) {
			return Status.WARN;
		}
		if (getOkCount() > 0) {
			return Status.OK;
		}
		return Status.FAIL;
	}

}
