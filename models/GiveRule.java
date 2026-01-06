package Main.model;
/** 
 * Used to create the rules that the give action is supposed to follow in the game
 * @author Terence
 */
public class GiveRule {

    private String npc;
    private String item;
    private String message;
    private boolean endsGame;
    /** 
     * Store the rule (the character wants this item),
     * the trigger for the rule (being given the item),
     * the message that displays when the rule is triggered,
     * and check if the game should end after the item is given (such as the player running out of turns)
     * @return
     */
    public String getNpc() { return npc; }

    public String getItem() { return item; }

    public String getMessage() { return message; }

    public boolean isEndsGame() { return endsGame; }
}
