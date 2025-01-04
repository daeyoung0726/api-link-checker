package api.link.checker.core;

public class ApiInfo {
    private String httpMethod;
    private String path;
    private String description;
    private String nickname;
    private boolean checked;

    public ApiInfo() {}

    public ApiInfo(String httpMethod, String path, String description, String nickname, boolean checked) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.description = description;
        this.nickname = nickname;
        this.checked = checked;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}