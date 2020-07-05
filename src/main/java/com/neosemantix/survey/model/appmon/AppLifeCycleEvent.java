// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.model.appmon;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document 
@Data 
public class AppLifeCycleEvent {

	@Id
	private String id;
	
	private Date timestamp;
	
	private String event;
	
	public AppLifeCycleEvent(String ed) {
		timestamp = new Date();
		event = ed;
	}

}
