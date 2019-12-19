package by.bsuir.exchange.chain;

import by.bsuir.exchange.command.CommandEnum;
import by.bsuir.exchange.manager.exception.ManagerInitializationException;
import by.bsuir.exchange.manager.exception.ManagerOperationException;

import javax.servlet.http.HttpServletRequest;

/**
 * The interface Command handler is a core interface of controller's part of the application.
 * The interface was used to divide the responsibilities into smaller parts to make the overall
 * application more modular. All components form a sequence of handlers.
 */
@FunctionalInterface
public interface CommandHandler {

    /**
     * The handle() method is a general method for processing a command
     *
     * @param request is used to pass the results to the next stage and access the results from th previous
     * @param command the command is used to determine what operation should be performed
     * @return the boolean which is used to determine whether or not the command was processed with success
     * @throws ManagerInitializationException if the manager was not initialized properly
     * @throws ManagerOperationException      if the manager fails to perform an operation
     */
    boolean handle(HttpServletRequest request, CommandEnum command) throws ManagerInitializationException,
                                                                            ManagerOperationException;

    /**
     * The chain() method is used to impose order of components execution.
     *
     * @param other The component which should handle the command after this instance
     * @return new CommandHandler which is a queue of components to chain
     */
    default CommandHandler chain(CommandHandler other){
        return (request, command) -> CommandHandler.this.handle(request, command) && other.handle(request, command);
    }

    /**
     * The branch() method is used to provide conditions in performing commands.
     *
     * @param condition the condition
     * @param success   The command handler which should be executed after this if the condition is satisfied
     * @return new command handler representing the condition in processing the commands
     */
    default CommandHandler branch(CommandHandler condition, CommandHandler success){
        return (request, command) -> {
            boolean status = condition.handle(request, command);
            return status? success.handle(request, command):CommandHandler.this.handle(request, command) ;
        };
    }
}
