package cab.mb.mediator.interfaces.command

/**
 * @author Mahdi Bohloul
 * @param TCommand type of the command to be handled
 */
interface CommandHandler<in TCommand : Command> {

    /**
     * @author Mahdi Bohloul
     * @param command type of the given command
     */
    fun handle(command: TCommand)
}