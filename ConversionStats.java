public class ConversionStats {
    public String projectileType = "Laser";

    public double firerateMult = 1;
    public double damageMult = 1;
    public double spreadMult = 1;

    public double projectileSpeedMult = 1;

    public int shotsMult = 1;
    public double shotsSpreadMult = 1;

    public ConversionStats(String conversionType) {
        if (conversionType.toLowerCase().equals("shotgun conversion")) {
            firerateMult = (double) 1 / (double) 3;
            damageMult = 1;
            spreadMult = 0;
            projectileSpeedMult = 0.75;

            shotsMult = 3;
            shotsSpreadMult = 2;
        } else if (conversionType.toLowerCase().equals("sniper conversion")) {
            firerateMult = (double) 1 / (double) 3;
            damageMult = 3;
            spreadMult = 0;
            projectileSpeedMult = 2;
            projectileType = "Strong Laser";
        }

    }
}

