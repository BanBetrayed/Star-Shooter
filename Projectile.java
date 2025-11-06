public class Projectile {
    public String type;
    public boolean players;

    public double[] position = new double[2];
    public double[] velocity = new double[2];

    public double angle;

    public double visualSize = 50;
    public double hitboxSize = 0;

    public double damage = 1;

    public Projectile(double[] position, double velocity, double angle, String type,
            boolean players, double damage) {

        double[] projectileVelocity = new double[2];

        projectileVelocity[0] =
                -velocity * (Math.sin(Math.toRadians(angle)) / Math.sin(Math.toRadians(90)));
        projectileVelocity[1] = velocity
                * (Math.sin(Math.toRadians(180 - (90 + angle))) / Math.sin(Math.toRadians(90)));

        this.position[0] = position[0];
        this.position[1] = position[1];

        this.velocity = projectileVelocity;
        this.angle = angle;
        this.type = type;
        this.damage = damage;

        this.players = players;
    }
}
