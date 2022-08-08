package models;

public abstract class WebAppObject {
    private int id;

    public WebAppObject(int id) {
        this.id = id;
    }

    public WebAppObject() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
