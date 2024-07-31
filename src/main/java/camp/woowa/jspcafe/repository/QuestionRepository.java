package camp.woowa.jspcafe.repository;

import camp.woowa.jspcafe.model.Question;

import java.util.List;

public interface QuestionRepository {
    Long save(String title, String content, String writer, Long writerId);

    List<Question> findAll();

    Question findById(Long id);

    void deleteAll();

    void update(Question target);

    void deleteById(Long id);
}
