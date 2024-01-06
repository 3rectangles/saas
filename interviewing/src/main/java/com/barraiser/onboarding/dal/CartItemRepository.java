package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemDAO, String> {
//
//    public CartItem findById(final String userId, final String itemId) {
//        return this.dynamoDBMapper.load(CartItem.builder()
//                .itemId(itemId)
//                .userId(userId)
//                .build());
//    }
//
//    public void delete(final CartItem cartItem) {
//        this.dynamoDBMapper.delete(cartItem);
//    }
//
//    public void save(final CartItem cartItem) {
//        this.dynamoDBMapper.save(cartItem);
//    }
//
//    public List<CartItem> findAllByUserId(final String userId) {
//        final DynamoDBQueryExpression<CartItem> queryExpression = new DynamoDBQueryExpression<CartItem>()
//                .withHashKeyValues(CartItem.builder()
//                        .userId(userId)
//                        .build());
//        final PaginatedQueryList<CartItem> result = this.dynamoDBMapper.query(CartItem.class, queryExpression);
//
//        return result.size() == 0 ? Collections.emptyList() : result;
//    }
}
