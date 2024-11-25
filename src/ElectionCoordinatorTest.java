import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ElectionCoordinatorTest {
    static List<CouncilMember> councilMembers = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        int totalMembers = 9;

        // Initialize council members
        for (int i = 1; i <= totalMembers; i++) {
            councilMembers.add(new CouncilMember(i, totalMembers));
        }

        // Start council members in their threads
        councilMembers.forEach(member -> new Thread(member).start());
    }

    @Test
    public void testSimultaneousProposals() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Start the election process
        System.out.println("--- Starting Election ---");

        // Simulate simultaneous proposals
        new Thread(() -> councilMembers.get(0).proposeCandidate(1)).start();
        new Thread(() -> councilMembers.get(1).proposeCandidate(2)).start();

        // Wait for election to finish
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = outContent.toString();
        System.setOut(System.out);

        // Validate that only one candidate is elected
        assertTrue(output.contains("Candidate 1 has been elected president!")
                || output.contains("Candidate 2 has been elected president!"));
    }

    @Test
    public void testImmediateResponses() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Start council members in their threads
        councilMembers.forEach(member -> new Thread(member).start());

        // Start the election process
        System.out.println("--- Starting Election ---");
        CouncilMember proposer = councilMembers.get(0); // M1 starts the election
        proposer.proposeCandidate(1); // Propose M1 as the candidate

        // Wait for election to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String output = outContent.toString();
        System.setOut(System.out);

        // Validate election completion
        assertTrue(output.contains("has been elected president!"));
    }
}
