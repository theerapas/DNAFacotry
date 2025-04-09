package logic;

public class GoalManager {
	private static GoalManager instance;

	private String targetLifeform = "HUMAN";
	private int delivered = 0;
	private int goalAmount = 3;

	private GoalManager() {
	}

	public static GoalManager getInstance() {
		if (instance == null)
			instance = new GoalManager();
		return instance;
	}

	public String getTargetLifeform() {
		return targetLifeform;
	}

	public int getDelivered() {
		return delivered;
	}

	public int getGoalAmount() {
		return goalAmount;
	}

	public boolean isGoalComplete() {
		return delivered >= goalAmount;
	}

	public void submitLifeform(String lifeform) {
		if (lifeform.equals(targetLifeform)) {
			delivered++;
			System.out.println("✅ Delivered: " + delivered + "/" + goalAmount);
		} else {
			System.out.println("❌ Wrong delivery: " + lifeform);
		}
	}
}
