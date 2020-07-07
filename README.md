This is a survey application implemented using Spring Boot JavaRx and MongoDB. Use case is we install Mongo DB locally and run this Spring Boot Application on the machine so that other Web Application running on that VM is able to utilize Survey Capabilities. There are 3 main parts to the functionality:

- To define and save Survey Definitions which comprises of Survey Questions.
- To record a survery response.
- To aggregate survey reponses.

Current code focuses on using Spring Framrework Reactor techniquen (Flux and Mono) to store Survey Definition and Survey Responses. Work in progress is about aggreation followed by Rest API controller. End goal is to make API end points available so that UI can be developed subsequently.

It is a multi-tenant application. Each survey definition has a unique id internally generated and an owner. Owner name essentially corresponds to a different tenant while responses and survey aggregation is all tied with the unique survey definition id. For each user / owner, only the list of survey definitions of that owner will be returned and that way that user only sees her survey definitions and consequent response aggregation. Survey responses are always again a specified survey definition (there is no enforcement on the backend side to segregate these responses).

Code in 'dev' branch is work in progress and not production ready. Code in master branch has test cases and test covered.


