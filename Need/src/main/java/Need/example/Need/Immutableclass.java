package Need.example.Need;

import java.util.ArrayList;
import java.util.List;

public final class Immutableclass {

    private final int id;
    private final String name;
    private final List<String> subjects;

    public Immutableclass(int id, String name, List<String> subjects) {

        this.id = id;
        this.name = name;

        // Defensive Copy
        this.subjects = new ArrayList<>(subjects);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Return a copy
    public List<String> getSubjects() {
        return new ArrayList<>(subjects);
    }
}