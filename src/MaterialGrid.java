

public class MaterialGrid {

    public static final int VACUUM = 0;
    public static final int SIO2 = 1;
    public static final int HFO2 = 2;
    public static final int GOLD = 3;

    private int[][] material;

    public MaterialGrid(int nx, int ny) {
        material = new int[nx][ny];

        // Java initializes every cell to 0 (VACUUM).
    }
    public MaterialGrid(int[][] grid){
        material = grid;
    }

    public int getMaterial(int i, int j) {
        return material[i][j];
    }

    public void setMaterial(int i, int j, int id) {
        material[i][j] = id;
    }
}