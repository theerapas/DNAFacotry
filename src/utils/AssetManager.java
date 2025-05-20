package utils;

import javafx.scene.image.Image;

public class AssetManager {
	private static final ClassLoader classLoader = AssetManager.class.getClassLoader();

	public static Image conveyor = load("conveyor.png");
	public static Image conveyorCurveBottomToRight = load("curve_bottom_to_right_conveyor.png");
	public static Image conveyorCurveBottomToLeft = load("curve_bottom_to_left_conveyor.png");
	public static Image extractorA = load("extractor_a.png");
	public static Image extractorT = load("extractor_t.png");
	public static Image extractorG = load("extractor_g.png");
	public static Image extractorC = load("extractor_c.png");
	public static Image resourceA = load("resource_a.png");
	public static Image resourceT = load("resource_t.png");
	public static Image resourceG = load("resource_g.png");
	public static Image resourceC = load("resource_c.png");
	public static Image deliveryZone = load("delivery.png");
	public static Image dnaCombiner = load("dna_combiner.png");
	public static Image lifeformAssembler = load("lifeform_assembler.png");
	public static Image nucleotideA = load("nucleotide_a.png");
	public static Image nucleotideT = load("nucleotide_t.png");
	public static Image nucleotideG = load("nucleotide_g.png");
	public static Image nucleotideC = load("nucleotide_c.png");
	public static Image traitBlood = load("trait_blood.png");
	public static Image traitMuscle = load("trait_muscle.png");
	public static Image traitBrain = load("trait_brain.png");
	public static Image lifeformHuman = load("lifeform_human.png");
	public static Image lifeformOctopus = load("lifeform_octopus.png");
	public static Image lifeformWorm = load("lifeform_worm.png");
	public static Image tunnel = load("tunnel.png");
	public static Image tunnel_exit = load("tunnel_exit.png");
	public static Image dnaSynthesizer1 = load("dna_synthesizer1.png");
	public static Image dnaSynthesizer2 = load("dna_synthesizer2.png");
	public static Image dnaSynthesizer3 = load("dna_synthesizer3.png");
	public static Image ribosome1 = load("ribosome1.png");
	public static Image ribosome2 = load("ribosome2.png");
	public static Image enzyme = load("enzyme.png");
	public static Image antibody = load("antibody.png");
	public static Image organSynthesizer = load("organ_synthesizer.png");
	public static Image organBrain = load("organ_brain.png");
	public static Image organHeart = load("organ_heart.png");
	public static Image organLungs = load("organ_lungs.png");

	private static Image load(String filename) {
		return new Image(classLoader.getResourceAsStream("assets/images/" + filename));
	}

	private static Image load(String filename, double w, double h) {
		return new Image(classLoader.getResourceAsStream("assets/images/" + filename), w, h, true, true);
	}

}
