package edu.stanford.bmir.protege.web.shared.nohrdbmappings;

import java.io.Serializable;
import java.util.Objects;

public class NohrColumnsTable implements Serializable, Comparable<NohrColumnsTable> {

    private String tableCol;

    private String columnCol;

    private Boolean isFloatingCol;

    private Integer tableNumber;

    public NohrColumnsTable() {
    }

    public NohrColumnsTable(String tableCol, String columnCol, Boolean isFloatingCol) {
        this.tableCol = tableCol;
        this.columnCol = columnCol;
        this.isFloatingCol = isFloatingCol;
        this.tableNumber = -1;
    }

    public Integer getNumber() {
        return tableNumber;
    }

    public void setNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableNumber() {
        if(tableNumber < 1)
            return "";
        return "t"+tableNumber;
    }

    public String getTableWithNumber() {
        return tableCol + " as t" + tableNumber;
    }

    public String getTableCol() {
        return tableCol;
    }

    public void setTableCol(String tableCol) {
        this.tableCol = tableCol;
    }

    public String getColumnCol() {
        return columnCol;
    }

    public void setColumnCol(String columnCol) {
        this.columnCol = columnCol;
    }

    public Boolean getFloatingCol() {
        return isFloatingCol;
    }

    public void setFloatingCol(Boolean floatingCol) {
        isFloatingCol = floatingCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrColumnsTable)) return false;
        NohrColumnsTable that = (NohrColumnsTable) o;
        return Objects.equals(tableCol, that.tableCol) &&
                Objects.equals(columnCol, that.columnCol) &&
                Objects.equals(isFloatingCol, that.isFloatingCol) &&
                Objects.equals(tableNumber, that.tableNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableCol, columnCol, isFloatingCol, tableNumber);
    }

    @Override
    public int compareTo(NohrColumnsTable nohrColumnsTable) {
        return 0;
    }
}
