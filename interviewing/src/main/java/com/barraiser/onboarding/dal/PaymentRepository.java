package com.barraiser.onboarding.dal;

import com.barraiser.onboarding.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentDAO, String> {
    PaymentDAO findByOrderId(String orderId);

    PaymentDAO findByUserIdAndPaymentId(String userId, String paymentId);

    PaymentDAO findByPaymentId(String paymentId);

    List<PaymentDAO> findAllByUserIdAndStatus(String userName, PaymentStatus orderCreated);

    PaymentDAO findByRazorpayPaymentId(String paymentId);
}
//    public PaymentDAO findByUserIdAndPaymentId(final String userId, final String paymentId) {
//        return this.dynamoDBMapper.load(PaymentDAO.builder()
//                .paymentId(paymentId)
//                .userId(userId)
//                .build());
//    }
//
//    public List<PaymentDAO> findAllByUserIdAndStatus(final String userId, final PaymentStatus status) {
//        final DynamoDBQueryExpression<PaymentDAO> expression = new DynamoDBQueryExpression<PaymentDAO>()
//                .withHashKeyValues(PaymentDAO.builder()
//                        .userId(userId)
//                        .build());
//
//        return this.dynamoDBMapper.query(PaymentDAO.class, expression).stream()
//                .filter(x -> x.getStatus() == status)
//                .collect(Collectors.toList());
//    }
//
//    public void save(final PaymentDAO paymentDAO) {
//        this.dynamoDBMapper.save(paymentDAO.toBuilder()
//                .createdOn(Instant.now().getEpochSecond())
//                .build());
//    }
//
//    public PaymentDAO findByOrderId(final String orderId) {
//        final PaymentDAO payment = PaymentDAO.builder().orderId(orderId).build();
//        final DynamoDBQueryExpression<PaymentDAO> expression = new DynamoDBQueryExpression<PaymentDAO>()
//                .withHashKeyValues(payment)
//                .withIndexName("orderId-index")
//                .withConsistentRead(false);
//        final PaginatedQueryList<PaymentDAO> result = this.dynamoDBMapper.query(PaymentDAO.class, expression);
//        if (result.size() != 1) {
//
//            log.error("Number of payments found for the orderId is not one. Total count is :" + result.size());
////            throw new RuntimeException("Either no data or too many records found.");
//            return null;
//        }
//
//        return result.get(0);
//    }
//}
