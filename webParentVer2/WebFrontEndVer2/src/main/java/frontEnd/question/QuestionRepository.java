package frontEnd.question;

import com.onlineStoreCom.entity.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.id = ?1")
    List<Question> findByProduct(Integer productId);

    @Query("SELECT COUNT(q.id) FROM Question q WHERE q.approved = true AND q.answer IS NOT NULL AND q.product.id = ?1")
    int countAnsweredQuestions(Integer productId);

    @Query("SELECT COUNT(q.id) FROM Question q WHERE q.approved = true AND q.product.id = ?1")
    int countByProduct(Integer productId);

    @Query("SELECT q FROM Question q WHERE q.approved = true AND q.product.alias = ?1")
    Page<Question> findByAlias(String alias, Pageable pageable);
}
