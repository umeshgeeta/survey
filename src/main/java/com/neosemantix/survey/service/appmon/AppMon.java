// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.service.appmon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.neosemantix.survey.model.appmon.AppLifeCycleEvent;

import reactor.core.publisher.Mono;

@Component
public class AppMon implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private AppMonService amService;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {
		Mono<AppLifeCycleEvent> lce = amService.create("Application started");
		System.out.println("Created application life cycle event at " + lce.block().getTimestamp());
	}

}
