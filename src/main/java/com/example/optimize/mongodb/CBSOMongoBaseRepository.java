package com.example.optimize.mongodb;

import java.util.List;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author chenws3
 */
@NoRepositoryBean
public interface CBSOMongoBaseRepository<T, ID> extends MongoRepository<T, ID> {

    List<T> batchSave(List<T> list);

    /**
     * page find
     *
     * @param conditionBson condition
     * @param sortBson sort
     * @param pageable page
     */
    PageableDTO<T> pageFind(Bson conditionBson, Bson sortBson, Pageable pageable);

    List<T> listPageFind(Bson conditionBson, Bson sortBson, Pageable pageable);

    List<T> findAllData(Bson conditionBson);

    long count(Bson bson);

}
