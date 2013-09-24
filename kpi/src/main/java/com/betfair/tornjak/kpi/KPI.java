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

package com.betfair.tornjak.kpi;

/**
 * Represents a specific instance of a Key Performance Indicator.
 * <p>
 * Each KPI instance has at least a name, and some measurement/metric. Since the metric can vary, this base class 
 * should be extended to provide more meaningful implementations.
 */
public abstract class KPI {

	private String name;
	
	
	/**
	 * This imposes some effort on implementations, but it results in easier code for creating beans, and enforces
	 * the fact that the name is meant to be immutable.
	 */
	public KPI(String name) {
	    this.name = name;
	}
	
	
	/**
	 * Return a string which uniquely identifies this type of KPI. Default impl is to rturn the current object's class.
	 * Method can be used for mapping to serialisers etc, (where using the class itself is too brittle for mocks/tests)
	 */
	public String getKPIType() {
	    return this.getClass().getName();
	}
	
	
	public String getName() {
		return name;
	}
}
