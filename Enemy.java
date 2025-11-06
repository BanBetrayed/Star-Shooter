public class Enemy {
    public static String[] allTypes = {"Basic Enemy", "Basic Enemy", "Basic Enemy", "Basic Enemy",
            "Basic Enemy", "Basic Enemy", "Basic Enemy", "Basic Enemy",

            "Sharpshooter Enemy", "Sharpshooter Enemy",

            "Tank Enemy", "Tank Enemy",};

    public String type;
    public String behavior;

    public double maxHealth;
    public double health;

    public double[] targetPosition = new double[2];
    public double[] position = new double[2];
    public double angle = 0;

    public double lastMoved = 0;
    public double movingInterval = 1;
    public double moveSpeed = 0.05;

    public double hitboxSize = 25;

    public double damage = 1;

    public double fireRate = 30; // 30
    public String projectileType = "laser";
    public double lastFired = 0;

    public double projectileSpread = 15; // 15
    public double projectileSpeed = 1000;

    public Enemy(String type, String behavior, double health) {
        this.type = type;
        this.behavior = behavior;

        this.maxHealth = health;
        this.health = health;

        this.movingInterval = (Math.random() * 1) + 1;
        this.lastMoved = Game.currentTime + this.movingInterval;
        this.lastFired = Game.currentTime + (Math.random());

        if (behavior.toLowerCase().equals("top strafer")) {
            this.moveSpeed = 0.025; // 0.025

            this.position[0] = Game.lerp((Math.random() * (double) Game.screenSize[0]),
                    ((double) Game.screenSize[0]) / 2, 0.125);
            this.position[1] = Game.screenSize[1] + 250;

            this.targetPosition[0] = this.position[0];
            this.targetPosition[1] = Game.screenSize[1] * 0.85;

            // moveEnemy();

        }
    }

    public void moveEnemy() {
        if (this.behavior.toLowerCase().equals("top strafer")) {
            this.targetPosition[1] = ((Game.screenSize[1] * 0.85) * ((Math.random() * 0.125) + 1));
            this.targetPosition[0] = Game.lerp((Math.random() * (double) Game.screenSize[0]),
                    ((double) Game.screenSize[0]) / 2, 0.125);
        }
    }
}
