package com.example.fix4you_api.Rsql;

import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.qbuilders.visitors.MongoVisitor;
import com.github.rutledgepaulv.rqe.pipes.QueryConversionPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RsqlQueryService {

    private final MongoTemplate mongoTemplate;
    private final QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();

    public <T> List<T> findAll(Class<T> entityClass, String rsqlQuery, String sort) {
        Query query = new Query();

        //filtering
        if (rsqlQuery != null && !rsqlQuery.isEmpty()) {
            Condition<GeneralQueryBuilder> condition = pipeline.apply(rsqlQuery, entityClass);
            Criteria criteria = condition.query(new MongoVisitor());
            query.addCriteria(criteria);
        }

        // sorting
        if (sort != null && !sort.isEmpty()) {
            query.with(parseSortParameter(sort));
        }

        return mongoTemplate.find(query, entityClass);
    }

    private Sort parseSortParameter(String sort) {
        String[] sortFields = sort.split(",");
        Sort mongoSort = Sort.unsorted();

        for (String field : sortFields) {
            String[] parts = field.trim().split(" ");
            String fieldName = parts[0];
            Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            mongoSort = mongoSort.and(Sort.by(direction, fieldName));
        }

        return mongoSort;
    }
}
