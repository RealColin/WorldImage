package realcolin.worldimage.util;

import java.util.Objects;

public class Pair {
    public final int x;
    public final int z;

    public Pair(int x, int z){
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        var pair = (Pair) obj;
        return x == pair.x && z == pair.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ")";
    }
}
