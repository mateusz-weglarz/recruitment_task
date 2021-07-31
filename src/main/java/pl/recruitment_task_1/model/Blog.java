package pl.recruitment_task_1.model;

public class Blog {

    private Long id;
    private String text;
    private Long userId;

    public Blog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", userId=" + userId +
                '}';
    }
}
