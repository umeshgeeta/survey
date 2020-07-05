// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.repo.appmon;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.appmon.AppLifeCycleEvent;

public interface AppLifeCycleEventRepository extends ReactiveMongoRepository<AppLifeCycleEvent, String> {

}
