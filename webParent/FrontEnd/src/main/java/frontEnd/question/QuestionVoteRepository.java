package frontEnd.question;

import com.onlineStoreCom.entity.question.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionVoteRepository extends JpaRepository<QuestionVote, Integer> {

    @Query("SELECT v FROM QuestionVote v WHERE v.question.id = ?1 AND v.customer.id = ?2")
    QuestionVote findByQuestionAndCustomer(Integer questionId, Integer customerId);

    @Query("SELECT v FROM QuestionVote v WHERE v.question.product.id = ?1 AND v.customer.id = ?2")
    List<QuestionVote> findByProductAndCustomer(Integer productId, Integer customerId);
}
