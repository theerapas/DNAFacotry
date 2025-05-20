package utils;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import grid.Tile;

public class MapGenerator {
	private static final double NOISE_SCALE = 0.3; // Controls the size of noise map
	private static final double RESOURCE_THRESHOLD = 0.7; // Controls resource density
	private static final int MIN_CLUSTER_SIZE = 2;
	private static final int MAX_CLUSTER_SIZE = 4;
	private static final int DELIVERY_ZONE_X = 47; // Delivery zone position
	private static final int DELIVERY_ZONE_Y = 47;
	private static final int DELIVERY_ZONE_WIDTH = 4;
	private static final int DELIVERY_ZONE_HEIGHT = 4;
	private static final int SAFE_ZONE_RADIUS = 10; // No resources within this radius
	private static final int TARGET_CLUSTERS_PER_TYPE = 5; // Target number of clusters for each resource type
	private static final double CLUSTER_SPAWN_CHANCE = 0.03; // Chance to spawn a cluster at a valid location

	private final Random random;
	private final int width;
	private final int height;
	private final Map<String, Integer> clusterCounts;

	public MapGenerator(int width, int height, long seed) {
		this.width = width;
		this.height = height;
		this.random = new Random(seed);
		this.clusterCounts = new HashMap<>();
		// Initialize counts for each resource type
		for (String type : new String[] { "A", "T", "G", "C" }) {
			clusterCounts.put(type, 0);
		}
	}

	public void generateResources(Tile[][] tiles) {
		double[][] noiseMap = generateNoiseMap();
		boolean[][] occupied = new boolean[width][height]; // Track placed resources

		// Collect all valid potential cluster centers
		List<int[]> validLocations = new ArrayList<>();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (!isNearDeliveryZone(x, y) && noiseMap[x][y] > RESOURCE_THRESHOLD && !occupied[x][y]) {
					validLocations.add(new int[] { x, y });
				}
			}
		}

		// Shuffle the valid locations to ensure random distribution
		for (int i = validLocations.size() - 1; i > 0; i--) {
			int j = random.nextInt(i + 1);
			int[] temp = validLocations.get(i);
			validLocations.set(i, validLocations.get(j));
			validLocations.set(j, temp);
		}

		// Place clusters at valid locations
		for (int[] location : validLocations) {
			int x = location[0];
			int y = location[1];

			if (random.nextDouble() < CLUSTER_SPAWN_CHANCE) {
				String resourceType = getLeastCommonResourceType();
				if (clusterCounts.get(resourceType) < TARGET_CLUSTERS_PER_TYPE) {
					generateCluster(tiles, occupied, x, y, resourceType);
					clusterCounts.put(resourceType, clusterCounts.get(resourceType) + 1);
				}
			}
		}
	}

	private String getLeastCommonResourceType() {
		String leastCommon = "A";
		int minCount = clusterCounts.get(leastCommon);

		for (String type : new String[] { "T", "G", "C" }) {
			int count = clusterCounts.get(type);
			if (count < minCount) {
				minCount = count;
				leastCommon = type;
			}
		}

		return leastCommon;
	}

	private boolean isNearDeliveryZone(int x, int y) {
		// Check if point is within the delivery zone itself
		if (x >= DELIVERY_ZONE_X && x < DELIVERY_ZONE_X + DELIVERY_ZONE_WIDTH && y >= DELIVERY_ZONE_Y
				&& y < DELIVERY_ZONE_Y + DELIVERY_ZONE_HEIGHT) {
			return true;
		}

		// Check if point is within safe zone radius
		int dx = Math.max(0, DELIVERY_ZONE_X - x);
		int dy = Math.max(0, DELIVERY_ZONE_Y - y);
		dx = Math.max(dx, x - (DELIVERY_ZONE_X + DELIVERY_ZONE_WIDTH - 1));
		dy = Math.max(dy, y - (DELIVERY_ZONE_Y + DELIVERY_ZONE_HEIGHT - 1));

		return Math.sqrt(dx * dx + dy * dy) < SAFE_ZONE_RADIUS;
	}

	private double[][] generateNoiseMap() {
		double[][] noiseMap = new double[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Simple Perlin-like noise using multiple octaves
				double noise = 0;
				double amplitude = 1;
				double frequency = NOISE_SCALE;
				double maxValue = 0;

				for (int i = 0; i < 4; i++) { // 4 octaves
					noise += amplitude * generateNoise(x * frequency, y * frequency);
					maxValue += amplitude;
					amplitude *= 0.5;
					frequency *= 2;
				}

				noiseMap[x][y] = noise / maxValue;
			}
		}

		return noiseMap;
	}

	private void generateCluster(Tile[][] tiles, boolean[][] occupied, int centerX, int centerY, String resourceType) {
		int clusterSize = random.nextInt(MAX_CLUSTER_SIZE - MIN_CLUSTER_SIZE + 1) + MIN_CLUSTER_SIZE;

		for (int x = -clusterSize; x <= clusterSize; x++) {
			for (int y = -clusterSize; y <= clusterSize; y++) {
				int targetX = centerX + x;
				int targetY = centerY + y;

				if (targetX < 0 || targetX >= width || targetY < 0 || targetY >= height)
					continue;
				if (x * x + y * y <= clusterSize * clusterSize) {
					if (!occupied[targetX][targetY] && !isNearDeliveryZone(targetX, targetY)
							&& random.nextDouble() < 0.8) {
						tiles[targetX][targetY].setResource(resourceType);
						occupied[targetX][targetY] = true;
					}
				}
			}
		}
	}

	private double generateNoise(double x, double y) {
		// Simple noise function
		return Math.sin(x) * Math.cos(y) * 0.5 + 0.5;
	}
}