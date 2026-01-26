package frontEnd.question;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.question.Question;
import com.onlineStoreCom.entity.question.QuestionVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class QuestionService {

    public static final int QUESTIONS_PER_PAGE = 10;
    @Autowired
    private QuestionRepository repo;
    @Autowired
    private QuestionVoteRepository voteRepo;

    public Question findById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Question not found with ID: " + id));
    }

    public List<Question> getTop3Questions(Integer productId) {
        // Since we don't have Pageable execution implemented yet in this iteration,
        // we can fetch list and limit, or implement Pageable in repo.
        // For simplicity and matching current repo method:
        List<Question> allQuestions = repo.findByProduct(productId);
        // Sort by votes (simple sort) and limit to 3
        allQuestions.sort((q1, q2) -> Integer.compare(q2.getVotes(), q1.getVotes()));

        if (allQuestions.size() > 3) {
            return allQuestions.subList(0, 3);
        }
        return allQuestions;
    }

    public int getNumberOfAnsweredQuestions(Integer productId) {
        return repo.countAnsweredQuestions(productId);
    }

    public int getNumberOfQuestions(Integer productId) {
        return repo.countByProduct(productId);
    }

    public void saveNewQuestion(Product product, Customer asker, String content) {
        Question question = new Question();
        question.setProduct(product);
        question.setAsker(asker);
        question.setQuestionContent(content);
        question.setAskTime(new Date());
        question.setApproved(false);
        question.setVotes(0);
        question.setTenantId(product.getTenantId());
        repo.save(question);
    }

    public VoteResult vote(Question question, Customer customer, String type) {
        QuestionVote vote = voteRepo.findByQuestionAndCustomer(question.getId(), customer.getId());
        String message = "";

        if (vote != null) {
            if (vote.isUpvoted() && type.equals("up")) {
                voteRepo.delete(vote);
                question.setVotes(question.getVotes() - 1);
                message = "You have unvoted up this question.";
            } else if (vote.isDownvoted() && type.equals("down")) {
                voteRepo.delete(vote);
                question.setVotes(question.getVotes() + 1);
                message = "You have unvoted down this question.";
            } else {
                if (vote.isUpvoted() && type.equals("down")) {
                    vote.voteDown();
                    question.setVotes(question.getVotes() - 2);
                    message = "You have voted down this question.";
                } else if (vote.isDownvoted() && type.equals("up")) {
                    vote.voteUp();
                    question.setVotes(question.getVotes() + 2);
                    message = "You have voted up this question.";
                }
                voteRepo.save(vote);
            }
        } else {
            QuestionVote newVote = new QuestionVote();
            newVote.setQuestion(question);
            newVote.setCustomer(customer);

            if (type.equals("up")) {
                newVote.voteUp();
                question.setVotes(question.getVotes() + 1);
                message = "You have successfully voted up this question.";
            } else {
                newVote.voteDown();
                question.setVotes(question.getVotes() - 1);
                message = "You have successfully voted down this question.";
            }
            newVote.setTenantId(question.getTenantId()); // Inherit tenant
            voteRepo.save(newVote);
        }

        repo.save(question);
        return new VoteResult(message, question.getVotes());
    }

    public void markQuestionsVotedForProductByCustomer(List<Question> questions, Integer productId,
                                                       Integer customerId) {
        List<QuestionVote> votes = voteRepo.findByProductAndCustomer(productId, customerId);

        for (QuestionVote vote : votes) {
            Question question = vote.getQuestion();
            if (questions.contains(question)) {
                int index = questions.indexOf(question);
                Question q = questions.get(index);

                if (vote.isUpvoted())
                    q.setUpvotedByCurrentCustomer(true);
                else if (vote.isDownvoted())
                    q.setDownvotedByCurrentCustomer(true);
            }
        }
    }

    public Page<Question> listQuestionsByProduct(String alias, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, QUESTIONS_PER_PAGE, sort);
        return repo.findByAlias(alias, pageable);
    }
}
