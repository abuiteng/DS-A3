import java.io.Serializable;

public class Message implements Serializable {
    private final String type;
    private final int senderId;
    private final int proposalId;
    private final int candidateId;

    public Message(String type, int senderId, int proposalId, int candidateId) {
        this.type = type;
        this.senderId = senderId;
        this.proposalId = proposalId;
        this.candidateId = candidateId;
    }

    public Message(String type, int senderId, int candidateId) {
        this(type, senderId, -1, candidateId);
    }

    public String getType() {
        return type;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getProposalId() {
        return proposalId;
    }

    public int getCandidateId() {
        return candidateId;
    }
}
