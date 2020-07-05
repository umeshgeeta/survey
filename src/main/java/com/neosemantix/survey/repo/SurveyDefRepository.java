// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.neosemantix.survey.model.SurveyDef;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SurveyDefRepository extends ReactiveMongoRepository<SurveyDef, String>  {

	@Query("{ 'name': ?0, 'owner': ?1 }")		//	"{ 'firstname': ?0, 'lastname': ?1}"
	Flux<SurveyDef> findByNameAndOwner(String name, String owner);
	
	@Query(value="{'id' : $0}", delete = true)
	Mono<Void> deleteById (String id);

}
