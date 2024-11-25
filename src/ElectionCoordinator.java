import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElectionCoordinator {
    public static void main(String[] args) {
        int totalMembers = 9;
        List<CouncilMember> councilMembers = new ArrayList<>();

        // Initialize council members
        for (int i = 1; i <= totalMembers; i++) {
            councilMembers.add(new CouncilMember(i, totalMembers));
        }

        // Start council members in their threads
        councilMembers.forEach(member -> new Thread(member).start());

        // Start the election process
        System.out.println("--- Starting Election ---");

        //Single random proposal
        Random rand = new Random();
        int proposer_num = rand.nextInt(0,8);
        int candidate_num = rand.nextInt(1,9);
        CouncilMember proposer = councilMembers.get(proposer_num);
        proposer.proposeCandidate(candidate_num);

        /*
        //Simultaneous proposal (M1 & M2)
        new Thread(() -> councilMembers.get(0).proposeCandidate(1)).start();
        new Thread(() -> councilMembers.get(1).proposeCandidate(2)).start();

        // Wait for election to finish
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
