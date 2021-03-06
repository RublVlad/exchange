package by.bsuir.exchange.command;

import by.bsuir.exchange.chain.CommandHandler;
import by.bsuir.exchange.command.exception.CommandOperationException;
import by.bsuir.exchange.entity.RoleEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;
import by.bsuir.exchange.provider.SessionAttributesNameProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Command {
    private CommandHandler handler;
    private CommandEnum tag;
    private String successPage;
    private String failurePage;
    private boolean redirect;

    public Command(CommandHandler handler, CommandEnum tag, String successPage, String failurePage, boolean redirect) {
        this.handler = handler;
        this.tag = tag;
        this.successPage = successPage;
        this.failurePage = failurePage;
        this.redirect = redirect;
    }

    public String execute(HttpServletRequest request) throws CommandOperationException {
        boolean success;
        try {
            success = handler.handle(request, tag);
        } catch (ManagerInitializationException | ManagerOperationException e) {
            throw new CommandOperationException(e);
        }

        HttpSession session = request.getSession();
        RoleEnum role = (RoleEnum) session.getAttribute(SessionAttributesNameProvider.ROLE);
        if (getTag() == CommandEnum.LOGIN && role == RoleEnum.ADMIN && success){
            return  "/controller?command=get_users";
        }

        if (!success){
            redirect = false;
        }
        return success? successPage : failurePage;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public CommandEnum getTag() {
        return tag;
    }
}
