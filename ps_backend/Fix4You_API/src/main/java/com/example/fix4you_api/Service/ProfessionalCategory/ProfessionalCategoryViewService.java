package com.example.fix4you_api.Service.ProfessionalCategory;

import com.example.fix4you_api.Data.Enums.EnumUserType;
import com.example.fix4you_api.Data.Models.User;
import com.example.fix4you_api.Rsql.RsqlQueryService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ProfessionalCategoryViewService {

    private final MongoTemplate mongoTemplate;
    private final RsqlQueryService rsqlQueryService;

    public List<ProfessionalCategoryView> findAll() {
        return mongoTemplate.findAll(ProfessionalCategoryView.class);
    }

    public List<ProfessionalCategoryView> createProfessionalCategoryView() {
        mongoTemplate.dropCollection(ProfessionalCategoryView.class);

        MatchOperation matchProfessionals = Aggregation.match(Criteria.where("userType").is(EnumUserType.PROFESSIONAL));

        AggregationOperation lookupCategories = new CustomAggregationOperation(
                """
                        {
                          "$lookup": {
                            "from": "CategoryDescriptions",
                            "let": { "userId": { "$toString": "$_id" } },
                            "pipeline": [
                              { "$match": { "$expr": { "$eq": ["$professionalId", "$$userId"] } } }
                            ],
                            "as": "categoryDescriptions"
                          }
                        }
                        """
        );

        ProjectionOperation projectFields = Aggregation.project()
                .and("_id").as("id")
                .and("email").as("email")
                .and("dateCreation").as("dateCreation")
                .and("userType").as("userType")
                .and("name").as("name")
                .and("phoneNumber").as("phoneNumber")
                .and("location").as("location")
                .and("profileImage").as("profileImage")
                .and("description").as("description")
                .and("nif").as("nif")
                .and("languages").as("languages")
                .and("locationsRange").as("locationsRange")
                .and("acceptedPayments").as("acceptedPayments")
                .and("categoryDescriptions").as("categoryDescriptions");

        // Step 4: Define and execute the aggregation
        Aggregation aggregation = Aggregation.newAggregation(matchProfessionals, lookupCategories, projectFields);

        AggregationResults<ProfessionalCategoryView> results =
                mongoTemplate.aggregate(aggregation, mongoTemplate.getCollectionName(User.class), ProfessionalCategoryView.class);

        List<ProfessionalCategoryView> professionalCategoryViews = results.getMappedResults();

        mongoTemplate.insertAll(professionalCategoryViews);

        return professionalCategoryViews;
    }

    public List<ProfessionalCategoryView> getProfessionalCategoryViews(String filter) {
        if (isEmpty(filter)) {
            return mongoTemplate.findAll(ProfessionalCategoryView.class);
        } else return rsqlQueryService.findAll(ProfessionalCategoryView.class, filter, null);
    }

    // Custom AggregationOperation class for inserting custom JSON stages
    public static class CustomAggregationOperation implements AggregationOperation {
        private final String jsonOperation;

        public CustomAggregationOperation(String jsonOperation) {
            this.jsonOperation = jsonOperation;
        }

        @Override
        public Document toDocument(AggregationOperationContext context) {
            return Document.parse(jsonOperation);
        }
    }
}
