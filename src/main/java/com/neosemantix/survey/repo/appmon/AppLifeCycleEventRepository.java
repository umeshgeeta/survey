// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.repo.appmon;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.appmon.AppLifeCycleEvent;

public interface AppLifeCycleEventRepository extends ReactiveMongoRepository<AppLifeCycleEvent, String> {

}
