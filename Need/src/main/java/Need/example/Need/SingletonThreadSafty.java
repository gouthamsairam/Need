package Need.example.Need;

public class SingletonThreadSafty {

    private static SingletonThreadSafty instance;

    private SingletonThreadSafty() {}

    public static synchronized SingletonThreadSafty getInstance() {
        if (instance == null) {
            instance = new SingletonThreadSafty();
        }
        return instance;
    }
}