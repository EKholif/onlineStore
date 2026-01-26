package frontEnd.question;

public class VoteResult {
    private String message;
    private int voteCount;

    public VoteResult(String message, int voteCount) {
        this.message = message;
        this.voteCount = voteCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
