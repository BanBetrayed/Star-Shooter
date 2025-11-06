public class Powerup {
    public static String[] conversions =
            {"Shotgun Conversion", "Sniper Conversion", "Automatic Conversion",};
    public static String[] upgrades = {"Firerate Upgrade", "Speed Upgrade"};

    public String type = "";

    public double[] position = new double[2];

    public Powerup(String type) {
        this.type = type;

        // if your speed is at the max, then you get a super health instead of another speed upg
        if (type.toLowerCase().equals("Speed Upgrade".toLowerCase())
                && Game.playerSpeed >= Game.maxPlayerSpeed) {
            this.type = "Firerate Upgrade";
        }

        if (type.toLowerCase().equals("Extra Shot Upgrade".toLowerCase())) {
            if (Game.shots >= Game.maxShots) {
                this.type = "Firerate Upgrade";
            }
        }

        position[0] = Math.random() * Game.screenSize[0];
        position[0] = Game.lerp(position[0], Game.screenSize[0] / 2, 0.1);

        position[1] = Game.screenSize[1] + 64;
    }

    public void use() {
        // Conversions
        for (String thisConversion : conversions) {
            if (thisConversion.toLowerCase().equals(type.toLowerCase())) {
                Game.currentConversion = thisConversion;
                Game.currentConversionInfo = new ConversionStats(thisConversion);
            }
        }

        // health n shield
        if (type.toLowerCase().equals("health powerup".toLowerCase())) {
            Game.health = Game.health + 1;
            if (Game.health > Game.maxHealth) {
                Game.health = Game.maxHealth;
            }
        } else if (type.toLowerCase().equals("super health powerup".toLowerCase())) {
            Game.maxHealth = Game.maxHealth + 1;
            Game.health = Game.health + Game.maxHealth / 2;
            if (Game.health > Game.maxHealth) {
                Game.health = Game.maxHealth;
            }
        }

        // Upgrades
        if (type.toLowerCase().equals("Firerate Upgrade".toLowerCase())) {
            Game.firerate = Game.firerate + 50;
        } else if (type.toLowerCase().equals("Speed Upgrade".toLowerCase())) {
            Game.playerSpeed = Game.playerSpeed + 50;
            if (Game.playerSpeed > Game.maxPlayerSpeed) {
                Game.playerSpeed = Game.maxPlayerSpeed;
            }
            System.out.println(Game.playerSpeed);
        } else if (type.toLowerCase().equals("Damage Upgrade".toLowerCase())) {
            Game.damage++;
        } else if (type.toLowerCase().equals("Extra Shot Upgrade".toLowerCase())) {
            // int oldShotNum = Game.shots;

            Game.shots++;
            Game.shotsSpread = Game.shotsSpread + 5;

            // Game.damage = Game.damage * ((double) oldShotNum / (double) Game.shots);
        }
    }
}

