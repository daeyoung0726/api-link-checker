package api.link.checker.core;

public class ApiInfo {
    private String httpMethod;
    private String path;
    private String description;
    private boolean checked;

    public ApiInfo() {}

    public ApiInfo(String httpMethod, String path, String description, boolean checked) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.description = description;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}