import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CouncilMember implements Runnable {
    private final int id;
    private final int totalMembers;
    private final Map<Integer, String> memberAddresses;
    private final ServerSocket serverSocket;
    private int currentProposal = -1;
    private int highestPromisedProposal = -1;
    private int highestAcceptedProposal = -1;
    private int acceptedCandidate = -1;
    private int voteCount = 0;
    private int accepted = 0;
    private boolean electionComplete = false;
    private boolean acceptedComplete = false;

    public CouncilMember(int id, int totalMembers) {
        this.id = id;
        this.totalMembers = totalMembers;
        this.memberAddresses = new ConcurrentHashMap<>();
        try {
            this.serverSocket = new ServerSocket(8000 + id);
        } catch (IOException e) {
            throw new RuntimeException("Error initializing server socket for Member " + id, e);
        }
        System.out.println("Member " + id + " has been initialized");
        initializeMemberAddresses();
    }

    private void initializeMemberAddresses() {
        for (int i = 1; i <= totalMembers; i++) {
            memberAddresses.put(i, "localhost:" + (8000 + i));
        }
    }

    public void proposeCandidate(int candidateId) {
        currentProposal = (int) generateProposalNumber();
        voteCount = 0;
        electionComplete = false;
        System.out.println("Member " + id + " has proposed Member " + candidateId + " for president (proposal " + currentProposal + ")");
        broadcastMessage(new Message("PREPARE", id, currentProposal, candidateId));
    }

    private void broadcastMessage(Message message) {
        memberAddresses.forEach((memberId, address) -> {
            if (memberId != id) { // Don't send to self
                sendMessage(memberId, message);
            }
        });
    }

    private void sendMessage(int memberId, Message message) {
        String[] parts = memberAddresses.get(memberId).split(":");
        try (Socket socket = new Socket(parts[0], Integer.parseInt(parts[1]));
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
        } catch (IOException e) {
            System.err.println("Error sending message from Member " + id + " to Member " + memberId);
        }
    }

    private void handlePrepare(Message message) {
        if(message.getProposalId() > currentProposal) {
            currentProposal = message.getProposalId();
        }
        System.out.println("Member " + id + " has received proposal " + message.getProposalId());
        if (message.getProposalId() > highestPromisedProposal) {
            System.out.println("Member " + id + " votes for proposal " + message.getProposalId());
            highestPromisedProposal = message.getProposalId();
            sendMessage(message.getSenderId(), new Message("PROMISE", id, highestPromisedProposal, message.getCandidateId()));
        }
    }

    private void handlePromise(Message message) {
        if(!electionComplete) {
            // If proposer, count promises for current proposal
            if (currentProposal == message.getProposalId()) {
                System.out.println("Member " + id + " received a PROMISE from Member " + message.getSenderId());
                voteCount++;
                if (voteCount > (9 / 2)) { // Majority: 5 out of 9
                    electionComplete = true;
                    System.out.println("Candidate " + message.getCandidateId() + " has received enough votes");
                    acceptedCandidate = message.getCandidateId();
                    accepted = 0;
                    acceptedComplete = false;
                    broadcastMessage(new Message("ACCEPT_REQUEST", id, currentProposal, message.getCandidateId()));
                }
            }
        }
    }

    private void handleAcceptRequest(Message message) {
        if (message.getProposalId() >= highestPromisedProposal) {
            highestPromisedProposal = message.getProposalId();
            highestAcceptedProposal = message.getProposalId();
            acceptedCandidate = message.getCandidateId();
            sendMessage(message.getSenderId(), new Message("ACCEPTED", id, highestAcceptedProposal, acceptedCandidate));
        }
    }

    private void handleAccepted(Message message) {
        if(!acceptedComplete){
            if (currentProposal == message.getProposalId()) {
                System.out.println("Member " + message.getSenderId() + " acknowledged acceptance for Candidate " + message.getCandidateId());
                accepted++;
                if (accepted > (9 / 2)) { // Majority: 5 out of 9
                    acceptedComplete = true;
                    System.out.println("Proposal " + currentProposal + " has passed. Candidate " + message.getCandidateId() + " has been elected president!");
                }
            }
        }
    }

    private long generateProposalNumber() {
        long timestamp = System.currentTimeMillis(); // Current time in milliseconds
        return timestamp * totalMembers + id;       // Combine timestamp with member ID
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = serverSocket.accept();
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                Message message = (Message) in.readObject();
                switch (message.getType()) {
                    case "PREPARE" -> handlePrepare(message);
                    case "PROMISE" -> handlePromise(message);
                    case "ACCEPT_REQUEST" -> handleAcceptRequest(message);
                    case "ACCEPTED" -> handleAccepted(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error in Member " + id + " server loop.");
            }
        }
    }
}
