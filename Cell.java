/**
 * Represents a single cell in Conway's Game of Life simulation.
 * Each cell can be alive or dead and keeps track of its age.
 *
 * <p>Alive cells have an age of one or greater. Dead cells have an age of zero.
 * The age increases by one each generation that the cell remains alive.</p>
 */
public class Cell {

    /** Indicates whether this cell is currently alive. */
    private boolean alive;

    /** The current age of this cell. Alive cells have age >= 1, dead cells have age 0. */
    private int age;

    /**
     * Constructs a new Cell object.
     *
     * @param alive true if the cell should start alive, false otherwise
     */
    public Cell(boolean alive) {
        this.alive = alive;
        this.age = alive ? 1 : 0;
    }

    /**
     * Checks whether this cell is alive.
     *
     * @return true if the cell is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets the cell to alive if it is currently dead and resets its age to 1.
     */
    public void setAlive() {
        if (!this.alive) {
            this.alive = true;
            this.age = 1;
        }
    }

    /**
     * Returns the age of this cell.
     *
     * @return the current age of the cell
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age of this cell.
     * If the age is 0, the cell becomes dead; otherwise, it becomes alive.
     *
     * @param age the new age value (must be >= 0)
     */
    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
            this.alive = age != 0;
        }
    }

    /**
     * Resets the cell to a dead state with age 0.
     */
    public void reset() {
        this.alive = false;
        this.age = 0;
    }
}
