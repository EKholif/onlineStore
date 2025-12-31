package frontEnd.question;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.question.Question;
import frontEnd.product.ProductNotFoundException;
import frontEnd.product.ProductService;
import frontEnd.utilites.ControllerHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ControllerHelper controllerHelper;

    @PostMapping("/post_question/{productId}")
    public ResponseEntity<String> postQuestion(@PathVariable(name = "productId") Integer productId,
                                               @RequestParam("questionContent") String questionContent,
                                               HttpServletRequest request) {

        Customer asker = controllerHelper.getAuthenticatedCustomer(request);
        if (asker == null) {
            return new ResponseEntity<>("You must accept login to post a question.", HttpStatus.UNAUTHORIZED);
        }

        try {
            Product product = productService.findById(productId);
            questionService.saveNewQuestion(product, asker, questionContent);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error saving question: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/vote_question/{id}/{type}")
    public ResponseEntity<VoteResult> voteQuestion(@PathVariable(name = "id") Integer questionId,
                                                   @PathVariable(name = "type") String type,
                                                   HttpServletRequest request) {

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        if (customer == null) {
            return new ResponseEntity<>(new VoteResult("You must login to vote.", 0), HttpStatus.UNAUTHORIZED);
        }

        try {
            // Product check removed as it was unused and question retrieval covers it
            Question question = questionService.findById(questionId);

            if (question == null)
                return new ResponseEntity<>(new VoteResult("Question not found", 0), HttpStatus.NOT_FOUND);

            VoteResult result = questionService.vote(question, customer, type);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new VoteResult("Error voting: " + e.getMessage(), 0),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/questions/{alias}")
    public String listQuestionsByProduct(@PathVariable(name = "alias") String alias, Model model) {
        return listQuestionsByProductByPage(alias, 1, model, "askTime", "desc");
    }

    @GetMapping("/questions/{alias}/page/{pageNum}")
    public String listQuestionsByProductByPage(@PathVariable(name = "alias") String alias,
                                               @PathVariable(name = "pageNum") int pageNum,
                                               Model model,
                                               @RequestParam(name = "sortField", defaultValue = "askTime") String sortField,
                                               @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        Product product = null;
        try {
            product = productService.findByAlis(alias);
        } catch (ProductNotFoundException e) {
            return "error/404";
        }

        Page<Question> page = questionService.listQuestionsByProduct(alias, pageNum, sortField, sortDir);
        List<Question> listQuestions = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listQuestions", listQuestions);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Questions for " + product.getShortName());
        model.addAttribute("modelUrl", "/questions/" + alias + "/page/");

        long startCount = (pageNum - 1) * QuestionService.QUESTIONS_PER_PAGE + 1;
        model.addAttribute("startCount", startCount);

        long endCount = startCount + QuestionService.QUESTIONS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        model.addAttribute("endCount", endCount);

        return "product/product_questions";
    }
}
