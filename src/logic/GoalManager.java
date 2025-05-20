package logic;

import java.util.ArrayList;
import java.util.List;

import utils.SoundManager;

public class GoalManager {
	private static GoalManager instance;

	private static class Stage {
		String target;
		int amount;
		int delivered;

		Stage(String target, int amount) {
			this.target = target;
			this.amount = amount;
			this.delivered = 0;
		}
	}

	private List<Stage> stages;
	private int currentStageIndex = 0;

	private GoalManager() {
		initializeStages();
	}

	private void initializeStages() {
		stages = new ArrayList<>();
		stages.add(new Stage("A", 10)); // Nucleotide A
		stages.add(new Stage("ANTIBODY", 10)); // Antibody
		stages.add(new Stage("ORGAN_BRAIN", 15)); // Brain (Organ)
		stages.add(new Stage("HUMAN", 20)); // Human
		stages.add(new Stage("WORM", 20)); // Worm
//		stages.add(new Stage("MUTATED_OCTOPUS", 3)); // Mutated Octopus
	}

	public static GoalManager getInstance() {
		if (instance == null)
			instance = new GoalManager();
		return instance;
	}

	public String getTargetLifeform() {
		return stages.get(currentStageIndex).target;
	}

	public int getDelivered() {
		return stages.get(currentStageIndex).delivered;
	}

	public int getGoalAmount() {
		return stages.get(currentStageIndex).amount;
	}

	public int getCurrentStage() {
		return currentStageIndex + 1;
	}

	public int getTotalStages() {
		return stages.size();
	}

	public boolean isGoalComplete() {
		return currentStageIndex >= stages.size() - 1
				&& stages.get(currentStageIndex).delivered >= stages.get(currentStageIndex).amount;
	}

	public void submitLifeform(String lifeform) {
		Stage currentStage = stages.get(currentStageIndex);
		if (lifeform.equals(currentStage.target)) {
			currentStage.delivered++;
//			System.out.println("Stage " + (currentStageIndex + 1) + ": Delivered " + currentStage.delivered + "/"
//					+ currentStage.amount + " " + currentStage.target);

			// Check if current stage is complete
			if (currentStage.delivered >= currentStage.amount && currentStageIndex < stages.size() - 1) {
				currentStageIndex++;
//				System.out.println("Stage " + currentStageIndex + " complete! Moving to stage "
//						+ (currentStageIndex + 1) + ": " + getTargetLifeform());
				SoundManager.play(SoundManager.SOUND_SUCCESS);
			}
		} 
//		else {
//			System.out.println("Wrong delivery: " + lifeform + " (Expected: " + currentStage.target + ")");
//		}
	}

	public void reset() {
		currentStageIndex = 0;
		for (Stage stage : stages) {
			stage.delivered = 0;
		}
	}
}
