package com.jpmorrsn.fbp.engine;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface InPort {

  /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  String value() default ""; // either value or valueList must be present

  int setDimension() default 0;

  String[] valueList() default {};

  boolean arrayPort() default false;

  boolean fixedSize() default false; // only allowed if arrayPort

  String description() default "";

  Class type() default Object.class; // type of object expected on this input port
  
  boolean optional() default false;


}
