package com.example.optimize.mongodb;


import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.CollectionUtils;

public class CBSOMongoBaseRepositoryImpl<T, ID> extends SimpleMongoRepository<T, ID> implements CBSOMongoBaseRepository<T, ID> {

    protected final MongoOperations mongoTemplate;

    protected final MongoEntityInformation<T, ID> entityInformation;

    protected final Class<T> clazz;


    public CBSOMongoBaseRepositoryImpl(MongoEntityInformation<T, ID> metadata,
                                       MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoTemplate = mongoOperations;
        this.entityInformation = metadata;
        clazz = entityInformation.getJavaType();
    }

    @Override
    public List<T> batchSave(List<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return entityList;
        }
        String idName = getIdFiled(entityList.get(0));
        if (StringUtils.isBlank(idName)) {
            try {
                throw new Exception("no Annotation @Id");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        MongoConverter converter = mongoTemplate.getConverter();
        List<ReplaceOneModel<Document>> bulkOperationList = entityList.stream()
            .map(thing -> {
                getId(thing, idName);
                Document dbDoc = new Document();
                converter.write(thing, dbDoc);
                ReplaceOneModel<Document> replaceOneModel = new ReplaceOneModel(
                    Filters.eq("_id", dbDoc.get("_id")),
                    dbDoc,
                    new ReplaceOptions().upsert(true)
                );
                //generate id
                return replaceOneModel;
            })
            .collect(Collectors.toList());
        org.springframework.data.mongodb.core.mapping.Document annotation = clazz.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
        if (annotation != null) {
            String collection = annotation.collection();
            MongoCollection<Document> mongoCollection = mongoTemplate.getCollection(collection);
            Lists.partition(bulkOperationList, 5000).parallelStream().forEach(mongoCollection::bulkWrite);
        }

        return entityList;

    }

    @SneakyThrows
    @Override
    public PageableDTO<T> pageFind(Bson conditionBson, Bson sortBson, Pageable pageable) {
        long count = count(conditionBson);
        List<T> content = listPageFind(conditionBson, sortBson, pageable);
        PageableDTO pageableDTO = new PageableDTO(new PageImpl<>(content, pageable, count));
        return pageableDTO;
    }

    @SneakyThrows
    @Override
    public List<T> listPageFind(Bson conditionBson, Bson sortBson, Pageable pageable) {

        String collection = getCollection();
        assert collection != null;

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
        }
        FindIterable<Document> iterable =
            mongoTemplate.getCollection(collection)
                .find(conditionBson)
                .sort(sortBson)
                .skip(pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize());
        List<T> content = Lists.newArrayList();
        for (Document document : iterable) {
            T t = clazz.getDeclaredConstructor().newInstance();

            for (Field declaredField : declaredFields) {
                String name = declaredField.getName();
                if (StringUtils.equalsIgnoreCase(name, "id")) {
                    declaredField.set(t, document.get("_id").toString());
                    continue;
                }
                Object value = document.get(name);
                if (BigDecimal.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, value == null ? null : new BigDecimal((String) value));
                    continue;
                }
                if (Set.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, value == null ? null : new TreeSet<String>((ArrayList) (value)));
                    continue;
                }
                if (boolean.class.isAssignableFrom(declaredField.getType())) {
                    if (Objects.nonNull(value)) {
                        declaredField.set(t, value);
                    }
                    continue;
                }
                declaredField.set(t, value);

            }
            content.add(t);

        }
        return content;
    }

    @SneakyThrows
    @Override
    public List<T> findAllData(Bson conditionBson) {
        String collection = getCollection();
        assert collection != null;

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
        }
        FindIterable<Document> iterable =
            mongoTemplate.getCollection(collection)
                .find(conditionBson);
        List<T> content = Lists.newArrayList();
        for (Document document : iterable) {
            T t = clazz.getDeclaredConstructor().newInstance();

            for (Field declaredField : declaredFields) {
                String name = declaredField.getName();
                if (StringUtils.equalsIgnoreCase(name, "id")) {
                    declaredField.set(t, document.get("_id").toString());
                    continue;
                }
                Object value = document.get(name);
                if (BigDecimal.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, value == null ? null : new BigDecimal((String) value));
                    continue;
                }
                if (Set.class.isAssignableFrom(declaredField.getType())) {
                    declaredField.set(t, value == null ? null : new TreeSet<String>((ArrayList) (value)));
                    continue;
                }

                declaredField.set(t, value);


            }
            content.add(t);

        }
        return content;
    }

    @Override
    public long count(Bson conditionBson) {
        return mongoTemplate.getCollection(getCollection()).countDocuments(conditionBson);
    }


    private ID getId(T entity, String idName) {

        ID id = null;
        try {
            Class<?> aClass = entity.getClass();
            Field field = aClass.getDeclaredField(idName);
            field.setAccessible(true);
            id = (ID) field.get(entity);
            if (Objects.isNull(id)) {
                ObjectId objectId = ObjectId.get();
                field.set(entity, objectId.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    private String getIdFiled(T t) {
        Class<?> aClass = t.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Id annotation = declaredField.getAnnotation(Id.class);
            if (Objects.nonNull(annotation)) {
                return declaredField.getName();
            }
        }
        return null;
    }

    private String getCollection() {
        org.springframework.data.mongodb.core.mapping.Document annotation = clazz.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
        if (annotation != null) {
            return annotation.collection();
        }
        return null;
    }

}