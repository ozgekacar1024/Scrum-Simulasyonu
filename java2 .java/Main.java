package odevler.odev2;

public class Main {

    public static void main(String[] args) {

        int teamSize = 3;
        int sprintCount = 3;

        TeamMember[] scrum = new TeamMember[5];

        scrum[0] = new ProductOwner(teamSize, sprintCount);
        scrum[1] = new ScrumMaster(teamSize, sprintCount);

        for(int i=2; i < scrum.length; i++)
            scrum[i] = new Developer(teamSize, String.format("Developer%d", (i-1)), sprintCount);

        for(TeamMember member : scrum)
            member.start();

    }

}

