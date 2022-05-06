package commands;


import dao.DataBaseDAO;
import dao.RouteDAO;
import exceptions.ExitException;
import interaction.Response;
import interaction.Status;

import utils.Route;

import java.util.NoSuchElementException;

/**
 * Класс команды ADD, предназначенный для добавления элемента в коллекцию
 */
public class Add extends ACommands{
    {
        isAsker = true;
    }
    public Response execute(RouteDAO routeDAO, DataBaseDAO dbDAO) {

        try {

            Route route = new Route(info.name, info.x, info.y, info.fromX,
                    info.fromY, info.nameFrom, info.toX, info.toY, info.nameTo,
                    info.distance, user);
            route.setUser(user);
            routeDAO.create(route);
            dbDAO.create(route);

            response.msg("элемент добавлен в коллекцию").status(Status.OK);
        }
        catch (NoSuchElementException e){throw new ExitException(e.getMessage());}

        catch (NullPointerException e){
            response.msg("ошибка..." + e.getMessage()).status(Status.COLLECTION_ERROR);
            e.printStackTrace();
        }

        catch (RuntimeException e) {
            response.msg("невозможно добавить элемент в коллекцию" + e.getMessage()).status(Status.COLLECTION_ERROR);

        }
        //response.msg("я дебил даже не зашел в цикл").status(Status.SERVER_ERROR);
        //:(
        return response;
    }

    @Override
    public String toString() {
        return "Add";
    }
}
