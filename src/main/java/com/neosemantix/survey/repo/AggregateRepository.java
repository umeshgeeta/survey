// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.SurveyAggregate;

import reactor.core.publisher.Mono;

public interface AggregateRepository extends ReactiveMongoRepository<SurveyAggregate, String>  {

	@Query("{ 'surveyDefId': ?0}")		
	Mono<SurveyAggregate> findBySurveyDefId(String sdId);
}
