package expressions;

public class regular {
    public static final String email = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String password = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[`~#%()_!@#$%^&+=,.?/*-])(?=\\S+$).{8,}$";

    public static final String numeric = "\\d+(?:\\.\\d+)?";
}
