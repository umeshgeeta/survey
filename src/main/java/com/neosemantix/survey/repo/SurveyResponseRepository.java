// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.SurveyResponse;

public interface SurveyResponseRepository extends ReactiveMongoRepository<SurveyResponse, String> {

}
