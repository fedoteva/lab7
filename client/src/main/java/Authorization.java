import console.ConsoleOutputer;
import dao.DataBaseDAO;
import exceptions.ExitException;
import interaction.User;
import utils.IdGenerator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Authorization {

    private final ConsoleOutputer o = new ConsoleOutputer();
    public static boolean isAuth = false;
    private int id;
    private String newUsername;
    private String newPassword;
    private Scanner sc;
    private DataBaseDAO dao;

    public Authorization(Scanner sc, DataBaseDAO dao) {
        this.sc = sc;
        this.dao = dao;
    }

    protected User askIfAuth(Scanner sc) {

        try {
            System.out.println("впервые тут? {Y/N}");
            String answ;

            while (true) {
                answ = sc.nextLine();
                switch (answ) {

                    case ("Y"): {
                        return newUser(dao, sc);
                    }
                    case ("N"): {
                        while(!isAuth) {
                            o.printNormal("помнишь свой id? {y/n}");
                            String s = sc.nextLine();
                            switch (s) {
                                case "y": {
                                    isAuth = true;
                                    return remembersID(dao, sc);

                                }
                                case "n": {
                                    isAuth = true;
                                    return dosentRemember(dao, sc);
                                }
                                default:
                                    isAuth = false;
                                    o.printRed("и че это значит");
                            }

                        }
                        //TODO опять же проверки по типу есть ли такой юзернейм или пароль в БД
                        isAuth = true;
                        o.printPurple("Для того чтобы начать введите команду. Чтобы увидеть список доступных команд введите help");

                        return new User(newUsername, PasswordHandler.encode(newPassword), id);
                    }
                    case "exit": {
                        List<String> input = new ArrayList<>();
                        input.add("exit");
                        ASCIIArt.ifExit(input, new ConsoleOutputer());
                    }
                    case "admin": {
                        isAuth = true;
                        o.printPurple("Для того чтобы начать введите команду. Чтобы увидеть список доступных команд введите help");
                        return new User("admin", PasswordHandler.encode("dfmjosdfo8107142827sidhfsodffsd47918741"), 0);
                    }
                    default: {
                        isAuth = false;
                        o.printRed("не авторизированным пользователям доступны только команды Y/N/exit");
                    }

                }
            }
        } catch (NoSuchElementException e) {
            throw new ExitException("ewwww you are so cringe");
        }
    }

    private User newUser(DataBaseDAO dao, Scanner sc) {
        while (true) {
            o.printNormal("придумай юзернейм");
            String username = sc.nextLine();
            if (!dao.checkUsername(username)) {
                o.printNormal("теперь пароль");
                String password = sc.nextLine();
                isAuth = true;
                dao.insertUser(new User(username, PasswordHandler.encode(password), id));

                //o.printNormal("тебе назначен id: " + dao.getUserID(username));
                o.printPurple("Для того чтобы начать введите команду. Чтобы увидеть список доступных команд введите help");

                return new User(username, PasswordHandler.encode(password), dao.getUserID(username));
            } else {
                System.out.println("такой юзернейм уже есть");
            }
        }
    }

    private User remembersID(DataBaseDAO dao, Scanner sc) {
        o.printNormal("введи id");
        while (true) {
            try {
                id = Integer.parseInt(sc.nextLine());
                if (dao.checkID(id)) {
                    try {
                        return new User(dao.getusername(id), dao.getPassword(id), id);
                    }
                    catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }
                break;
            } catch (RuntimeException e) {
                o.printRed("и че это за тип данных такой");
                e.printStackTrace();
                continue;

            }
        }
        return null;
    }

    private User dosentRemember(DataBaseDAO dao, Scanner sc) {
        while (true) {
            o.printNormal("введи юзернейм");
            newUsername = sc.nextLine();
            if (dao.checkUsername(newUsername)) {
                o.printNormal("теперь пароль");
                newPassword = sc.nextLine();
                if (!dao.checkPassword(PasswordHandler.encode(newPassword))) {
                    System.out.println("что то у тебя не соотносятся пароль и юзернейм. вспомниай");

                } else {
                    id = dao.getUserID(newUsername);
                    return new User(newUsername, newPassword, id);
                }
            } else {
                o.printRed("нет такого юзернейма");
            }
        }
    }
}
