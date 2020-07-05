// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.service.appmon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neosemantix.survey.model.appmon.AppLifeCycleEvent;
import com.neosemantix.survey.repo.appmon.AppLifeCycleEventRepository;

import reactor.core.publisher.Mono;

@Service
public class AppMonService {

	@Autowired
	private final AppLifeCycleEventRepository alcRepository;
	
	public AppMonService(AppLifeCycleEventRepository adr) {
		this.alcRepository = adr;
	}
	
	public Mono<AppLifeCycleEvent> create(String event){
		return this.alcRepository.save(new AppLifeCycleEvent(event));
	}

}
