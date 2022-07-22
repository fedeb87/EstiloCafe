package com.federicoberon.estilocafe.datasource.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.federicoberon.estilocafe.model.OrderEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface OrdersDao extends BaseDao<OrderEntity>{

    @Query("SELECT * FROM orders")
    Flowable<List<OrderEntity>> getAllQuestions();

    @Query("SELECT * FROM orders WHERE category = :quizType")
    Flowable<List<OrderEntity>> getAllQuestionsByCategory(int quizType);

    @Query("SELECT q.* FROM orders q WHERE q.id = :id")
    Flowable<OrderEntity> getQuestionById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Maybe<List<Long>> insertQuestions(List<OrderEntity> questionsList);

    @Query("DELETE FROM orders WHERE id IN (:ids)")
    Completable deleteQuestions(List<Long> ids);

    @Query("SELECT count(*) FROM orders")
    int getQuestionsCount();
    /* 0 (false) and 1 (true). */
}