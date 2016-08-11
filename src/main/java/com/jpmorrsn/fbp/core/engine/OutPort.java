/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */

package com.jpmorrsn.fbp.core.engine;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface OutPort {
	
	/***
	 * 
	 * Annotation for component, describing individual output port - this will be displayed
	 * by DrawFBP's Display Port Info command 
	 * 
	 */

 
  String value() default ""; // either value or valueList must be present

  int setDimension() default 0;

  String[] valueList() default {};

  boolean arrayPort() default false;

  boolean optional() default false;

  boolean fixedSize() default false; // only relevant if arrayPort - cannot coexist with optional

  String description() default "";

  Class type() default Object.class; // type of object generated from this input port

}
