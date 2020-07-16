package edu.stanford.bmir.protege.web.shared.nohrdbmappings;

import java.io.Serializable;
import java.util.Objects;

public class NohrTablesTable implements Serializable, Comparable<NohrTablesTable> {

    private String table;

    private Integer tableNumber;

    private String column;

    private String joinTable;

    private Integer tableJoinNumber;

    private String onColumn;

    public NohrTablesTable() {
    }

    public NohrTablesTable(String table, String column, String joinTable, String onColumn) {
        this.table = table;
        this.column = column;
        this.joinTable = joinTable;
        this.onColumn = onColumn;
        this.tableJoinNumber = -1;
        this.tableNumber = -1;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Integer getNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableNumber() {
        if(tableNumber < 1)
            return "";
        return "t" + tableNumber;
    }

    public String getTableWithNumber() {
        return table + " as t" + tableNumber;
    }

    //----------------------------------------------------------

    public Integer getJoinNumber() {
        return tableJoinNumber;
    }

    public void setJoinNumber(Integer tableJoinNumber) {
        this.tableJoinNumber = tableJoinNumber;
    }

    public String getTableJoinNumber() {
        if(tableJoinNumber < 1)
            return "";
        return "t" + tableJoinNumber;
    }

    public String getJoinTableWithNumber() {
        return joinTable + " as t"+tableJoinNumber;
    }

    //----------------------------------------------------------

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getOnColumn() {
        return onColumn;
    }

    public void setOnColumn(String onColumn) {
        this.onColumn = onColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrTablesTable)) return false;
        NohrTablesTable that = (NohrTablesTable) o;
        return Objects.equals(table, that.table) &&
                Objects.equals(tableNumber, that.tableNumber) &&
                Objects.equals(column, that.column) &&
                Objects.equals(joinTable, that.joinTable) &&
                Objects.equals(tableJoinNumber, that.tableJoinNumber) &&
                Objects.equals(onColumn, that.onColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, tableNumber, column, joinTable, tableJoinNumber, onColumn);
    }

    @Override
    public int compareTo(NohrTablesTable nohrTablesTable) {
        return 0;
    }
}
