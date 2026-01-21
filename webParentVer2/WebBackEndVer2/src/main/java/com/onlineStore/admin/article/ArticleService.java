package com.onlineStore.admin.article;

import com.onlineStore.admin.article.paging.PagingAndSortingHelper;
import com.onlineStoreCom.entity.articals.Article;
import com.onlineStoreCom.entity.articals.ArticleType;
import com.onlineStoreCom.entity.exception.ArticleNotFoundException;
import com.onlineStoreCom.entity.users.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ArticleService {
    public static final int ARTICLES_PER_PAGE = 5;

    @Autowired
    private ArticleRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, ARTICLES_PER_PAGE, repo);
    }

    public List<Article> listAll() {
        return repo.findPublishedArticlesWithIDAndTitleOnly();
    }

    public List<Article> listArticlesForMenu() {
        return repo.findByTypeOrderByTitle(ArticleType.MENU_BOUND);
    }

    public void save(Article article, User user) {
        setDefaultAlias(article);

        article.setUpdatedTime(new Date());
        article.setUser(user);
        article.setTenantId(user.getTenantId());

        repo.save(article);
    }

    private void setDefaultAlias(Article article) {
        if (article.getAlias() == null || article.getAlias().isEmpty()) {
            article.setAlias(article.getTitle().replaceAll(" ", "-"));
        }
    }

    public Article get(Integer id) throws ArticleNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ArticleNotFoundException("Could not find any article with ID " + id);
        }
    }

    public void updatePublishStatus(Integer id, boolean published) throws ArticleNotFoundException {
        if (!repo.existsById(id)) {
            throw new ArticleNotFoundException("Could not find any article with ID " + id);
        }
        repo.updatePublishStatus(id, published);
    }

    public void delete(Integer id) throws ArticleNotFoundException {
        if (!repo.existsById(id)) {
            throw new ArticleNotFoundException("Could not find any article with ID " + id);
        }
        repo.deleteById(id);
    }
}
