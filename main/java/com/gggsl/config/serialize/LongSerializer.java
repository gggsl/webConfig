 /*		
 * ===========================================================================================
 * = COPYRIGHT		          
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or nondisclosure 	
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or 
 *   disclosed except in accordance with the terms in that agreement.   		
 *     Copyright (C) 2018-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: 		
 *              		
 * Revision History:		
 * Date	                 Author	                Action
 * 2018-02-08  	         west.w           			Create
 * ===========================================================================================
 */
 
/**
 * 
 */
package com.gggsl.config.serialize;

 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.databind.JsonSerializer;
 import com.fasterxml.jackson.databind.SerializerProvider;
 import org.springframework.boot.jackson.JsonComponent;
 import java.io.IOException;

 @JsonComponent
 public class LongSerializer extends JsonSerializer<Long>{

     /* (non-Javadoc)
      * @see com.fasterxml.jackson.databind.JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
      */
     @Override
     public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
             throws IOException {
         gen.writeString(value.toString());

     }


 }
