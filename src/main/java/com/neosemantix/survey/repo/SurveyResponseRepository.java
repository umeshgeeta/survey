// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.SurveyResponse;

import reactor.core.publisher.Flux;

public interface SurveyResponseRepository extends ReactiveMongoRepository<SurveyResponse, String> {

	@Query("{'surveyId': ?0}")
	Flux<SurveyResponse> findAllResponses(String surveyId);
}
