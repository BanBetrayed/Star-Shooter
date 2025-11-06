import java.io.*;
import java.util.ArrayList;

/**
 * 
 * @author Davy Liu
 * @since 7/27/23
 * @version 0.1
 * 
 *          PLAYER, ENEMY, AND BASIC PROJECTILE ASSETS CREATED BY [GrafxKid] ON
 *          https://opengameart.org/content/arcade-space-shooter-game-assets
 * 
 */

public class Game {
    // Paths n stuff

    public static String soundsPath =
            System.getProperty("user.dir") + File.separator + "Sounds" + File.separator;
    public static String dataPath =
            System.getProperty("user.dir") + File.separator + "Save Data.txt";

    // Lists

    static ArrayList<Projectile> projectiles = new ArrayList<>();
    static ArrayList<Enemy> enemies = new ArrayList<>();
    static ArrayList<Powerup> powerups = new ArrayList<>();

    // Game Values

    public static double[] screenSize = {1280, 900}; // 1000 x 750
    public static double[] centerPosition = {screenSize[0] / 2, screenSize[1] / 2};

    public static double currentTime = 0;

    public static double framerate = 60;
    public static double deltaTime = 1 / framerate;

    public static boolean musicEnabled = true;

    // Scores

    public static int highestStage = 0;

    public static int highestKills = 0;
    public static int totalKills = 0;
    public static int sessionKills = 0;

    // Background Values

    public static String[] backgrounds = {"Images/Background.png", "Images/Stars - Far.png",
            "Images/Stars - Middle.png", "Images/Stars - Close.png",};
    public static double[] backgroundPositions = {0, 0, 0, 0};
    public static double[] backgroundSpeeds = {5, 12.5, 25, 50};

    public static double backgroundSpeedIncrease = 1.1;

    // Player values

    public static double[] targetPosition = {centerPosition[0], screenSize[1] * 0.25};
    public static double[] position = {centerPosition[0], -100};

    public static double angle = 0;

    public static boolean started = false;
    public static boolean decelerating = false;

    public static double playerSpeed = 400;
    public static double maxPlayerSpeed = 1000;

    public static double maxHealth = 10;
    public static double health = maxHealth;

    public static String currentConversion = "automatic conversion";
    public static ConversionStats currentConversionInfo =
            new ConversionStats("automatic conversion");

    public static double playerLerpFactor = 0;

    public static boolean dead = false;

    // Shooting values

    public static boolean shooting = false;
    public static double shootCooldown = 0;

    public static double spread = 2.5;

    public static int shots = 1; // 1
    public static int maxShots = 25;

    public static double shotsSpread = 10;

    public static double firerate = 600; // 600
    public static double bulletSpeed = 1000; // 1000

    public static double damage = 1;
    public static double shootingKnockback = 0; // 10

    // Enemy Values

    public static boolean allowEnemySpawning = false;

    public static double enemySpawnTime = 0;

    public static double enemySpawnRate = 2; // 2
    public static double finalEnemySpawnRate = 0; // 0.5
    public static double toFinalSpawnrateModifier = 0.05;

    public static int maxEnemies = 10; // 25, now 10

    // Stages

    public static int stageRequiredKills = 0;
    public static int stageKills = 0;

    public static int currentStage = 0;// 0;

    public static double stageCooldownTime = 0;
    public static double stageCooldownDuration = 10;

    // Powerup Stuff

    public static double powerupSpeed = 150;

    public static double[] powerupSpawnTimes = {15, // Conversions
            5, // Heal
            30, // Super Health
            60 * 5, // Upgrades
    };

    public static double[] powerupSpawnRates = {16, // Conversions
            5, // Heal
            29, // Super Health
            62 * 5, // Upgrades
    };

    // Misc

    public static double titleSizeMult = 2;
    public static double deadTime = -1;

    public static boolean bobMode = false;

    public static boolean inspectMode = false;

    public static void main(String[] args) throws InterruptedException, IOException {
        getData();

        // Map<String, Integer> weapons = new HashMap<>();
        // weapons.put("Laser", 100000000);
        // weapons.put("Beam", 100000000);
        // weapons.put("Rockets", 100000000);

        PennDraw.setCanvasSize((int) screenSize[0], (int) screenSize[1]);
        PennDraw.setXscale(0, screenSize[0]); // set it so it does size and position in pixels
                                              // rather than scale
        PennDraw.setYscale(0, screenSize[1]);
        PennDraw.enableAnimation(framerate);
        PennDraw.setPenColor(PennDraw.WHITE);

        // Music

        Thread musicThread = new Thread(() -> {
            while (true) {
                if (musicEnabled) {
                    if (!bobMode) {
                        SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                + File.separator + "Melody.wav");

                        if (currentStage >= 20) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Layer.wav");
                        }

                        if (currentStage >= 1) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Chords.wav");
                        }

                        if (currentStage >= 5) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Kicks.wav");
                        }

                        if (currentStage >= 10) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Snares.wav");
                        }

                        if (currentStage >= 25) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Hats 2.wav");
                        } else if (currentStage >= 15) {
                            SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                    + File.separator + "Hats.wav");
                        }


                        try {
                            Thread.sleep((int) ((((double) 60 / (double) 130) * 16) * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        SoundHandler.playSoundInBackground(soundsPath + File.separator + "Music"
                                + File.separator + "Bob the Builder.wav");

                        try {
                            Thread.sleep(35839);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        musicThread.start();

        while (true) {
            PennDraw.clear();

            // Values

            decelerating = false;
            shooting = false;
            inspectMode = false;

            shootCooldown = shootCooldown - deltaTime;

            // User Input

            if (PennDraw.isKeyPressed(32) || PennDraw.mousePressed()) {
                started = true;
                shooting = true;
            }

            if (PennDraw.isKeyPressed(97) || PennDraw.isKeyPressed(65)) { // A key
                targetPosition[0] = targetPosition[0] - (playerSpeed * deltaTime);
            }
            if (PennDraw.isKeyPressed(100) || PennDraw.isKeyPressed(68)) { // D key
                targetPosition[0] = targetPosition[0] + (playerSpeed * deltaTime);
            }

            if (PennDraw.isKeyPressed(119) || PennDraw.isKeyPressed(87)) { // W key
                targetPosition[1] = targetPosition[1] + (playerSpeed * deltaTime);
            }
            if (PennDraw.isKeyPressed(115) || PennDraw.isKeyPressed(83)) { // S key
                targetPosition[1] = targetPosition[1] - (playerSpeed * deltaTime);
                decelerating = true;
            }

            if (PennDraw.isKeyPressed(73) || PennDraw.isKeyPressed(105)) { // I key
                inspectMode = true;
            }

            // Cheat
            if (PennDraw.isKeyPressed(32) && PennDraw.mousePressed()) {
                powerups.add(new Powerup("Speed Upgrade"));
                powerups.add(new Powerup("Extra Shot Upgrade"));
                powerups.add(new Powerup("Firerate Upgrade"));
                powerups.add(new Powerup("Damage Upgrade"));
                powerups.add(new Powerup("Super Health Powerup"));
            }

            if (PennDraw.isKeyPressed(98) || PennDraw.isKeyPressed(66)) {
                if (PennDraw.isKeyPressed(79) || PennDraw.isKeyPressed(111)) {
                    bobMode = true;
                }
            }

            // Drawing

            // Background

            for (int bgIndex = 0; bgIndex < backgrounds.length; bgIndex++) {
                for (int iy = 0; iy < 3; iy++) {
                    for (int ix = 0; ix < 3; ix++) {
                        PennDraw.picture(1000 / 2 + (1000 * ix),
                                backgroundPositions[bgIndex] + (750 * iy), backgrounds[bgIndex],
                                1000, 750);
                    }
                }

                backgroundPositions[bgIndex] =
                        backgroundPositions[bgIndex] - backgroundSpeeds[bgIndex] * deltaTime;
                if (backgroundPositions[bgIndex] < 750 / -2) {
                    backgroundPositions[bgIndex] = 750 / 2;
                }
            }

            // Game Stuff
            if (started == true) {
                titleSizeMult = lerp(titleSizeMult, 0, 0.05);
                playerLerpFactor = lerp(playerLerpFactor, 0.1, 0.01);

                // Movement

                for (int i = 0; i < 2; i++) {
                    if (targetPosition[i] > screenSize[i]) {
                        targetPosition[i] = screenSize[i];
                    } else if (targetPosition[i] < 0) {
                        targetPosition[i] = 0;
                    }

                    double Factor = playerLerpFactor; // 0.1
                    Factor = Factor * ((Math.log((playerSpeed / maxPlayerSpeed) + 1) + 1));

                    position[i] = lerp(position[i], targetPosition[i], Factor);
                }

                // Sprites

                angle = lerp(angle, -((targetPosition[0] - position[0]) * 0.25), 1);
                angle = angle / (playerSpeed / 400);

                if (!dead) {
                    PennDraw.picture(position[0], position[1], "Images/Player.png", 50, 50, angle);
                    double fireRandom = (Math.random() * 25) - 12.5;
                    if (decelerating) {
                        PennDraw.picture(position[0], position[1], "Images/Flame Small.png",
                                50 + fireRandom, 100, angle);
                    } else {
                        PennDraw.picture(position[0], position[1], "Images/Flame.png",
                                50 + fireRandom, 100, angle);
                    }
                    if (bobMode) {
                        PennDraw.picture(position[0], position[1], "Images/Bob Helmet.png", 50, 50,
                                angle);
                    }
                }


                // Shooting

                if (shooting && shootCooldown <= 0 && !dead) {
                    double tempFirerate = firerate * currentConversionInfo.firerateMult;

                    shootCooldown = 60 / tempFirerate;

                    int tempShots = shots * currentConversionInfo.shotsMult;
                    double tempShotsSpread = shotsSpread * currentConversionInfo.shotsSpreadMult;

                    for (int i = 1; i <= tempShots; i++) {
                        double thisShotSpread = lerp(-(tempShotsSpread / 2), tempShotsSpread / 2,
                                ((double) i) / ((double) (tempShots + 1)));
                        if (tempShots <= 1) {
                            thisShotSpread = 0;
                        }

                        thisShotSpread = thisShotSpread + (((Math.random() * spread * 2) - spread)
                                * currentConversionInfo.spreadMult);

                        double thisDamage = damage * currentConversionInfo.damageMult;
                        thisDamage = thisDamage / shots;

                        Projectile newProjectile = new Projectile(position,
                                bulletSpeed * currentConversionInfo.projectileSpeedMult,
                                angle + thisShotSpread, currentConversionInfo.projectileType, true,
                                thisDamage);
                        projectiles.add(newProjectile);

                    }

                    // Misc shooting effects

                    SoundHandler
                            .playSoundInBackground(soundsPath + File.separator + "Laser Shoot.wav");
                    targetPosition[1] = targetPosition[1] - shootingKnockback;
                }

                // Handle Projectiles

                for (int projectileIndex = 0; projectileIndex < projectiles
                        .size(); projectileIndex++) {
                    Projectile thisProjectile = projectiles.get(projectileIndex);

                    if ((thisProjectile.position[0] > screenSize[0])
                            || (thisProjectile.position[1] > screenSize[1])) {
                        projectiles.remove(projectileIndex);
                    } else {
                        for (int i = 0; i < 2; i++) {
                            thisProjectile.position[i] = thisProjectile.position[i]
                                    + (thisProjectile.velocity[i] * deltaTime);
                        }
                        PennDraw.picture(thisProjectile.position[0], thisProjectile.position[1],
                                "Images/" + thisProjectile.type + ".png", thisProjectile.visualSize,
                                thisProjectile.visualSize, thisProjectile.angle);
                    }
                }

                // Spawning enemies

                // Spawns enemies in 5 second intervials
                if (

                (enemySpawnTime < currentTime) && (allowEnemySpawning)
                        && (enemies.size() < (stageRequiredKills - stageKills)))

                {

                    enemySpawnTime = currentTime + enemySpawnRate * ((Math.random() * 0.125) + 0.9);

                    if (enemies.size() < maxEnemies) {
                        double enemyHealth = 3 + (currentStage / 10);
                        enemyHealth = Math.round(enemyHealth);

                        Enemy tempEnemy = new Enemy("Basic Enemy", "top strafer", enemyHealth);
                        enemies.add(tempEnemy);

                        int bossChance = (int) (Math.random() * 50);
                        if (bossChance == 0 && (currentStage >= 5)) {
                            tempEnemy.hitboxSize = tempEnemy.hitboxSize * 2;
                            tempEnemy.health = enemyHealth * 5;
                            tempEnemy.maxHealth = enemyHealth * 5;
                            tempEnemy.fireRate = 300;
                        }

                        String enemyType =
                                Enemy.allTypes[(int) (Math.random() * Enemy.allTypes.length)];
                        if (enemyType.toLowerCase().equals("Sharpshooter Enemy".toLowerCase())) {
                            tempEnemy.type = enemyType;
                            tempEnemy.projectileSpeed = 1500;
                            tempEnemy.projectileSpread = 0;
                            tempEnemy.fireRate = tempEnemy.fireRate / 2;
                        } else if (enemyType.toLowerCase().equals("Tank Enemy".toLowerCase())) {
                            tempEnemy.type = enemyType;
                            tempEnemy.maxHealth = tempEnemy.maxHealth * 2;
                            tempEnemy.health = tempEnemy.maxHealth;
                            tempEnemy.moveSpeed = tempEnemy.moveSpeed / 2;
                            tempEnemy.movingInterval = tempEnemy.movingInterval * 2;
                        }
                    }
                }

                // enemies

                for (Enemy thisEnemy : enemies) {
                    // Rendering

                    if (thisEnemy.lastMoved < currentTime) {
                        thisEnemy.lastMoved = thisEnemy.lastMoved + thisEnemy.movingInterval;
                        thisEnemy.moveEnemy();
                    }

                    for (int i = 0; i <= 1; i++) {
                        thisEnemy.position[i] = lerp(thisEnemy.position[i],
                                thisEnemy.targetPosition[i], thisEnemy.moveSpeed);
                    }

                    double[] positionDifference = new double[2];
                    for (int i = 0; i <= 1; i++) {
                        positionDifference[i] = position[i] - thisEnemy.position[i];
                    }

                    PennDraw.text(thisEnemy.position[0], thisEnemy.position[1] - 50,
                            roundTo(thisEnemy.health, 10) + " / "
                                    + roundTo(thisEnemy.maxHealth, 10));

                    double toPlayerAngle = Math
                            .toDegrees(Math.atan2(positionDifference[1], positionDifference[0]));
                    thisEnemy.angle = lerp(thisEnemy.angle, toPlayerAngle, 0.05);

                    PennDraw.picture(thisEnemy.position[0], thisEnemy.position[1],
                            "Images/" + thisEnemy.type + ".png", thisEnemy.hitboxSize * 2,
                            thisEnemy.hitboxSize * 2, thisEnemy.angle - 90);

                    // Shooting

                    if (thisEnemy.lastFired < currentTime && !dead) {
                        thisEnemy.lastFired = currentTime + (60 / thisEnemy.fireRate);

                        double projectileSpread = (Math.random() * (thisEnemy.projectileSpread * 2))
                                - thisEnemy.projectileSpread;

                        Projectile newProjectile = new Projectile(thisEnemy.position,
                                thisEnemy.projectileSpeed, (toPlayerAngle - 90) + projectileSpread,
                                "laser", false, thisEnemy.damage);
                        projectiles.add(newProjectile);

                        SoundHandler.playSoundInBackground(
                                soundsPath + File.separator + "Laser Shoot.wav");
                    }

                }

                // Power ups

                // Spawning Conversions
                if (powerupSpawnTimes[0] < currentTime) {
                    powerupSpawnTimes[0] = currentTime + powerupSpawnRates[0];

                    String powerupType =
                            Powerup.conversions[(int) (Math.random() * Powerup.conversions.length)];
                    Powerup newPowerup = new Powerup(powerupType);
                    powerups.add(newPowerup);
                }

                // Spawning Health
                if (powerupSpawnTimes[1] < currentTime) {
                    powerupSpawnTimes[1] = currentTime + powerupSpawnRates[1];

                    Powerup newPowerup = new Powerup("Health Powerup");
                    powerups.add(newPowerup);
                }

                // Spawning Super Health
                if (powerupSpawnTimes[2] < currentTime) {
                    powerupSpawnTimes[2] = currentTime + powerupSpawnRates[2];

                    Powerup newPowerup = new Powerup("Super Health Powerup");
                    powerups.add(newPowerup);
                }

                // Spawning Upgrades
                if (powerupSpawnTimes[3] < currentTime) {
                    powerupSpawnTimes[3] = currentTime + powerupSpawnRates[3];

                    String[] allUpgrades = {"Damage Upgrade", "Speed Upgrade", "Firerate Upgrade"};

                    Powerup newPowerup =
                            new Powerup(allUpgrades[(int) (Math.random() * allUpgrades.length)]);
                    powerups.add(newPowerup);
                }

                // rendering
                for (int i = 0; i < powerups.size(); i++) {
                    Powerup thisPowerup = powerups.get(i);
                    thisPowerup.position[1] = thisPowerup.position[1] - (powerupSpeed * deltaTime);
                    PennDraw.picture(thisPowerup.position[0], thisPowerup.position[1],
                            "Images/" + thisPowerup.type + ".png", 100, 100);
                    PennDraw.text(thisPowerup.position[0], thisPowerup.position[1] - 50,
                            thisPowerup.type);
                    if (thisPowerup.position[1] < -64) {
                        powerups.remove(i);
                    }
                }

                // Player Display

                PennDraw.text(centerPosition[0], 30, "Stage: " + currentStage);
                PennDraw.text(centerPosition[0], 10, "" + stageKills + " / " + stageRequiredKills);

                if (!dead) {
                    PennDraw.text(position[0], position[1] - 50,
                            ((int) health) + " / " + ((int) maxHealth));

                }

                if (inspectMode) { // TODO: ADD MORE VALUES
                    PennDraw.text(centerPosition[0], centerPosition[1] - 50,
                            "---------- Player Stats ----------");
                    PennDraw.text(centerPosition[0], centerPosition[1] - 75,
                            "Damage: " + damage + " (base dmg) / " + shots + " (num shots) = "
                                    + roundTo((damage / (double) shots), 100) + " dmg");
                    PennDraw.text(centerPosition[0], centerPosition[1] - 100,
                            "Base Firerate: " + (int) firerate + " rpm");
                    PennDraw.text(centerPosition[0], centerPosition[1] - 125,
                            "Player Speed: " + (int) playerSpeed + " / " + (int) maxPlayerSpeed);
                    PennDraw.text(centerPosition[0], centerPosition[1] - 150,
                            "Shots: " + shots + " / " + maxShots);


                    PennDraw.text(centerPosition[0], centerPosition[1] - 200,
                            "---------- Spawn Times ----------");
                    PennDraw.text(centerPosition[0], centerPosition[1] - 225,
                            "Time until Heal Spawns: "
                                    + roundTo(powerupSpawnTimes[1] - currentTime, 10) + " s");
                    PennDraw.text(centerPosition[0], centerPosition[1] - 250,
                            "Time until Conversion Spawns: "
                                    + roundTo(powerupSpawnTimes[0] - currentTime, 10) + " s");



                    PennDraw.text(centerPosition[0] + 350, centerPosition[1] - 50,
                            "---------- Stage Values ----------");
                    PennDraw.text(centerPosition[0] + 350, centerPosition[1] - 75,
                            "Enemy Spawnrate: " + enemySpawnRate + " s");
                    PennDraw.text(centerPosition[0] + 350, centerPosition[1] - 100,
                            "Max Enemies: " + maxEnemies);

                    String spawnsOnStageCompletion = "";
                    if (currentStage % 2 == 0) {
                        spawnsOnStageCompletion = spawnsOnStageCompletion + " Super Health,";
                    }
                    if (currentStage % 5 == 0) {
                        spawnsOnStageCompletion = spawnsOnStageCompletion + " Basic Upgrade,";
                    }
                    if (currentStage % 10 == 0) {
                        spawnsOnStageCompletion = spawnsOnStageCompletion + " Extra Shot Upgrade,";
                    }
                    if (currentStage % 15 == 0) {
                        spawnsOnStageCompletion = spawnsOnStageCompletion + " Damage Upgrade,";
                    }
                    if (spawnsOnStageCompletion.length() > 0) {
                        spawnsOnStageCompletion = spawnsOnStageCompletion.substring(0,
                                spawnsOnStageCompletion.length() - 1);
                    } else {
                        spawnsOnStageCompletion = " Nothing";
                    }

                    PennDraw.text(centerPosition[0] + 0, centerPosition[1] - 300,
                            "Spawns on Stage Completion:" + spawnsOnStageCompletion);



                    PennDraw.text(centerPosition[0] - 350, centerPosition[1] - 50,
                            "---------- Conversion Info ----------");
                    PennDraw.text(centerPosition[0] - 350, centerPosition[1] - 75,
                            "Current Conversion: " + currentConversion);
                    PennDraw.text(centerPosition[0] - 350, centerPosition[1] - 125,
                            "Damage Multiplier: " + roundTo(currentConversionInfo.damageMult, 100)
                                    + "x");
                    PennDraw.text(centerPosition[0] - 350, centerPosition[1] - 150,
                            "Firerate Multiplier: "
                                    + roundTo(currentConversionInfo.firerateMult, 100) + "x");
                    PennDraw.text(centerPosition[0] - 350, centerPosition[1] - 175,
                            "Shots Multiplier: " + currentConversionInfo.shotsMult + "x");

                }

                if (stageCooldownTime > currentTime
                        && (stageCooldownTime - currentTime > (stageCooldownDuration - 1))
                        && currentStage > 1) {
                    PennDraw.text(centerPosition[0], centerPosition[1],
                            "Stage " + (currentStage - 1) + " complete!");
                } else if (stageCooldownTime > currentTime) {
                    PennDraw.text(centerPosition[0], centerPosition[1],
                            "" + ((int) ((stageCooldownTime - currentTime) + 1)));
                } else if (stageCooldownTime + 1 > currentTime) {
                    PennDraw.text(centerPosition[0], centerPosition[1], "Stage " + currentStage);
                } else if (stageCooldownTime + 3 > currentTime) {
                    PennDraw.text(centerPosition[0], centerPosition[1],
                            "Kill " + stageRequiredKills + " enemies");
                } else if (stageCooldownTime + 8 > currentTime) {
                    if (currentStage == 5) {
                        PennDraw.text(centerPosition[0], centerPosition[1],
                                "Boss enemies will start spawning");
                    }
                }

                // hit reg

                for (int projectileIndex = 0; projectileIndex < projectiles
                        .size(); projectileIndex++) {
                    Projectile thisProjectile = projectiles.get(projectileIndex);

                    if (thisProjectile.players == true) {
                        // Player's projectile
                        for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {
                            Enemy thisEnemy = enemies.get(enemyIndex);

                            boolean hit =
                                    hitDetection(thisProjectile.position, thisProjectile.hitboxSize,
                                            thisEnemy.position, thisEnemy.hitboxSize);
                            if (hit) {
                                projectiles.remove(thisProjectile);
                                thisEnemy.health = thisEnemy.health - thisProjectile.damage;
                                if (thisEnemy.health <= 0) {
                                    stageKills++;
                                    enemies.remove(enemyIndex);

                                    totalKills++;
                                    sessionKills++;
                                    if (sessionKills > highestKills) {
                                        highestKills = sessionKills;
                                    }

                                    System.out.println(highestStage + " - " + highestKills + " - "
                                            + totalKills);

                                    saveData();
                                }
                            }
                        }
                    } else {
                        // Enemy's Projectile (watch out!)
                        boolean hit = hitDetection(thisProjectile.position,
                                thisProjectile.hitboxSize, position, 25);
                        if (hit) {
                            health = health - thisProjectile.damage;
                            projectiles.remove(thisProjectile);
                            if (health <= 0) {
                                dead = true;
                                deadTime = currentTime;
                            }
                        }
                    }

                }

                for (int powerupIndex = 0; powerupIndex < powerups.size(); powerupIndex++) {
                    Powerup thisPowerup = powerups.get(powerupIndex);

                    boolean hit = hitDetection(thisPowerup.position, 25, position, 15);
                    if (hit) {
                        thisPowerup.use();
                        powerups.remove(powerupIndex);
                        SoundHandler
                                .playSoundInBackground(soundsPath + File.separator + "Powerup.wav");
                    }
                }

                // Levels / Stages

                if (stageKills >= stageRequiredKills) {
                    currentStage = currentStage + 1;

                    stageKills = 0;
                    stageRequiredKills = 5 + ((int) (2.5 * ((double) currentStage - 1)));

                    stageCooldownTime = currentTime + stageCooldownDuration;

                    enemySpawnRate = 2 - (0.1 * (currentStage - 1));
                    if (enemySpawnRate < finalEnemySpawnRate) {
                        enemySpawnRate = finalEnemySpawnRate;
                    }

                    for (int i = 0; i < backgroundSpeeds.length; i++) {
                        backgroundSpeeds[i] = backgroundSpeeds[i] * backgroundSpeedIncrease;
                    }

                    if (currentStage > 1) {
                        if ((currentStage - 1) % 2 == 0) {
                            powerups.add(new Powerup("Super Health Powerup"));
                        }

                        if ((currentStage - 1) % 5 == 0) {
                            String randomUpgrade = Powerup.upgrades[(int) (Math.random()
                                    * ((double) Powerup.upgrades.length))];
                            powerups.add(new Powerup(randomUpgrade));
                        }

                        if ((currentStage - 1) % 15 == 0) {
                            powerups.add(new Powerup("Damage Upgrade"));
                        }

                        if ((currentStage - 1) % 10 == 0) {
                            powerups.add(new Powerup("Extra Shot Upgrade"));
                        }
                    }

                    enemySpawnRate = 2 - ((currentStage - 1) * toFinalSpawnrateModifier);
                    if (enemySpawnRate < finalEnemySpawnRate) {
                        enemySpawnRate = finalEnemySpawnRate;
                    }

                    if (currentStage > highestStage) {
                        highestStage = currentStage;
                        saveData();
                    }

                    maxEnemies = 10 + (currentStage - 1);

                    System.out.println("You have completed stage " + (currentStage - 1)
                            + ", you are now on stage " + currentStage);
                    System.out.println("kills required: " + stageRequiredKills);
                    System.out.println("");
                }

                if (stageCooldownTime < currentTime) {
                    allowEnemySpawning = true;
                } else {
                    allowEnemySpawning = false;
                }


            } else {
                titleSizeMult = lerp(titleSizeMult, 0.5, 0.05);
            }

            // Scoreboard

            PennDraw.text(80, screenSize[1] - 20, "Highest Stage: " + highestStage);
            PennDraw.text(55, screenSize[1] - 40, "Total Kills: " + totalKills);
            PennDraw.text(65, screenSize[1] - 60, "Highest Kills: " + highestKills);

            if (titleSizeMult > 0.01 && dead == false && started == false) {
                PennDraw.picture(centerPosition[0], centerPosition[1], "Images/Title Image.png",
                        1920 * titleSizeMult, 1080 * titleSizeMult, Math.sin(currentTime) * 5);
            }

            if (dead == true) {
                titleSizeMult = lerp(titleSizeMult, 1, 0.05);

                if (deadTime + 5 > currentTime) {
                    PennDraw.picture(centerPosition[0], centerPosition[1],
                            "Images/Death Message.png", 1920 * titleSizeMult, 1080 * titleSizeMult,
                            Math.sin(currentTime) * 5);
                } else {
                    System.out.println("RESTART");

                    currentConversion = "automatic conversion";
                    currentConversionInfo = new ConversionStats("automatic conversion");

                    targetPosition[0] = centerPosition[0];
                    targetPosition[1] = screenSize[1] * 0.25;

                    position[0] = centerPosition[0];
                    position[1] = -100;

                    maxHealth = 10;
                    health = 10;

                    damage = 1;

                    shots = 1;
                    shotsSpread = 10;

                    firerate = 600;
                    playerSpeed = 400;

                    backgroundSpeeds[0] = 5;
                    backgroundSpeeds[1] = 12.5;
                    backgroundSpeeds[2] = 25;
                    backgroundSpeeds[3] = 50;

                    projectiles.clear();
                    enemies.clear();
                    powerups.clear();

                    started = false;
                    dead = false;
                    deadTime = -1;

                    stageRequiredKills = 0;
                    stageKills = 0;
                    currentStage = 0;

                    titleSizeMult = 2;
                }
            }

            currentTime = currentTime + deltaTime;
            PennDraw.advance();
        }
    }

    public static double lerp(double a, double b, double x) {
        return a + (b - a) * x;
    }

    public static boolean hitDetection(double[] position1, double magnitude1, double[] position2,
            double magnitude2) {
        boolean tooFar = false;
        for (int i = 0; i <= 1; i++) {
            double magnitude = Math.abs(position1[i] - position2[i]);
            if (magnitude > (magnitude2 + magnitude1)) {
                tooFar = true;
            }
        }
        return !tooFar;
    }

    public static double roundTo(double number, double spot) {
        return ((double) Math.floor(number * spot) / spot);
    }

    public static void getData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(dataPath))) {
            int[] data = {0, 0, 0};

            int startLine = 0;
            int lineNumber = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                if (lineNumber >= startLine) {
                    System.out.println("Line " + lineNumber + ": " + line);
                }
                lineNumber++;

                /*
                 * 
                 * 0 = Highest Stage 1 = Highest Kills 2 = Total Kills
                 * 
                 */

                data[lineNumber - 1] = Integer.parseInt(line);
            }

            highestStage = data[0];
            highestKills = data[1];
            totalKills = data[2];

            System.out.println(highestStage + " - " + highestKills + " - " + totalKills);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData() throws IOException {
        Writer output =
                new FileWriter(System.getProperty("user.dir") + File.separator + "Save Data.txt");
        output.write(highestStage + "\n" + highestKills + "\n" + totalKills);
        output.close();
    }
}
