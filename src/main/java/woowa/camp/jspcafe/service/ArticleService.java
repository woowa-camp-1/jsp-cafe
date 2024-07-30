package woowa.camp.jspcafe.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import woowa.camp.jspcafe.domain.Article;
import woowa.camp.jspcafe.domain.User;
import woowa.camp.jspcafe.domain.exception.ArticleException;
import woowa.camp.jspcafe.domain.exception.UserException;
import woowa.camp.jspcafe.repository.article.ArticleRepository;
import woowa.camp.jspcafe.repository.dto.ArticleUpdateRequest;
import woowa.camp.jspcafe.repository.user.UserRepository;
import woowa.camp.jspcafe.service.dto.ArticleDetailsResponse;
import woowa.camp.jspcafe.service.dto.ArticlePreviewResponse;
import woowa.camp.jspcafe.service.dto.ArticleWriteRequest;
import woowa.camp.jspcafe.utils.time.DateTimeProvider;

public class ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final DateTimeProvider dateTimeProvider;

    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository,
                          DateTimeProvider dateTimeProvider) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    public Article writeArticle(ArticleWriteRequest articleWriteRequest) {
        Article article = Article.create(
                articleWriteRequest.authorId(),
                articleWriteRequest.title(),
                articleWriteRequest.content(),
                dateTimeProvider.getNow());

        articleRepository.save(article);
        return article;
    }

    public ArticleDetailsResponse findArticleDetails(Long id) {
        Article article = findArticle(id);
        upHits(article);
        article = findArticle(id);

        if (article.isAnonymousAuthor()) {
            return ArticleDetailsResponse.of(article, null, "익명");
        }

        User author = findAuthor(article.getAuthorId());
        return ArticleDetailsResponse.of(article, author.getId(), author.getNickname());
    }

    public List<ArticlePreviewResponse> findArticleList(int page) {
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Article> articles = articleRepository.findByOffsetPagination(offset, pageSize);

        List<ArticlePreviewResponse> articlePreviewRespons = new ArrayList<>();
        for (Article article : articles) {
            if (article.isAnonymousAuthor()) {
                articlePreviewRespons.add(ArticlePreviewResponse.of(article, null, "익명"));
                continue;
            }
            User author = findAuthor(article.getAuthorId());
            articlePreviewRespons.add(ArticlePreviewResponse.of(article, author.getId(), author.getNickname()));
        }

        return articlePreviewRespons;
    }

    private Article findArticle(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleException("Article not found : " + id));
    }

    private User findAuthor(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User with id " + userId + " not found"));
    }

    private void upHits(Article article) {
        article.upHits();
        articleRepository.update(article.getId(), new ArticleUpdateRequest(article.getHits()));
    }

}
