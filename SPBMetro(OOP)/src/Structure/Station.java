package Structure;

import java.util.Objects;

/**
 * Class for storing information about the station.
 */
public class Station {
    private int line;
    private String nameOfStation;
    private boolean status;

    public Station(String nameOfStation, int line) {
        this.nameOfStation = nameOfStation;
        this.line = line;
        this.status = true;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getLine() {
        return line;
    }

    public String getNameOfStation() {
        return nameOfStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return line == station.line &&
                Objects.equals(nameOfStation, station.nameOfStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, nameOfStation);
    }

    @Override
    public String toString() {
        return nameOfStation + ": " + line;
    }
}
