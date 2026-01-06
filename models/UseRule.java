package Main.model;
/** 
 * Used to create the rules that the use action is supposed to follow in the game
 * @author Terence
 */
public class UseRule {
	/**
	 * obj1 = the item being used
	 * obj2 = the item being used on
	 * message = Success message 
	 * Check if doing this action will cause the game to end 
	 */
    private String obj1;
    private String obj2;
    private String message;
    private boolean endsGame;

    public String getObj1() { return obj1; }

    public String getObj2() { return obj2; }

    public String getMessage() { return message; }

    public boolean isEndsGame() { return endsGame; }
}