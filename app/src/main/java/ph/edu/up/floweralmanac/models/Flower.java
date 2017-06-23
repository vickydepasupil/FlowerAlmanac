package ph.edu.up.floweralmanac.models;


public class Flower{

    private int id;
    private String name;
    private String ease;
    private String instructions;
    private String rev;

    public Flower() { }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getEase() { return ease; }

    public String getInstructions() { return instructions; }

    public String getRev() { return rev; }

    public void setId(int uid) { this.id = uid; }

    public void setName(String name) { this.name = name; }

    public void setEase(String ease) { this.ease = ease; }

    public void setInstructions(String instructions) { this.instructions = instructions; }

    public void setRev(String rev) { this.rev = rev; }


}
